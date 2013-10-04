package com.codereligion.hammock.compiler;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import java.util.ArrayList;
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

    private void check(ExecutableElement method) {
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        if (modifiers.contains(Modifier.STATIC)) {
            switch (parameters.size()) {
                case 0: {
                    error(method, "not enough arguments");
                    break;
                }
                case 1: {
                    break;
                }
                default: {
                    error(method, "too many arguments");
                }
            }
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
    
    private void parse(Element element) {
        final ExecutableElement method = (ExecutableElement) element;
        
        check(method);

        final Types types = processingEnv.getTypeUtils();
        final List<? extends VariableElement> parameters = method.getParameters();
        final Element returnType = types.asElement(method.getReturnType());
        final String name = method.getSimpleName().toString();

        final String visibility = getVisibility(method);

        final Function<VariableElement, String> function = new Function<VariableElement, String>() {

            @Override
            public String apply(VariableElement input) {
                final Name type = types.asElement(input.asType()).getSimpleName();
                final Name name = input.getSimpleName();
                return type + " " + name;
            }

        };

        final Joiner joiner = Joiner.on(' ');
        final String params = joiner.join(Iterables.transform(parameters, function));

        final List<String> signature = new ArrayList<>();

        if (!visibility.isEmpty()) {
            signature.add(visibility);
        }

        if (method.getModifiers().contains(Modifier.STATIC)) {
            signature.add("static");
        }

        signature.add(returnType.getSimpleName().toString());
        signature.add(name + "(" + params + ")");

        System.out.println(method.getEnclosingElement());
        System.out.println(joiner.join(signature));
    }

    private String getVisibility(ExecutableElement method) {
        if (method.getModifiers().contains(Modifier.PRIVATE)) {
            return "private";
        } else if (method.getModifiers().contains(Modifier.PROTECTED)) {
            return "protected";
        } else if (method.getModifiers().contains(Modifier.PUBLIC)) {
            return "public";
        } else {
            return "";
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
