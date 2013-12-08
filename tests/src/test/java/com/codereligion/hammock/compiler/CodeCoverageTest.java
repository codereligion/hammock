package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member_;
import com.codereligion.hammock.sample.Optionals;
import com.codereligion.hammock.sample.Optionals_;
import com.codereligion.hammock.sample.Strings;
import com.codereligion.hammock.sample.Strings_;
import com.google.common.reflect.Invokable;
import com.google.gag.annotation.remark.Hack;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@Hack
public class CodeCoverageTest {

    @Test
    public void privateConstructors() throws Exception {
        invokePrivateNoArgumentConstructor(Optionals.class);
        invokePrivateNoArgumentConstructor(Strings.class);

        invokePrivateNoArgumentConstructor(Member_.class);
        invokePrivateNoArgumentConstructor(Optionals_.class);
        invokePrivateNoArgumentConstructor(Strings_.class);
    }

    @Test
    public void enumMethods() throws Exception {
        invokeEnumMethods(Member_.class);
        invokeEnumMethods(Optionals_.class);
        invokeEnumMethods(Strings_.class);
    }
    
    private void invokePrivateNoArgumentConstructor(Class<?> type) throws Exception {
        final Collection<Constructor<?>> constructors = asList(type.getDeclaredConstructors());

        assertThat(constructors, is(not(empty())));
        
        for (Constructor<?> constructor : constructors) {
            assertThat(constructor.getModifiers(), is(Modifier.PRIVATE));
            assertThat(constructor.isAccessible(), is(false));
            
            try {
                constructor.setAccessible(true);
                constructor.newInstance();
            } finally {
                constructor.setAccessible(false);
            }
        }
    }
    
    private void invokeEnumMethods(Class<?> container) throws Exception{
        for (Method method : container.getDeclaredMethods()) {
            final Invokable<?, Object> invokable = Invokable.from(method);
            if (invokable.isPublic() && invokable.getParameters().isEmpty()) {
                final Object result = invokable.invoke(null);
                assertThat(result, is(instanceOf(Enum.class)));
                invokeValueOf(result.getClass());
            }
        }
    }

    private <E extends Enum<E>> void invokeValueOf(Class<?> type) throws Exception{
        @SuppressWarnings("unchecked")
        final Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
        
        final Enum<?>[] constants = enumType.getEnumConstants();
        assertThat(constants, is(not(nullValue())));

        for (Enum<?> constant : constants) {
            final Method valueOf = enumType.getDeclaredMethod("valueOf", String.class);
            
            try {
                valueOf.setAccessible(true);
                
                final Object actual = valueOf.invoke(null, constant.name());
                assertThat(actual, sameInstance((Object) constant));
            } finally {
                valueOf.setAccessible(false);
            }
        }
    }

}
