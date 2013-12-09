package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functor;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.lang.reflect.Method;
import java.util.List;

enum MethodFilter implements Predicate<ExecutableElement> {

    OBJECT {

        @Override
        public boolean apply(ExecutableElement method) {
            final Functor annotation = method.getAnnotation(Functor.class);
            final List<? extends VariableElement> parameters = method.getParameters();
            
            for (Method m : Object.class.getDeclaredMethods()) {
                final Invokable<?, Object> invokable = Invokable.from(m);
                
                final boolean isPrivate = invokable.isPrivate();
    
                if (isPrivate) {
                    continue;
                }
    
                final String name = annotation == null || annotation.name().isEmpty() ? 
                        method.getSimpleName().toString() : annotation.name();
                
                final boolean isNotSameName = !name.equals(invokable.getName());
    
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
                final String left = lefts.get(i).asType().toString();
                final String right = rights.get(i).getType().getRawType().getName();
                
                final boolean typesDontMatch = !left.equals(right);
    
                if (typesDontMatch) {
                    return false;
                }
            }
    
            return true;
        }
        
    },
    
    VOID {
        
        @Override
        public boolean apply(ExecutableElement method) {
            return method.getReturnType().getKind() == TypeKind.VOID;
        }
        
    }

}
