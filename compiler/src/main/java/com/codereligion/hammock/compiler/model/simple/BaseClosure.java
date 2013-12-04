package com.codereligion.hammock.compiler.model.simple;

import com.codereligion.hammock.compiler.model.api.Closure;
import com.codereligion.hammock.compiler.model.api.ClosureName;
import com.codereligion.hammock.compiler.model.api.Name;

public final class BaseClosure implements Closure {
    
    public static final Name BOOLEAN = new StringName("boolean");

    private final ClosureName name;
    private final Name parameterType;
    private final Name returnType;
    private final boolean nullsafe;

    public BaseClosure(ClosureName name, Name parameterType, Name returnType, boolean nullsafe) {
        this.name = name;
        this.parameterType = parameterType;
        this.returnType = returnType;
        this.nullsafe = nullsafe;
    }

    public BaseClosure(ClosureName name, Name parameterType, boolean nullsafe) {
        this(name, parameterType, BOOLEAN, nullsafe);
    }

    @Override
    public final ClosureName getName() {
        return name;
    }

    @Override
    public boolean isPredicate() {
        return BOOLEAN.equals(returnType);
    }

    @Override
    public final Name getParameterType() {
        return parameterType;
    }

    @Override
    public final Name getReturnType() {
        return returnType;
    }

    @Override
    public boolean isNullsafe() {
        return nullsafe;
    }
    
}
