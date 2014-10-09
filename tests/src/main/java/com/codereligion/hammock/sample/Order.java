package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.sample.article.Article;

public class Order {
    private Article article;

    @Functor
    public Article getArticle() {
        return article;
    }

    public void setArticle(final Article article) {
        this.article = article;
    }
}
