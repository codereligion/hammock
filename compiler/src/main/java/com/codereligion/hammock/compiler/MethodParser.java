package com.codereligion.hammock.compiler;

import com.codereligion.hammock.FirstClass;
import com.codereligion.hammock.compiler.model.Closure;
import com.codereligion.hammock.compiler.model.ClosureName;
import com.codereligion.hammock.compiler.model.Name;
import com.codereligion.hammock.compiler.model.Type;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Set;

public class MethodParser implements Parser {

    @Override
    public void check(Element element) throws UnsupportedUsageException {
        final ExecutableElement method = (ExecutableElement) element;
        final Set<Modifier> modifiers = method.getModifiers();
        final List<? extends VariableElement> parameters = method.getParameters();

        final boolean isStatic = modifiers.contains(Modifier.STATIC);
        
        if (isStatic) {
            if (parameters.isEmpty()) {
                throw new UnsupportedUsageException(method, "static few arguments");
            }
            
            if (parameters.size() > 1) {
                throw new UnsupportedUsageException(method, "too many arguments");
            }
        } else {
            final boolean hasParameters = !parameters.isEmpty();

            if (hasParameters) {
                throw new UnsupportedUsageException(method, "too many arguments");
            }
        }

        final boolean returnsVoid = method.getReturnType().getKind() == TypeKind.VOID;
        
        if (returnsVoid) {
            throw new UnsupportedUsageException(method, "Void methods are not supported");
        }
    }

    @Override
    public void parse(Element element, Function<TypeElement, Type> storage) {
        final ExecutableElement method = (ExecutableElement) element;
        final TypeElement typeElement = (TypeElement) method.getEnclosingElement();

        final FirstClass annotation = method.getAnnotation(FirstClass.class);
        final ClosureName name = new ClosureName(method.getSimpleName().toString());
        final boolean isStatic = method.getModifiers().contains(Modifier.STATIC);
        
        final Name parameterType;
        
        if (isStatic) {
            final VariableElement firstParameter = method.getParameters().get(0);
            parameterType = new Name(firstParameter.asType().toString());
        } else {
            parameterType = new Name(typeElement.getQualifiedName().toString());
        }

        final Closure closure;
        
        if (method.getReturnType().getKind() == TypeKind.BOOLEAN) {
            closure = new Closure(name, parameterType, isStatic, annotation.nullsafe());
        } else {
            final Name returnType = new Name(method.getReturnType().toString());
            closure = new Closure(name, parameterType, returnType, isStatic, annotation.nullsafe());
        }

        final Type type = storage.apply(typeElement);
        Preconditions.checkNotNull(type, "No type found for %s", typeElement);
        type.getClosures().add(closure);
    }
    
}
