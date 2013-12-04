package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functional;

public class Member {

    private String name;
    private String nickName;
    private boolean happy;
    private boolean active;

    @Functional
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Functional(nullsafe = false)
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Functional
    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    @Functional
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
