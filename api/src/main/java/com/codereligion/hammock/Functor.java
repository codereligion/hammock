package com.codereligion.hammock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows static, compile-time code generation of functionss
 * and predicates for ordinary java methods, i.e. promoting
 * them to first-class functions.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Functor {

    /**
     * Allows to override the name for the generated method which
     * defaults to the name of annotated method.
     *
     * @return the generated method name
     */
    String name() default "";

    /**
     * Allows to change the behaviour for null inputs. By default nulls
     * will be invoked/passed without any special handling. When set to
     * true null inputs will yield null or false respectively.
     *
     * @return whether nulls will be handled or passed to the method
     */
    boolean graceful() default false;

    /**
     * Allows to change the default return value for graceful predicate functors.
     *
     * @return the desired return value for null inputs to predicates
     */
    boolean nullTo() default false;

}
