package com.andreasogeirik.model.dto.incoming;

import com.andreasogeirik.model.entities.User;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class UserDto {
    private int id;
    private String password = "";
    private String email = "";
    private String firstname = "";
    private String lastname = "";
    private String location = "";
    private String imageUri = "";

    public User toUser() {
        return new User(email, password, true, firstname, lastname,
                location, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
