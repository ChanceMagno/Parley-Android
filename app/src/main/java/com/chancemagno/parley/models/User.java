package com.chancemagno.parley.models;


public class User {
    String name;
    String email;
    String photoURL;

    User(){}

    public User(String name, String email, String photoURL) {
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }


}
