package com.codereligion.hammock.compiler.model;

import com.codereligion.hammock.Input;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class Argument {

    private final Name type;
    private final String name;
    private final boolean input;

    public Argument(VariableElement element) {
        this.type = new Name(element.asType());
        this.name = element.getSimpleName().toString();
        this.input = element.getAnnotation(Input.class) != null;
    }

    public Argument(VariableElement element, boolean input) {
        this.type = new Name(element.asType());
        this.name = element.getSimpleName().toString();
        this.input = input;
    }

    public Argument(TypeElement type, String name) {
        this.type = new Name(type);
        this.name = name;
        this.input = false;
    }

    public Name getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isInput() {
        return input;
    }

    enum IsInput implements Predicate<Argument> {

        INSTANCE;

        @Override
        public boolean apply(Argument input) {
            return input.isInput();
        }

    }

    enum To implements Function<Argument, String> {

        PARAMETER {
            @Override
            public String apply(Argument input) {
                return input.getType().getSimple() + " " + input.getName();
            }

        },

        NAME {
            @Override
            public String apply(Argument input) {
                return input.getName();
            }

        }

    }

}
