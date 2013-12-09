package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

import java.util.ArrayList;
import java.util.List;

public class Stock {

    private final List<Quantity> quantities = new ArrayList<>();
    
    public final class Quantity {
        
        private String article;
        private int quantity;

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        @Functor
        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
    }

    public List<Quantity> getQuantities() {
        return quantities;
    }
    
}
