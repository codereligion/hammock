package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;

public class ValidUseOnAllInstanceMethods {

    private String first;
    private String second;
    private String third;

    @Functional
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Functional
    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Functional
    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

}
