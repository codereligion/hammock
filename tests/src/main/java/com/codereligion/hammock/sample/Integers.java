package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

public class Integers {
    
    private Integers() {
        
    }

    @Functor
    public static int negate(int i) {
        return -i;
    }

}
