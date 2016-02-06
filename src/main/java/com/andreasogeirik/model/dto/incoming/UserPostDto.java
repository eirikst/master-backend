package com.andreasogeirik.model.dto.incoming;

import com.andreasogeirik.model.entities.UserPost;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class UserPostDto {
    private int id;
    private String message = "";
    private String imageUri = "";

    public UserPost toPost() {
        return new UserPost(id, message, imageUri);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
