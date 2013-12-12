package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

public class Table {

    public final class Row {
        
        public final class Cell {
            
            private String value;

            @Functor
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
            
        }
        
    } 

}
