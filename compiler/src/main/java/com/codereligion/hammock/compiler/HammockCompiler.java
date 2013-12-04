package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functional;
import com.codereligion.hammock.compiler.model.api.Closure;
import com.codereligion.hammock.compiler.model.api.ClosureName;
import com.codereligion.hammock.compiler.model.api.Name;
import com.codereligion.hammock.compiler.model.api.Type;
import com.codereligion.hammock.compiler.model.simple.BaseClosure;
import com.codereligion.hammock.compiler.model.simple.SimpleType;
import com.codereligion.hammock.compiler.model.simple.StringClosureName;
import com.codereligion.hammock.compiler.model.simple.StringName;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static javax.lang.model.element.ElementKind.METHOD;

@SupportedAnnotationTypes("com.codereligion.hammock.Functional")
public class HammockCompiler extends AbstractProcessor {

    private final Set<ElementKind> supported = Sets.immutableEnumSet(
            METHOD
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claimed = false;

        final LoadingCache<TypeElement, Type> cache = CacheBuilder.newBuilder().build(new CacheLoader<TypeElement, Type>() {

            @Override
            public Type load(TypeElement typeElement) throws Exception {
                return new SimpleType(new StringName(typeElement.getQualifiedName() + "_"));
            }

        });

        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (supported.contains(element.getKind())) {
                    final ExecutableElement method = (ExecutableElement) element;
                    final TypeElement typeElement = (TypeElement) method.getEnclosingElement();

                    if (check(method)) {
                        try {
                            parse(method, cache.get(typeElement));
                        } catch (ExecutionException e) {
                            throw new IllegalStateException(e);
                        }
                        claimed = true;
                    }
                } else {
                    error(element, element.getKind() + " is not supported");
                }
            }
        }

        for (Type type : cache.asMap().values()) {
            write(type);
        }

        return claimed;
    }

    private void write(Type type) {
        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();

        try {
            final ClassLoader loader = HammockCompiler.class.getClassLoader();
            thread.setContextClassLoader(loader);

            final MustacheFactory factory = new DefaultMustacheFactory();
            final Mustache mustache = factory.compile("templates/template.mustache");

            try {
                final Filer filer = processingEnv.getFiler();
                final JavaFileObject file = filer.createSourceFile(type.getName().getQualified());

                try (Writer writer = file.openWriter()) {
                    mustache.execute(writer, type).flush();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } finally {
            thread.setContextClassLoader(original);
        }
    }

    private boolean check(ExecutableElement method) {
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        if (modifiers.contains(Modifier.STATIC)) {
            error(method, "static not supported");
            return false;
        }

        if (!parameters.isEmpty()) {
            error(method, "too many arguments");
            return false;
        }

        if (method.getReturnType().getKind() == TypeKind.VOID) {
            error(method, "can't apply to void-methods");
            return false;
        }

        return true;
    }

    private void parse(ExecutableElement method, Type type) {
        final TypeElement typeElement = (TypeElement) method.getEnclosingElement();

        final Functional functional = method.getAnnotation(Functional.class);
        final ClosureName name = new StringClosureName(method.getSimpleName().toString());
        final Name parameterType = new StringName(typeElement.getQualifiedName().toString());

        final Closure closure;
        if (method.getReturnType().getKind() == TypeKind.BOOLEAN) {
            closure = new BaseClosure(name, parameterType, functional.nullsafe());
        } else {
            final Name returnType = new StringName(method.getReturnType().toString());
            closure = new BaseClosure(name, parameterType, returnType, functional.nullsafe());
        }

        type.getClosures().add(closure);
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
