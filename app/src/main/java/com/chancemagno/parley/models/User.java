package com.chancemagno.parley.models;

public class User {
    String firstName;
    String lastName;
    String fullName;
    String email;
    String photoURL;

    public User(){}

    public User(String firstName, String lastName, String email, String photoURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = String.format("%s %s", firstName, lastName);
        this.email = email;
        this.photoURL = photoURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName(){ return lastName;}

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getFullName() {return fullName;}


}
