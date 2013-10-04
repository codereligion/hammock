package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;

public class InvalidUseOnNoArgumentStaticMethod {

    @Functional
    public static String getFirst() {
        return null;
    }

}
