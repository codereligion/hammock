package com.codereligion.hammock.compiler;

import com.codereligion.hammock.FirstClass;
import com.codereligion.hammock.compiler.model.Closure;
import com.codereligion.hammock.compiler.model.ClosureName;
import com.codereligion.hammock.compiler.model.Name;
import com.codereligion.hammock.compiler.model.Type;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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

@SupportedAnnotationTypes("com.codereligion.hammock.FirstClass")
public class FirstClassCompiler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claimed = false;

        final LoadingCache<TypeElement, Type> cache = CacheBuilder.newBuilder().build(new CacheLoader<TypeElement, Type>() {

            @Override
            public Type load(TypeElement typeElement) throws Exception {
                return new Type(new Name(typeElement.getQualifiedName() + "_"));
            }

        });

        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                final ElementKind kind;

                try {
                    kind = parseAndCheck(element);
                } catch (UnsupportedUsageException e) {
                    error(e.getElement(), e.getMessage());
                    continue;
                }

                switch (kind) {
                    case METHOD:
                        final ExecutableElement method = (ExecutableElement) element;
                        final TypeElement typeElement = (TypeElement) method.getEnclosingElement();

                        try {
                            parse(method, cache.get(typeElement));
                        } catch (ExecutionException e) {
                            throw new IllegalStateException(e);
                        }

                        claimed = true;
                        break;
                }
            }
        }

        for (Type type : cache.asMap().values()) {
            write(type);
        }

        return claimed;
    }

    private ElementKind parseAndCheck(Element element) throws UnsupportedUsageException {
        final ElementKind kind = element.getKind();

        switch (kind) {
            case METHOD:
                final ExecutableElement method = (ExecutableElement) element;
                final Set<Modifier> modifiers = method.getModifiers();
                final List<? extends VariableElement> parameters = method.getParameters();

                final boolean isStatic = modifiers.contains(Modifier.STATIC);
                
                if (isStatic) {
                    throw new UnsupportedUsageException(method, "static not supported");
                }

                final boolean hasParameters = !parameters.isEmpty();
                
                if (hasParameters) {
                    throw new UnsupportedUsageException(method, "too many arguments");
                }

                final boolean returnsVoid = method.getReturnType().getKind() == TypeKind.VOID;
                
                if (returnsVoid) {
                    throw new UnsupportedUsageException(method, "Void methods are not supported");
                }

                return kind;
            default:
                throw new UnsupportedUsageException(element, "unsupported usage");
        }
    }

    private void write(Type type) {
        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();

        try {
            final ClassLoader loader = FirstClassCompiler.class.getClassLoader();
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

    private void parse(ExecutableElement method, Type type) {
        final TypeElement typeElement = (TypeElement) method.getEnclosingElement();

        final FirstClass annotation = method.getAnnotation(FirstClass.class);
        final ClosureName name = new ClosureName(method.getSimpleName().toString());
        final Name parameterType = new Name(typeElement.getQualifiedName().toString());

        final Closure closure;
        if (method.getReturnType().getKind() == TypeKind.BOOLEAN) {
            closure = new Closure(name, parameterType, annotation.nullsafe());
        } else {
            final Name returnType = new Name(method.getReturnType().toString());
            closure = new Closure(name, parameterType, returnType, annotation.nullsafe());
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
