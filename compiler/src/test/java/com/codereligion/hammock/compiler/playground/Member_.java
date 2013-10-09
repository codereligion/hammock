package com.codereligion.hammock.compiler.playground;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.codereligion.hammock.compiler.FunctionalCompiler")
public final class Member_ {

    private enum GetName
        implements Function<Member, String> {

        INSTANCE;

        @Nullable
        @Override
        public String apply(@Nullable Member input) {
            return input == null ? null : input.getName();
        }

    }

    private enum IsHappy
        implements Predicate<Member> {

        INSTANCE;

        @Override
        public boolean apply(@Nullable Member input) {
            return input != null && input.isHappy();
        }

    }

    private Member_() {

    }

    public static Function<Member, String> getName() {
        return GetName.INSTANCE;
    }

    public static Predicate<Member> isHappy() {
        return IsHappy.INSTANCE;
    }

}
