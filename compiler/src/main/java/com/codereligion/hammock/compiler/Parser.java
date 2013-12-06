package com.codereligion.hammock.compiler;

import com.codereligion.hammock.compiler.model.Type;
import com.google.common.base.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface Parser {
    
    void check(Element element) throws UnsupportedUsageException;

    void parse(Element element, Function<TypeElement, Type> storage);

}
