package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Department;
import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import static com.codereligion.hammock.sample.Department_.getLead;
import static com.codereligion.hammock.sample.Department_.toName;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ClassLevelTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setName("Alice");

        final Member bob = new Member();
        bob.setName("Bob");

        final Department finance = new Department();
        finance.setName("Finance");
        finance.setLead(alice);
        
        final Department hr = new Department();
        hr.setName("Human Resources");
        hr.setLead(bob);

        final FluentIterable<Department> departments = FluentIterable.from(asList(finance, hr));
        final FluentIterable<Member> leads = FluentIterable.from(asList(alice, bob));

        assertThat(departments.transform(toName()).toList(), is(asList("Finance", "Human Resources")));
        assertThat(departments.transform(getLead()).toList(), is(leads.toList()));
    }

}
