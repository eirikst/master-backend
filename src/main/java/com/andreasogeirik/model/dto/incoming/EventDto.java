package com.andreasogeirik.model.dto.incoming;

import com.andreasogeirik.model.entities.Event;

import java.util.Date;

/**
 * Created by eirikstadheim on 03/02/16.
 */
public class EventDto {
    private String name = "";
    private String location = "";
    private String description = "";
    private Date timeStart;
    private Date timeEnd;
    private String imageUri = "";
    private int difficulty = 1;

    public Event toEvent() {
        return new Event(name, location, description, timeStart, timeEnd, imageUri, difficulty);
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

    public void setImageUri(String encodedImage) {
        this.imageUri = encodedImage;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
