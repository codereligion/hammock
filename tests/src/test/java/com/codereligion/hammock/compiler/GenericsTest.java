package com.codereligion.hammock.compiler;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.junit.Test;

import static com.codereligion.hammock.sample.Optionals_.isPresent;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class GenericsTest {

    @Test
    public void test() {
        final FluentIterable<Optional<?>> optionals = from(asList(
                Optional.absent(), Optional.of("abc")
        ));

        assertThat(optionals.filter(isPresent()).toList(), hasSize(1));
    }

}
