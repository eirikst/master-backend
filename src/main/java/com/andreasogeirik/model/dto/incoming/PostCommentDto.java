package com.andreasogeirik.model.dto.incoming;

import com.andreasogeirik.model.entities.Comment;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class PostCommentDto {
    private int userId;
    private String message = "";

    public Comment toUserPostComment() {
        return new Comment(message);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
