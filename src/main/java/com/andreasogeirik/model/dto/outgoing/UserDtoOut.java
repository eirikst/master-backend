package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("id", id);
        node.put("email", email);
        node.put("enabled", enabled);
        node.put("location", id);
        node.put("firstname", firstname);
        node.put("lastname", lastname);
        node.put("imageUri", imageUri);
        return node;
    }
}
