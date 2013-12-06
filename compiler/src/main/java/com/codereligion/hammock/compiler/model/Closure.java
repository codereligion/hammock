package com.codereligion.hammock.compiler.model;

public final class Closure {
    
    public static final Name BOOLEAN = new Name("boolean");

    private final ClosureName name;
    private final Name parameterType;
    private final Name returnType;
    private final boolean nullsafe;

    public Closure(ClosureName name, Name parameterType, Name returnType, boolean nullsafe) {
        this.name = name;
        this.parameterType = parameterType;
        this.returnType = returnType;
        this.nullsafe = nullsafe;
    }

    public Closure(ClosureName name, Name parameterType, boolean nullsafe) {
        this(name, parameterType, BOOLEAN, nullsafe);
    }

    public final ClosureName getName() {
        return name;
    }

    public boolean isPredicate() {
        return BOOLEAN.equals(returnType);
    }

    public final Name getParameterType() {
        return parameterType;
    }

    public final Name getReturnType() {
        return returnType;
    }

    public boolean isNullsafe() {
        return nullsafe;
    }
    
}
