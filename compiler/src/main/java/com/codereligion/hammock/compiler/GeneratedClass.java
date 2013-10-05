package com.codereligion.hammock.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;

public class GeneratedClass {

    private String packageName;
    private final List<String> imports = new ArrayList<>();
    private String simpleSourceName;
    private final Set<GeneratedMethod> methods = new TreeSet<>();

    public boolean hasFunctions() {
        return any(methods, instanceOf(GeneratedFunction.class));
    }
    
    public boolean hasPredicates() {
        return any(methods, instanceOf(GeneratedPredicate.class));
    }
    
    public Iterable<GeneratedFunction> getFunctions() {
        return filter(methods, GeneratedFunction.class);
    }
    
    public Iterable<GeneratedPredicate> getPredicates() {
        return filter(methods, GeneratedPredicate.class);
    }
    
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSimpleSourceName() {
        return simpleSourceName;
    }

    public void setSimpleSourceName(String simpleSourceName) {
        this.simpleSourceName = simpleSourceName;
    }

    public Set<GeneratedMethod> getMethods() {
        return methods;
    }

    public List<String> getImports() {
        return imports;
    }
        
}
