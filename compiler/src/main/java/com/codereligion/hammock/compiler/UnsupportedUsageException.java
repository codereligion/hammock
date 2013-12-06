package com.codereligion.hammock.compiler;

import javax.lang.model.element.Element;

public class UnsupportedUsageException extends Exception {

    private final Element element;

    public UnsupportedUsageException(Element element, String message) {
        super(message);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
