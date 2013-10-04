package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;

import static com.google.common.base.Objects.firstNonNull;

public class InvalidUseOnTwoArgumentInstanceMethod {

    private String first;

    @Functional
    public String getFirst(String defaultValue, String secondDefaultValue) {
        return firstNonNull(firstNonNull(first, defaultValue), secondDefaultValue);
    }

    public void setFirst(String first) {
        this.first = first;
    }

}
