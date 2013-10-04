package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;
import com.google.common.base.Objects;

public class InvalidUseOnSingleArgumentInstanceMethod {

    private String first;
    
    @Functional
    public String getFirst(String defaultValue) {
        return Objects.firstNonNull(first, defaultValue);
    }

    public void setFirst(String first) {
        this.first = first;
    }
    
}
