package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.codereligion.hammock.sample.Member_.getNickName;
import static com.codereligion.hammock.sample.Member_.isSad;
import static com.codereligion.hammock.sample.Member_.toName;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class CompilerTest {

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

        final FluentIterable<Member> members = from(ImmutableList.of(alice, bob));

        assertThat(members.transform(toName()).toList(), is(asList("Alice", "Robert")));
        assertThat(members.transform(getNickName()).toList(), is(asList("Ali", "Bob")));
        assertThat(members.filter(isSad()).toList(), hasSize(1));
    }

}
