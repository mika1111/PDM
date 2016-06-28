package com.example.magdam.handshake;

/**
 * Created by Magdalena on 2016-06-27.
 */
public class User {
    int id;
    String name;
    String surname;
    String displayName;
    String googleId;

    User(int id, String name, String surname, String displayName) {
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.displayName=displayName;
    }
    public void setGoogleId(String id){
        this.googleId=id;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
