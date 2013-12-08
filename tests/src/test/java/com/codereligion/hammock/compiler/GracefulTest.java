package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import java.util.ArrayList;

import static com.codereligion.hammock.sample.Member_.getNickName;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GracefulTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setNickName("Ali");

        final Member bob = new Member();
        bob.setNickName("Bob");

        final Member charlie = null;
        
        final FluentIterable<Member> members = from(asList(alice, bob, charlie));

        final ArrayList<String> actual = new ArrayList<>();
        members.transform(getNickName()).copyInto(actual);
        
        assertThat(actual, is(asList("Ali", "Bob", null)));
    }

}
