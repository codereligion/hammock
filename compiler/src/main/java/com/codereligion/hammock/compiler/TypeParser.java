package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.compiler.model.Type;
import com.google.common.base.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.List;

public class TypeParser implements Parser {

    private final MethodParser parser = new MethodParser();
    
    @Override
    public void check(Element original) throws UnsupportedUsageException {
        final TypeElement typeElement = (TypeElement) original;

        for (ExecutableElement method : filter(typeElement)) {
            parser.check(method);
        }
    }
    
    @Override
    public void parse(Element original, Function<TypeElement, Type> storage) {
        final TypeElement typeElement = (TypeElement) original;

        for (ExecutableElement method : filter(typeElement)) {
            parser.parse(method, storage);
        }
    }

    private List<ExecutableElement> filter(TypeElement typeElement) {
        final List<ExecutableElement> methods = new ArrayList<>();

        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                final ExecutableElement method = (ExecutableElement) element;
                
                if (method.getReturnType().getKind() == TypeKind.VOID) {
                    continue;
                }

                if (method.getAnnotation(Functor.class) != null) {
                    continue;
                }
                
                methods.add(method);
            }
        }

        return methods;
    }

}
