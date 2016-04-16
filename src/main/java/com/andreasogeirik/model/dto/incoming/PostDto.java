package com.andreasogeirik.model.dto.incoming;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class PostDto {
    private String message = "";

    public PostDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
