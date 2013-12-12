package com.codereligion.hammock.sample;

import com.google.common.base.Objects;

public class Department {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof Department) {
            final Department other = (Department) that;
            return Objects.equal(name, other.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
    
}
