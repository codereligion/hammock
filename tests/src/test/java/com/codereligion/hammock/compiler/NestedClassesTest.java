package com.codereligion.hammock.compiler;

import com.codereligion.hammock.sample.Stock;
import org.junit.Test;

import static com.codereligion.hammock.sample.Stock_.Quantity_.getQuantity;
import static com.google.common.collect.FluentIterable.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NestedClassesTest {

    @Test
    public void test() {
        final Stock stock = new Stock();
        final Stock.Quantity quantity = stock.new Quantity();
        
        quantity.setArticle("Cap");
        assertThat(quantity.getArticle(), is("Cap"));
        
        quantity.setQuantity(100);
        
        stock.getQuantities().add(quantity);

        final Integer actual = from(stock.getQuantities()).transform(getQuantity()).get(0);
        assertThat(actual, is(100));
    }

}
