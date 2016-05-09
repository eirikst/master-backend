package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.ActivityType;
import com.andreasogeirik.model.entities.Event;
import com.andreasogeirik.model.entities.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    private String thumbUri = "";
    private int difficulty = 1;
    private int activityTypeId = 0;
    private Set<UserDtoOut> users = new HashSet<>();
    private UserDtoOut admin;

    public EventDtoOut() {
    }

    public EventDtoOut(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.timeStart = event.getTimeStart();
        this.timeEnd = event.getTimeEnd();
        this.imageUri = event.getImageURI();
        this.thumbUri = event.getThumbURI();
        this.difficulty = event.getDifficulty();
        this.activityTypeId = event.getActivityType().getId();

        for (User user : event.getUsers()) {
            users.add(new UserDtoOut(user));
        }
        this.admin = new UserDtoOut(event.getAdmin());
    }

    public static EventDtoOut newInstanceWithoutUsersOrAdmin(Event event) {
        EventDtoOut instance = new EventDtoOut();
        instance.id = event.getId();
        instance.name = event.getName();
        instance.location = event.getLocation();
        instance.description = event.getDescription();
        instance.timeStart = event.getTimeStart();
        instance.timeEnd = event.getTimeEnd();
        instance.imageUri = event.getImageURI();
        instance.difficulty = event.getDifficulty();
        instance.activityTypeId = event.getActivityType().getId();
        return instance;
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

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Set<UserDtoOut> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDtoOut> users) {
        this.users = users;
    }

    public UserDtoOut getAdmin() {
        return admin;
    }

    public void setAdmin(UserDtoOut admin) {
        this.admin = admin;
    }
}
