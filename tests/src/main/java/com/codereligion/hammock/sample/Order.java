package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.sample.article.Article;

public class Order {
    private String orderNumber;
    private Article article;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Functor
    public Article getArticle() {
        return article;
    }

    public void setArticle(final Article article) {
        this.article = article;
    }
}
