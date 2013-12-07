package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import static com.codereligion.hammock.sample.Member_.isSad;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NullToTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setName("Alice");
        alice.setNickName("Ali");
        alice.setSad(false);

        final Member bob = new Member();
        bob.setName("Robert");
        bob.setNickName("Bob");
        bob.setSad(true);

        final Member charlie = null;
        
        final FluentIterable<Member> members = from(asList(alice, bob, charlie));

        assertThat(members.filter(isSad()).size(), is(2));
    }

}
