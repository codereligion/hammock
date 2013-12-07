package com.codereligion.hammock.compiler.model;

import java.util.ArrayList;
import java.util.List;

public final class ClosureBuilder {

    ClosureName name;
    final Argument input;
    String delegate;
    final ClosureName method;
    final List<Argument> arguments = new ArrayList<>();
    final Name returnType;
    boolean isStatic = false;
    boolean nullsafe = true;

    public ClosureBuilder(Argument input, ClosureName method, Name returnType) {
        this.input = input;
        this.delegate = input.getName();
        this.method = method;
        this.returnType = returnType;
    }

    public ClosureBuilder(Argument input, ClosureName method) {
        this(input, method, Closure.BOOLEAN);
    }
    
    public void withName(ClosureName name) {
        this.name = name;
    }
    
    public void withDelegate(String delegate) {
        this.delegate = delegate;
    }
    
    public void withArgument(Argument argument) {
        this.arguments.add(argument);
    }
    
    public void withStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    public void withNullsafe(boolean nullsafe) {
        this.nullsafe = nullsafe;
    }
    
    public Closure build() {
        return new Closure(this);
    }
    
}
