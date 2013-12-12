package com.codereligion.hammock.sample;

import com.codereligion.hammock.Functor;

public class Member {

    private String name;
    private String nickName;
    private boolean sad;
    private boolean active;
    private int age;

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

    @Functor(nullTo = true)
    public boolean isSad() {
        return sad;
    }

    public void setSad(boolean sad) {
        this.sad = sad;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    @Functor
    public boolean isOlderThan(int minimumAge) {
        return age > minimumAge;
    }
    
    @Functor
    public boolean isBetween(int minimumAge, int maximumAge) {
        return minimumAge < age && age < maximumAge;
    }
    
    @Functor(name = "asString")
    @Override
    public String toString() {
        return name;
    }
    
}
