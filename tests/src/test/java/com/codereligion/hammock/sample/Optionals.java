package com.codereligion.hammock.sample;

import com.codereligion.hammock.FirstClass;
import com.google.common.base.Optional;

public class Optionals {

    @FirstClass
    public static boolean isPresent(Optional<?> optional) {
        return optional.isPresent();
    }

}
