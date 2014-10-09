package com.codereligion.hammock.sample.article;

import com.codereligion.hammock.Functor;

public class Article {
    private String name;

    @Functor
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
