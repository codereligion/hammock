package com.codereligion.hammock.compiler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ComparisonChain;

abstract class GeneratedMethod implements Comparable<GeneratedMethod> {
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUppercaseName() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    @Override
    public int compareTo(GeneratedMethod that) {
        return ComparisonChain.start().compare(getName(), that.getName()).result();
    }
    
}
