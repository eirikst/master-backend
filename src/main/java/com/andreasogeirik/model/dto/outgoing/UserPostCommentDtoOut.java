package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.UserPostComment;
import org.hibernate.Hibernate;

import java.util.Date;

/**
 * Created by eirikstadheim on 08/02/16.
 */
public class UserPostCommentDtoOut {
    private int id;
    private String message;
    private Date timeCreated;
    private UserDtoOut user;
    //set med likes må inn en gang


    public UserPostCommentDtoOut() {
    }

    //does not add user, use setter for that
    public UserPostCommentDtoOut(UserPostComment comment) {
        this.id = comment.getId();
        this.message = comment.getMessage();
        this.timeCreated = comment.getTimeCreated();
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public UserDtoOut getUser() {
        return user;
    }

    public void setUser(UserDtoOut user) {
        this.user = user;
    }
}
