package com.codereligion.hammock.compiler.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.removeIf;

public class Type {
    
    private static final Pattern GENERICS = Pattern.compile("<.+>");

    private final Name name;
    private final List<Closure> closures = new ArrayList<>();

    public Type(Name name) {
        this.name = name;
    }

    public String getPackage() {
        return name.getPackage();
    }

    public Name getName() {
        return name;
    }

    public Iterable<String> getImports() {
        final Set<String> imports = new TreeSet<>();

        for (Closure closure : closures) {
            for (Argument argument : closure.getArguments()) {
                imports.add(argument.getType().getQualified());
            }
            
            imports.add(closure.getInput().getType().getQualified());
            imports.add(closure.getReturnType().getQualified());
        }

        if (any(closures, Is.PREDICATE)) {
            imports.add(Predicate.class.getName());
        }

        if (any(closures, Is.FUNCTION)) {
            imports.add(Function.class.getName());
        }
        
        imports.add(Nullable.class.getName());
        imports.add(Generated.class.getName());

        removeIf(imports, Exclude.JAVA_LANG);
        removeIf(imports, Exclude.BOOLEAN);

        final String samePackage = name.getPackage();
        removeIf(imports, startsWith(samePackage));
        
        return ungenerify(imports);
    }

    private Set<String> ungenerify(Set<String> imports) {
        final Set<String> ungenerified = new TreeSet<>();
        
        for (String name : imports) {
            final Matcher matcher = GENERICS.matcher(name);
            ungenerified.add(matcher.replaceAll(""));
        }
        
        return ungenerified;
    }

    public List<Closure> getClosures() {
        return closures;
    }

    private enum Is implements Predicate<Closure> {

        PREDICATE {
            
            @Override
            public boolean apply(@Nullable Closure input) {
                return input != null && input.isPredicate();
            }

        },

        FUNCTION {
            
            @Override
            public boolean apply(@Nullable Closure input) {
                return input != null && !input.isPredicate();
            }

        }

    }

    private enum Exclude implements Predicate<String> {

        JAVA_LANG {
            
            @Override
            public boolean apply(@Nullable String input) {
                return input != null && input.startsWith("java.lang.");
            }
            
        },
        
        BOOLEAN {

            @Override
            public boolean apply(@Nullable String input) {
                return "boolean".equals(input);
            }
            
        }

    }
    
    private static Predicate<String> startsWith(final String prefix) {
        return new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return input != null && input.startsWith(prefix);
            }
        };
    }

}
