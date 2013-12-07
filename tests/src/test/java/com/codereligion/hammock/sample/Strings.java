package com.codereligion.hammock.sample;

import com.codereligion.hammock.FirstClass;
import com.codereligion.hammock.Input;

import java.util.Locale;

public class Strings {

    @FirstClass
    public static boolean isEmpty(String s) {
        return s.isEmpty();
    }
    
    @FirstClass
    public static String toLowerCamel(@Input String s, Locale locale) {
        return s.toLowerCase(locale);
    }
    
    @FirstClass(nullsafe = false)
    public static String toUpperCamel(@Input String s, Locale locale) {
        return s.toUpperCase(locale);
    }
    
    @FirstClass
    public static String replace(String search, String replacement, @Input String target) {
        return target.replace(search, replacement);
    }

}
