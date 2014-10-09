package com.codereligion.hammock.compiler;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import static com.codereligion.hammock.sample.Order_.getArticle;
import static com.codereligion.hammock.sample.article.Article_.getName;

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.FluentIterable.from;

import org.junit.Test;

import com.codereligion.hammock.sample.Order;
import com.codereligion.hammock.sample.article.Article;

public class ImportFromNestedPackageTest {

    @Test
    public void importFromNestedPackagesShouldBeKept() {
        Article article = new Article();
        article.setName("name");

        final Order order = new Order();
        order.setArticle(article);

        assertThat(from(asList(order)).transform(compose(getName(), getArticle())).first().orNull(), is("name"));

    }
}
