package com.codereligion.hammock.compiler.model;

import com.google.common.base.CaseFormat;

public class ClosureName {

    private final String lowerCamelName;

    public ClosureName(String lowerCamelName) {
        this.lowerCamelName = lowerCamelName;
    }

    public String toLowerCamel() {
        return lowerCamelName;
    }

    public String toUpperCamel() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, lowerCamelName);
    }
    
}
