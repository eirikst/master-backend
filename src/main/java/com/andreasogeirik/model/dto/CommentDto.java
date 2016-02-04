package com.andreasogeirik.model.dto;

import com.andreasogeirik.model.UserPostComment;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class CommentDto {
    private int userId;
    private String message = "";

    public UserPostComment toUserPostComment() {
        return new UserPostComment(message);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
