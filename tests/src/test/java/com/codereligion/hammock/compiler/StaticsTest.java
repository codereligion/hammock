package com.codereligion.hammock.compiler;

import com.google.common.collect.FluentIterable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;

import static com.codereligion.hammock.sample.Strings_.isEmpty;
import static com.codereligion.hammock.sample.Strings_.replace;
import static com.codereligion.hammock.sample.Strings_.toUpperCamel;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class StaticsTest {

    @Test
    public void testParameters() {
        final FluentIterable<String> strings = from(asList("a", "b", "c", ""));

        assertThat(strings.filter(isEmpty()).toList(), hasSize(1));
        assertThat(strings.transform(replace("a", "x")).toList(), is(asList("x", "b", "c", "")));
    }
    
    @Test
    public void testGraceful() {
        final FluentIterable<String> strings = from(asList("a", "b", "c", null));
        final ArrayList<String> actual = new ArrayList<>();
        strings.transform(toUpperCamel(Locale.ENGLISH)).copyInto(actual);
        assertThat(actual, hasSize(4));
    }

}
