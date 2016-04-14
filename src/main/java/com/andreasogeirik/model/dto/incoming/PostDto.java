package com.andreasogeirik.model.dto.incoming;

import com.andreasogeirik.model.entities.Post;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class PostDto {
    private String message = "";
    private String imageUri = "";
    private int writerId;
    private int userId;
    private int eventId;

    public Post toPost() {
        return new Post(message, imageUri);
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWriterId() {
        return writerId;
    }

    public void setWriterId(int writerId) {
        this.writerId = writerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
