package com.codereligion.hammock.compiler.model.api;

import java.util.List;

public interface Type {

    String getPackage();
    
    Name getName();
    
    Iterable<String> getImports();

    List<Closure> getClosures();
    
}
