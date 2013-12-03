package com.codereligion.hammock.compiler;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.METHOD;

@SupportedAnnotationTypes("com.codereligion.hammock.Functional")
public class FunctionalCompiler extends AbstractProcessor {

    private final Set<ElementKind> supported = Sets.immutableEnumSet(
            METHOD
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (supported.contains(element.getKind())) {
                    parse(element);
                } else {
                    error(element, element.getKind() + " is not supported");
                }
            }
        }

        return true;
    }

    private void parse(Element element) {
        final ExecutableElement method = (ExecutableElement) element;

        check(method);

        final Types types = processingEnv.getTypeUtils();

        final TypeElement classElement = (TypeElement) element.getEnclosingElement();
        final PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

        final GeneratedClass generatedClass = new GeneratedClass();
        final String className = classElement.getSimpleName().toString();

        generatedClass.setPackageName(packageElement.getQualifiedName().toString());
        generatedClass.setSimpleSourceName(className);

        final Element returnType = types.asElement(method.getReturnType());

        final GeneratedMethod generatedMethod;

        if (returnType.asType().getKind() == TypeKind.BOOLEAN) {
            generatedMethod = new GeneratedPredicate();
        } else {
            GeneratedFunction function = new GeneratedFunction();
            function.setSimpleTargetName(returnType.getSimpleName().toString());
            function.setFullyQualifiedTargetName(((PackageElement) returnType.getEnclosingElement()).getQualifiedName().toString());
            generatedMethod = function;
        }

        generatedMethod.setName(method.getSimpleName().toString());
        generatedClass.getMethods().add(generatedMethod);

        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();
        
        try {
            final ClassLoader loader = FunctionalCompiler.class.getClassLoader();
            thread.setContextClassLoader(loader);

            final MustacheFactory factory = new DefaultMustacheFactory();
            final Mustache mustache = factory.compile("templates/template.mustache");

            try {
                final Filer filer = processingEnv.getFiler();
                final JavaFileObject file = filer.createSourceFile(classElement.getQualifiedName() + "_", element);

                try (Writer writer = file.openWriter()) {
                    mustache.execute(writer, generatedClass).flush();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } finally {
            thread.setContextClassLoader(original);
        }
    }

    private void check(ExecutableElement method) {
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        if (modifiers.contains(Modifier.STATIC)) {
            error(method, "static not supported");
        } else {
            switch (parameters.size()) {
                case 0: {
                    break;
                }
                default: {
                    error(method, "too many arguments");
                }
            }
        }
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
