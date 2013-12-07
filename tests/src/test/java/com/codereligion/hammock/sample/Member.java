package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

public class Member {

    private String name;
    private String nickName;
    private boolean sad;
    private boolean active;

    @Functor(name = "toName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Functor(graceful = true)
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Functor(graceful = true, nullTo = true)
    public boolean isSad() {
        return sad;
    }

    public void setSad(boolean sad) {
        this.sad = sad;
    }

    @Functor(graceful = true)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
