package com.codereligion.hammock.compiler.model.api;

public interface Closure {
    
    ClosureName getName();
    
    boolean isPredicate();

    Name getParameterType();
    
    Name getReturnType();
    
    boolean isNullsafe();

}
