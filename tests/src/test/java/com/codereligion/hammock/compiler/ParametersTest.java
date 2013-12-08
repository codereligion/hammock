package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Member;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import static com.codereligion.hammock.sample.Member_.isBetween;
import static com.codereligion.hammock.sample.Member_.isOlderThan;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class ParametersTest {

    @Test
    public void test() {
        final Member alice = new Member();
        alice.setName("Alice");
        alice.setAge(21);

        final Member bob = new Member();
        bob.setName("Bob");
        bob.setAge(27);
        
        final Member charlie = new Member();
        charlie.setName("Charlie");
        charlie.setAge(31);

        final FluentIterable<Member> members = from(asList(alice, bob, charlie));

        assertThat(members.toList(), hasSize(3));
        assertThat(members.filter(isOlderThan(30)).toList(), hasSize(1));
        assertThat(members.filter(isBetween(20, 30)).toList(), hasSize(2));
        assertThat(members.filter(isBetween(17, 22)).toList(), hasSize(1));
        assertThat(members.filter(isBetween(0, 19)).toList(), hasSize(0));
        assertThat(members.filter(isBetween(40, 65)).toList(), hasSize(0));
    }

}
