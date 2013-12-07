package com.codereligion.hammock;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows static, compile-time code generation of {@link Function}s
 * and {@link Predicate}s for ordinary java methods, i.e. promoting
 * them to first-class functions.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Functor {

    /**
     * Allows to override the name for the generated method which
     * defaults to the name of annotated method.
     * 
     * For example
     * 
     * <pre>{@code 
     * &#064;Functor(name = "toName")
     * public String getName() {
     *     return name;
     * }
     * }</pre>
     * 
     * can than be used like this:
     * 
     * <pre>{@code FluentIterable.from(members).transform(toName());}</pre>
     * 
     * @return the generated method name
     */
    String name() default "";

    /**
     * Allows to change the behaviour for null inputs. By default nulls
     * will be invoked/passed without any special handling:
     * 
     * <pre>{@code    
     * &#064;Override
     * public String apply(&#064;Nullable Member member) {
     *     return member.getName(); 
     * }
     * }</pre>
     * 
     * When set to true the implementation changes to:
     * 
     * <pre>{@code    
     * &#064;Override
     * public String apply(&#064;Nullable Member member) {
     *     return member == null ? null : member.getName(); 
     * }
     * }</pre>
     * 
     * or to the following for predicates respectively:
     * 
     * <pre>{@code    
     * &#064;Override
     * public boolean apply(&#064;Nullable Member member) {
     *     return member != null && member.isActive();
     * }
     * }</pre>
     * 
     * @return whether nulls will be handled or passed to the method
     */
    boolean graceful() default false;

    /**
     * Allows to change the default return value for graceful predicate functors.
     * This property is only considered when {@link #graceful()} is set to true.
     * Setting it to true changes the implementation of predicates to:
     * 
     * <pre>{@code    
     * &#064;Override
     * public boolean apply(&#064;Nullable Member member) {
     *     return member == null || member.isDeleted();
     * }
     * }</pre>
     * 
     * e.g. treating nulls as deleted in this case.
     * 
     * @return the desired return value for null inputs to predicates
     */
    boolean nullTo() default false;

}
