package com.codereligion.hammock.compiler.model;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.codereligion.hammock.compiler.model.Argument.IsInput;
import static com.codereligion.hammock.compiler.model.Argument.To;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;

public final class Closure {
    
    static final Name BOOLEAN = new Name("boolean");

    private final ClosureName name;
    private final String delegate;
    private final ClosureName method;
    private final Argument input;
    private final List<Argument> arguments;
    private final Name returnType;
    private final boolean isStatic;
    private final boolean graceful;
    private final boolean nullTo;

    public Closure(ClosureBuilder builder) {
        this.name = builder.name;
        this.delegate = builder.delegate;
        this.method = builder.method;
        this.input = builder.input;
        this.arguments = builder.arguments;
        this.returnType = builder.returnType;
        this.isStatic = builder.isStatic;
        this.graceful = builder.graceful;
        this.nullTo = builder.nullTo;
    }

    public final ClosureName getName() {
        return name;
    }

    public String getDelegate() {
        return delegate;
    }

    public ClosureName getMethod() {
        return method;
    }

    public Argument getInput() {
        return input;
    }

    public boolean isPredicate() {
        return BOOLEAN.equals(returnType);
    }

    public final FluentIterable<Argument> getArguments() {
        return from(arguments).filter(not(IsInput.INSTANCE));
    }
    
    public boolean isStateless() {
        return getArguments().isEmpty();
    }
    
    public final String getArgumentList() {
        return Joiner.on(", ").join(getArguments().transform(To.NAME));
    }

    public final String getParameterList() {
        return Joiner.on(", ").join(getArguments().transform(To.PARAMETER));
    }

    public final String getInvocationList() {
        return Joiner.on(", ").join(from(arguments).transform(To.NAME));
    }
    
    public final Name getReturnType() {
        return returnType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isGraceful() {
        return graceful || nullTo;
    }

    public boolean isNullTo() {
        return nullTo;
    }

}
