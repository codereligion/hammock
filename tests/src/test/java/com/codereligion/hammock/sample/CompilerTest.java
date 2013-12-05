package com.codereligion.hammock.sample;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;

import static com.codereligion.hammock.sample.Member_.*;
import static com.google.common.collect.FluentIterable.from;
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

        assertThat(members.filter(isHappy()).toList(), hasSize(1));
        assertThat(members.transform(getNickName()).toList(), is(Arrays.asList("Ali", "Bob")));
    }

}
