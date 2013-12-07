package com.codereligion.hammock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Functor {

    String name() default "";
    
    boolean graceful() default false;
    
    boolean nullTo() default false;

}
