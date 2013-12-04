package com.codereligion.hammock.compiler.playground;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.codereligion.hammock.compiler.HammockCompiler")
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

    private enum GetNickName
        implements Function<Member, String> {

        INSTANCE;

        @Nullable
        @Override
        public String apply(Member input) {
            Preconditions.checkNotNull(input, "Member");
            return input.getNickName();
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

    private enum IsActive
        implements Predicate<Member> {

        INSTANCE;

        @Override
        public boolean apply(Member input) {
            Preconditions.checkNotNull(input, "Member");
            return input.isActive();
        }

    }

    private Member_() {

    }

    public static Function<Member, String> getName() {
        return GetName.INSTANCE;
    }

    public static Function<Member, String> getNickName() {
        return GetNickName.INSTANCE;
    }

    public static Predicate<Member> isHappy() {
        return IsHappy.INSTANCE;
    }

    public static Predicate<Member> isActive() {
        return IsActive.INSTANCE;
    }

}
