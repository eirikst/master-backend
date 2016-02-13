package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Event;

import java.util.Date;

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
    private int adminId;

    public EventDtoOut(int id, String name, String location, String description, Date timeCreated, Date timeStart, Date timeEnd, String imageUri, int adminId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.imageUri = imageUri;
        this.adminId = adminId;
    }

    public EventDtoOut(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.timeStart = event.getTimeStart();
        this.timeEnd = event.getTimeEnd();
        this.imageUri = event.getImageURI();
        this.adminId = event.getAdmin().getId();
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

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
