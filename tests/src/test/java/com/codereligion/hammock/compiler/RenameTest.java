package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import static com.codereligion.hammock.sample.Member_.toName;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RenameTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setName("Alice");

        final Member bob = new Member();
        bob.setName("Robert");

        final FluentIterable<Member> members = from(asList(alice, bob));

        assertThat(members.transform(toName()).toList(), is(asList("Alice", "Robert")));
    }

}
