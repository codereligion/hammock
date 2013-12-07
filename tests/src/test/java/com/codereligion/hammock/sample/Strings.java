package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;
import com.codereligion.hammock.Input;

import java.util.Locale;

public class Strings {

    @Functor
    public static boolean isEmpty(String s) {
        return s.isEmpty();
    }
    
    @Functor
    public static String toLowerCamel(@Input String s, Locale locale) {
        return s.toLowerCase(locale);
    }
    
    @Functor(nullsafe = false)
    public static String toUpperCamel(@Input String s, Locale locale) {
        return s.toUpperCase(locale);
    }
    
    @Functor
    public static String replace(String search, String replacement, @Input String target) {
        return target.replace(search, replacement);
    }

}
