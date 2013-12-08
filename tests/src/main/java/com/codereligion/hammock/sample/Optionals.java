package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;
import com.google.common.base.Optional;

public class Optionals {

    private Optionals() {
        
    }
    
    @Functor
    public static boolean isPresent(Optional<?> optional) {
        return optional.isPresent();
    }

}
