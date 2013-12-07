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
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class MethodParser implements Parser {

    @Override
    public void check(Element element) throws UnsupportedUsageException {
        final ExecutableElement method = (ExecutableElement) element;
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        final boolean returnsVoid = method.getReturnType().getKind() == TypeKind.VOID;

        if (returnsVoid) {
            throw new UnsupportedUsageException(method, "void methods are not supported");
        }

        final boolean isStatic = modifiers.contains(Modifier.STATIC);

        if (isStatic && parameters.isEmpty()) {
            throw new UnsupportedUsageException(method, "too few arguments");
        }
        
        if (parameters.size() > 1 && isNotAnyAnnotatedWithInput(parameters)) {
            throw new UnsupportedUsageException(method, "multiple parameters require one @Input");
        }
        
        if (parameters.size() > 1 && isMoreThanOneAnnotatedWithInput(parameters)) {
            throw new UnsupportedUsageException(method, "illegal usage of @Input");
        }

        final boolean isObjectMethod = isObjectMethod(method, parameters);

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

    private boolean isObjectMethod(ExecutableElement method, List<? extends VariableElement> parameters) {
        for (Method m : Object.class.getDeclaredMethods()) {
            final Invokable<?, Object> invokable = Invokable.from(m);
            final boolean isPrivate = invokable.isPrivate();

            if (isPrivate) {
                continue;
            }

            final boolean isNotSameName = !method.getSimpleName().toString().equals(invokable.getName());

            if (isNotSameName) {
                continue;
            }

            final boolean isDifferentNumberOfArguments = parameters.size() != invokable.getParameters().size();

            if (isDifferentNumberOfArguments) {
                continue;
            }

            final boolean typesMatch = typesMatch(parameters, invokable.getParameters());

            if (typesMatch) {
                return true;
            }
        }

        return false;
    }

    private boolean typesMatch(List<? extends VariableElement> lefts, ImmutableList<Parameter> rights) {
        for (int i = 0; i < lefts.size(); i++) {
            final VariableElement left = lefts.get(i);
            final Parameter right = rights.get(i);

            final boolean typesMatch = left.asType().toString().equals(right.getClass().getName());

            if (typesMatch) {
                continue;
            }

            return false;
        }

        return true;
    }

    @Override
    public void parse(Element element, Function<TypeElement, Type> storage) {
        final ExecutableElement method = (ExecutableElement) element;
        final TypeElement typeElement = (TypeElement) method.getEnclosingElement();
        final Functor annotation = method.getAnnotation(Functor.class);
        final ClosureName delegate = new ClosureName(method.getSimpleName().toString());;

        final ClosureName name;
        
        if (annotation.name().isEmpty()) {
            name = delegate;
        } else {
            name = new ClosureName(annotation.name());
        }

        final List<? extends VariableElement> parameters = method.getParameters();
        final boolean isStatic = method.getModifiers().contains(Modifier.STATIC);

        final Argument input;

        if (isStatic) {
            input = findInput(parameters);
        } else {
            input = new Argument(typeElement, "input");
        }
        
        final ClosureBuilder builder;

        if (method.getReturnType().getKind() == TypeKind.BOOLEAN) {
            builder = new ClosureBuilder(input, delegate);
        } else {
            final Name returnType = new Name(method.getReturnType().toString());
            builder = new ClosureBuilder(input, delegate, returnType);
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
                builder.withArgument(new Argument(parameter, isInput || isOnlyParameter));
            }
        } else {
            for (VariableElement parameter : parameters) {
                builder.withArgument(new Argument(parameter));
            }
        }

        final Closure closure = builder.build();

        final Type type = storage.apply(typeElement);
        Preconditions.checkNotNull(type, "No type found for %s", typeElement);
        type.getClosures().add(closure);
    }

    private Argument findInput(List<? extends VariableElement> parameters) {
        if (parameters.size() == 1) {
            final VariableElement firstParameter = parameters.get(0);
            return new Argument(firstParameter, true);
        } else {
            for (VariableElement parameter : parameters) {
                if (parameter.getAnnotation(Input.class) != null) {
                    return new Argument(parameter);
                }
            }
        }
            
        throw new AssertionError();
    }

}
