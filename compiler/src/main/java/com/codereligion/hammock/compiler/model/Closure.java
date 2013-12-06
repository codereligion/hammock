package com.codereligion.hammock.compiler.model;

public final class Closure {
    
    public static final Name BOOLEAN = new Name("boolean");

    private final ClosureName name;
    private final ClosureName delegate;
    private final Name parameterType;
    private final Name returnType;
    private final boolean isStatic;
    private final boolean nullsafe;

    public Closure(ClosureName name, ClosureName delegate, Name parameterType, Name returnType, boolean isStatic, boolean nullsafe) {
        this.name = name;
        this.delegate = delegate;
        this.parameterType = parameterType;
        this.returnType = returnType;
        this.isStatic = isStatic;
        this.nullsafe = nullsafe;
    }

    public Closure(ClosureName name, ClosureName delegate, Name parameterType, boolean isStatic, boolean nullsafe) {
        this(name, delegate, parameterType, BOOLEAN, isStatic, nullsafe);
    }

    public final ClosureName getName() {
        return name;
    }

    public ClosureName getDelegate() {
        return delegate;
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

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isNullsafe() {
        return nullsafe;
    }
    
}
