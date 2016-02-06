package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.User;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class UserDtoOut {
    private int id;
    private String email = "";
    private boolean enabled;
    private String firstname = "";
    private String lastname = "";
    private String location = "";
    private String imageUri = "";

    public UserDtoOut() {

    }

    public UserDtoOut(int id, String email, boolean enabled, String firstname, String lastname, String location, String imageUri) {
        this.id = id;
        this.email = email;
        this.enabled = enabled;
        this.firstname = firstname;
        this.lastname = lastname;
        this.location = location;
        this.imageUri = imageUri;
    }

    public UserDtoOut(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.location = user.getLocation();
        this.imageUri = user.getImageUri();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
