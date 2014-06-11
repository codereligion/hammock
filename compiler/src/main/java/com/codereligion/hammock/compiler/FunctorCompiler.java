package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.Input;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.sun.codemodel.JExpr._null;

@SupportedAnnotationTypes("com.codereligion.hammock.Functor")
public class FunctorCompiler extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean claimed = false;

        final JCodeModel model = new JCodeModel();

        for (TypeElement annotationType : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotationType)) {
                final TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                final ExecutableElement methodType = (ExecutableElement) element;

                final JDefinedClass type = getType(model, typeElement);

                final Functor annotation = methodType.getAnnotation(Functor.class);
                final String methodName;

                if (annotation.name().isEmpty()) {
                    methodName = methodType.getSimpleName().toString();
                } else {
                    methodName = annotation.name();
                }

                final TypeElement inputType = findInput(typeElement, methodType);
                final JType input = model.ref(inputType.getQualifiedName().toString());

                final JClass returnType;

                if (methodType.getReturnType().getKind() == TypeKind.BOOLEAN) {
                    returnType = model.ref(Predicate.class).narrow(input);
                } else {
                    final JClass inputRef = model.ref(input.fullName());
                    final JClass outputRef = model.ref(box(methodType.getReturnType()).getQualifiedName().toString());
                    returnType = model.ref(Function.class).narrow(inputRef, outputRef);
                }

                final JMethod method = type.method(JMod.PUBLIC | JMod.STATIC, returnType, methodName);

                for (VariableElement parameter : methodType.getParameters()) {
                    if (parameter.getAnnotation(Input.class) == null || (methodType.getModifiers().contains(Modifier.STATIC) && methodType.getParameters().size() > 1)) {
                        final JClass parameterType = transform(model, parameter.asType());
                        method.param(JMod.FINAL, parameterType, parameter.getSimpleName().toString());
                    }
                }

                method.body()._return(_null());

                claimed = true;
            }
        }

        final Filer filer = processingEnv.getFiler();

        try {
            model.build(new SourceCodeWriter(filer));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return claimed;
    }

    private TypeElement findInput(TypeElement typeElement, ExecutableElement methodType) {
        final List<? extends VariableElement> parameters = methodType.getParameters();

        switch (parameters.size()) {
            case 0: {
                return typeElement;
            }
            case 1: {
                final VariableElement parameter = parameters.get(0);
                return box(parameter.asType());
            }
            default: {
                for (VariableElement parameter : parameters) {
                    if (parameter.getAnnotation(Input.class) != null) {
                        return box(parameter.asType());
                    }
                }

                if (!methodType.getModifiers().contains(Modifier.STATIC)) {
                    return typeElement;
                }
            }
        }

        throw new IllegalStateException("Can't find input parameter for " + methodType);
    }
    
    private JClass transform(JCodeModel model, TypeMirror mirror) {
        if (mirror instanceof PrimitiveType) {
            return model.ref(mirror.toString());
        } else {
            final TypeElement typeElement = asTypeElement(mirror);
            final List<JClass> typeParameters = new ArrayList<>();

            for (TypeParameterElement parameterElement : getTypeParameterElements(typeElement)) {
                typeParameters.add(transform(model, parameterElement.asType()));
            }
            
            return model.ref(typeElement.getQualifiedName().toString()).narrow(typeParameters);
        }
    }

    private List<? extends TypeParameterElement> getTypeParameterElements(TypeElement typeElement) {
        if (typeElement.getTypeParameters() == null) {
            return Collections.emptyList();
        }
        
        return typeElement.getTypeParameters();
    }

    private TypeElement asTypeElement(TypeMirror mirror) {
        return (TypeElement) processingEnv.getTypeUtils().asElement(mirror);
    }

    private TypeElement box(TypeMirror returnType) {
        final Types types = processingEnv.getTypeUtils();

        final String name = returnType.toString();
        switch (name) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "char":
            case "boolean":
                final TypeKind kind = TypeKind.valueOf(name.toUpperCase(Locale.ENGLISH));
                return types.boxedClass(types.getPrimitiveType(kind));
            default:
                return (TypeElement) types.asElement(returnType);
        }
    }

    private JDefinedClass getType(JCodeModel model, TypeElement typeElement) {
        final Element parent = typeElement.getEnclosingElement();
        final boolean isTopLevel = parent instanceof PackageElement;

        if (isTopLevel) {
            final String typeName = toQualifiedName(typeElement);
            final JDefinedClass type = model._getClass(typeName);

            if (type != null) {
                return type;
            }

            try {
                return model._class(typeName);
            } catch (JClassAlreadyExistsException e) {
                throw new AssertionError(e);
            }
        } else if (parent instanceof TypeElement) {
            final JDefinedClass parentType = getType(model, (TypeElement) parent);
            return getType(parentType, typeElement);
        } else {
            throw new IllegalStateException("Unknown enclosing element: " + parent);
        }
    }

    private JDefinedClass getType(JDefinedClass parent, TypeElement typeElement) {
        final String typeName = toSimpleName(typeElement);
        final Iterator<JDefinedClass> classes = parent.classes();

        while (classes.hasNext()) {
            final JDefinedClass child = classes.next();
            if (child.name().equals(typeName)) {
                return child;
            }
        }

        try {
            return parent._class(JMod.PUBLIC | JMod.STATIC, typeName);
        } catch (JClassAlreadyExistsException e) {
            throw new AssertionError(e);
        }
    }

    private String toSimpleName(TypeElement typeElement) {
        return typeElement.getSimpleName() + "_";
    }

    private String toQualifiedName(TypeElement typeElement) {
        return typeElement.getQualifiedName() + "_";
    }

    private void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
