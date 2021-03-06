package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserRole;
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
    private String thumbUri = "";
    private boolean admin = false;

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
        this.thumbUri = user.getThumbUri();

        if(user.getUserRole() != null && !user.getUserRole().isEmpty()) {
            for(UserRole role: user.getUserRole()) {
                if(role.getRole().equals("ADMIN")) {
                    admin = true;
                }
            }
        }
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

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
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
        node.put("thumbUri", thumbUri);
        node.put("admin", admin);
        return node;
    }
}
