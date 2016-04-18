package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class UserDtoOutSmall {
    private int id;
    private String firstname = "";
    private String lastname = "";
    private String thumbUri = "";
    private boolean admin = false;

    public UserDtoOutSmall() {

    }

    public UserDtoOutSmall(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
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
}
