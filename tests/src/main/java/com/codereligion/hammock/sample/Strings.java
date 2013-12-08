package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.Input;
import com.google.common.base.CaseFormat;

import java.util.Locale;

public class Strings {
    
    private Strings() {
        
    }
    
    @Functor
    public static boolean isEmpty(String s) {
        return s.isEmpty();
    }
    
    @Functor(graceful = true)
    public static String toUpperCamel(@Input String s, Locale locale) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, s.toUpperCase(locale));
    }
    
    @Functor
    public static String replace(String target, String replacement, @Input String s) {
        return s.replace(target, replacement);
    }

}
