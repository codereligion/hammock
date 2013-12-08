package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

import java.util.ArrayList;
import java.util.List;

@Functor
public class Department {

    private String name;
    private Member lead;

    @Functor(name = "toName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Member getLead() {
        return lead;
    }

    public void setLead(Member lead) {
        this.lead = lead;
    }

}
