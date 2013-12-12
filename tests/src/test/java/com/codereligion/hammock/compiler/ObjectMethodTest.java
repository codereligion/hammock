package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Department;
import com.codereligion.hammock.sample.Member;
import com.codereligion.hammock.sample.Member_;
import com.google.common.base.Optional;
import org.junit.Test;

import static com.codereligion.hammock.sample.Member_.*;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ObjectMethodTest {

    @Test
    public void testEqualsAndHashCode() {
        final Department finance = new Department();
        finance.setName("Finance");
        
        final Department hr = new Department();
        hr.setName("Human Resources");
        
        assertThat(finance, is(finance));
        assertThat(finance, is(not(hr)));
        assertThat(finance, is(not((Object) this)));

        // uses equals/hashCode
        newLinkedHashSet(asList(finance, hr));
    }
    
    @Test
    public void testToString() {
        final Member alice = new Member();
        alice.setName("Alice");
        
        assertThat(Optional.of(alice).transform(asString()).orNull(), is("Alice"));

    }

}
