package com.codereligion.hammock.compiler.model.simple;

import com.codereligion.hammock.compiler.model.api.Name;
import com.google.common.base.Objects;

public class StringName implements Name {

    private final String qualifiedName;

    public StringName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    private int indexOfLastDot() {
        return qualifiedName.lastIndexOf('.');
    }

    @Override
    public String getPackage() {
        final int dot = indexOfLastDot();
        return dot == -1 ? "" : qualifiedName.substring(0, dot);
    }

    @Override
    public String getSimple() {
        final int dot = indexOfLastDot();
        return dot == -1 ? qualifiedName : qualifiedName.substring(dot + 1);
    }

    @Override
    public String getQualified() {
        return qualifiedName;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof Name) {
            final Name other = (Name) that;
            return qualifiedName.equals(other.getQualified());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(qualifiedName);
    }
    
}
