package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Event;
import com.andreasogeirik.model.entities.User;

import java.util.*;

/**
 * Created by Andreas on 12.02.2016.
 */
public class EventDtoOut {
    private int id;
    private String name = "";
    private String location = "";
    private String description = "";
    private Date timeStart;
    private Date timeEnd;
    private String imageUri = "";
    private UserDtoOut admin;
    private Set<UserDtoOut> users = new HashSet<>();

    public EventDtoOut(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.timeStart = event.getTimeStart();
        this.timeEnd = event.getTimeEnd();
        this.imageUri = event.getImageURI();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public UserDtoOut getAdmin() {
        return admin;
    }

    public void setAdmin(UserDtoOut admin) {
        this.admin = admin;
    }

    public Set<UserDtoOut> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDtoOut> users) {
        this.users = users;
    }
}
