package com.codereligion.hammock.sample;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;

import static com.codereligion.hammock.sample.Member_.*;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class CompilerTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setName("Alice");
        alice.setNickName("Ali");
        alice.setHappy(true);

        final Member bob = new Member();
        bob.setName("Robert");
        bob.setNickName("Bob");
        bob.setHappy(false);

        final FluentIterable<Member> members = from(ImmutableList.of(alice, bob));

        assertThat(members.transform(toName()).toList(), is(asList("Alice", "Robert")));
        assertThat(members.transform(getNickName()).toList(), is(asList("Ali", "Bob")));
        assertThat(members.filter(isHappy()).toList(), hasSize(1));
    }

}
