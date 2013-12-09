package com.codereligion.hammock.compiler;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static com.codereligion.hammock.sample.Integers_.negate;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PrimitivesTest {

    @Test
    public void test() {
        final List<Integer> integers = asList(1, 2, 3);
        final ImmutableList<Integer> negated = from(integers).transform(negate()).toList();

        assertThat(negated, is(asList(-1, -2, -3)));
    }

}
