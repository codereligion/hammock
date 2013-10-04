package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;

public class ValidUseOnAllStaticMethods {

    @Functional
    public static String getFirst(String first) {
        return first;
    }

    @Functional
    public static String getSecond(String second) {
        return second;
    }

    @Functional
    public static String getThird(String third) {
        return third;
    }

}
