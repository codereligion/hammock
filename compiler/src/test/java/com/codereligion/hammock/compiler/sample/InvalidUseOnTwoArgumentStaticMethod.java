package com.codereligion.hammock.compiler.sample;

import com.codereligion.hammock.Functional;
import com.google.common.base.Objects;

public class InvalidUseOnTwoArgumentStaticMethod {

    @Functional
    public static String getFirst(String first, String defaultValue) {
        return Objects.firstNonNull(first, defaultValue);
    }

}
