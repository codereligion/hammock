package com.codereligion.hammock.compiler.example;

import com.google.common.base.Function;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.codereligion.hammock.compiler.FunctionalCompiler")
public final class Member_ {

    private static final Function<Member, String> GET_LOCATION =
        new Function<Member, String>() {

            @Nullable
            @Override
            public String apply(@Nullable Member input) {
                return input == null ? null : input.getLocation();
            }

        };

    private static final Function<Member, String> GET_NAME =
        new Function<Member, String>() {

            @Nullable
            @Override
            public String apply(@Nullable Member input) {
                return input == null ? null : input.getName();
            }

        };

    private Member_() {

    }

    public static Function<Member, String> getLocation() {
        return GET_LOCATION;
    }

    public static Function<Member, String> getName() {
        return GET_NAME;
    }

}