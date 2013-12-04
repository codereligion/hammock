package com.codereligion.hammock.compiler.model.simple;

import com.codereligion.hammock.compiler.model.api.ClosureName;
import com.google.common.base.CaseFormat;

public class StringClosureName implements ClosureName {

    private final String lowerCamelName;

    public StringClosureName(String lowerCamelName) {
        this.lowerCamelName = lowerCamelName;
    }

    @Override
    public String toLowerCamel() {
        return lowerCamelName;
    }

    @Override
    public String toUpperCamel() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, lowerCamelName);
    }
    
}
