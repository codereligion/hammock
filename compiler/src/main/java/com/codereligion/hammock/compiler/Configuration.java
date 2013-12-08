package com.codereligion.hammock.compiler;

import com.codereligion.hammock.Functor;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import javax.annotation.Nullable;

final class Configuration {
    
    private final Optional<String> name;
    private final boolean graceful;
    private final boolean nullTo;
    
    public Configuration(Functor functor) {
        this.name = Optional.fromNullable(Strings.emptyToNull(functor.name()));
        this.graceful = functor.graceful();
        this.nullTo = functor.nullTo();
    }

    public Configuration(Optional<String> name, boolean graceful, boolean nullTo) {
        this.name = name;
        this.graceful = graceful;
        this.nullTo = nullTo;
    }

    public Optional<String> getName() {
        return name;
    }

    public boolean isGraceful() {
        return graceful;
    }

    public boolean isNullTo() {
        return nullTo;
    }
    
    public Configuration merge(@Nullable Configuration configuration) {
        return configuration == null ? this :new Configuration(
            name.or(configuration.getName()),
            graceful || configuration.graceful,
            nullTo || configuration.nullTo
        );
    }
    
}
