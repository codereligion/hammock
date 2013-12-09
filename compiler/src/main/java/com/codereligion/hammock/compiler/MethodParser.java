package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.Input;
import com.codereligion.hammock.compiler.model.Argument;
import com.codereligion.hammock.compiler.model.Closure;
import com.codereligion.hammock.compiler.model.ClosureBuilder;
import com.codereligion.hammock.compiler.model.ClosureName;
import com.codereligion.hammock.compiler.model.Name;
import com.codereligion.hammock.compiler.model.Type;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MethodParser implements Parser {

    private final ProcessingEnvironment env;

    public MethodParser(ProcessingEnvironment env) {
        this.env = env;
    }

    @Override
    public void check(Element element) throws UnsupportedUsageException {
        final ExecutableElement method = (ExecutableElement) element;
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        final boolean returnsVoid = MethodFilter.VOID.apply(method);

        if (returnsVoid) {
            throw new UnsupportedUsageException(method, "void methods are not supported");
        }

        final boolean isStatic = modifiers.contains(Modifier.STATIC);

        if (isStatic) {
            if (parameters.isEmpty()) {
                throw new UnsupportedUsageException(method, "too few arguments");
            }
            
            if (parameters.size() > 1) {
                if (isNotAnyAnnotatedWithInput(parameters)) {
                    throw new UnsupportedUsageException(method, "multiple parameters require one @Input");
                }
                
                if (isMoreThanOneAnnotatedWithInput(parameters)) {
                    throw new UnsupportedUsageException(method, "illegal usage of @Input");
                }
            }
        }

        final boolean isObjectMethod = MethodFilter.OBJECT.apply(method);

        if (isObjectMethod) {
            throw new UnsupportedUsageException(method, "can't use java.lang.Object methods");
        }
    }

    private boolean isNotAnyAnnotatedWithInput(List<? extends VariableElement> parameters) {
        for (VariableElement parameter : parameters) {
            if (parameter.getAnnotation(Input.class) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isMoreThanOneAnnotatedWithInput(List<? extends VariableElement> parameters) {
        boolean found = false;

        for (VariableElement parameter : parameters) {
            if (parameter.getAnnotation(Input.class) != null) {
                if (found) {
                    return true;
                } else {
                    found = true;
                }
            }
        }

        return false;
    }

    @Override
    public void parse(Element element, Function<TypeElement, Type> storage) {
        final TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        final ExecutableElement method = (ExecutableElement) element;
        final Functor annotation = method.getAnnotation(Functor.class);

        final String defaultName = method.getSimpleName().toString();
        final ClosureName delegate = new ClosureName(defaultName);
        final ClosureName name = new ClosureName(annotation.name().isEmpty() ? defaultName : annotation.name());

        final List<? extends VariableElement> parameters = method.getParameters();
        final boolean isStatic = method.getModifiers().contains(Modifier.STATIC);

        final Argument input;

        if (isStatic) {
            input = findInput(parameters);
        } else {
            input = new Argument(box(typeElement.asType()), "input");
        }

        final ClosureBuilder builder;

        TypeMirror returnType = method.getReturnType();
        
        if (returnType.getKind() == TypeKind.BOOLEAN) {
            builder = new ClosureBuilder(input, delegate);
        } else {
            builder = new ClosureBuilder(input, delegate, new Name(box(returnType).toString()));
        }

        builder.withName(name);
        builder.withStatic(isStatic);
        builder.withGraceful(annotation.graceful());
        builder.withNullTo(annotation.nullTo());

        if (isStatic) {
            builder.withDelegate(typeElement.getSimpleName().toString());
            
            for (VariableElement parameter : parameters) {
                final boolean isInput = parameter.getAnnotation(Input.class) != null;
                final boolean isOnlyParameter = parameters.size() == 1;
                builder.withArgument(new Argument(box(parameter.asType()), parameter, isInput || isOnlyParameter));
            }
        } else {
            for (VariableElement parameter : parameters) {
                builder.withArgument(new Argument(box(parameter.asType()), parameter));
            }
        }

        final Closure closure = builder.build();

        final Type type = storage.apply(typeElement);
        Preconditions.checkNotNull(type, "No type found for %s", typeElement);
        type.getClosures().add(closure);
    }

    private TypeElement box(TypeMirror returnType) {
        final Types types = env.getTypeUtils();

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

    private Argument findInput(List<? extends VariableElement> parameters) {
        if (parameters.size() == 1) {
            final VariableElement firstParameter = parameters.get(0);
            return new Argument(box(firstParameter.asType()), firstParameter, true);
        } else {
            for (VariableElement parameter : parameters) {
                if (parameter.getAnnotation(Input.class) != null) {
                    return new Argument(box(parameter.asType()), parameter);
                }
            }
        }
            
        throw new AssertionError();
    }

}
