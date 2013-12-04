package com.codereligion.hammock.sample;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.FluentIterable.*;
import static org.hamcrest.MatcherAssert.*;
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
        
        assertThat(((FluentIterable<Member>) members).filter(Member_.isHappy()).toList(), hasSize(1));
        assertThat(members.transform(Member_.getNickName()).toList(), is(Arrays.asList("Ali", "Bob")));
    }

}
