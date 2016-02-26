package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.UserPost;

import java.util.*;

/**
 * Created by eirikstadheim on 06/02/16.
 */
public class UserPostDtoOut {
    private int id;
    private String message;
    private Date timeCreated;
    private String imageUri;
    private Set<UserPostCommentDtoOut> comments;
    private Set<UserDtoOut> likers;


    public UserPostDtoOut() {
    }

    public UserPostDtoOut(int id, String message, Date timeCreated, String imageUri) {
        this.id = id;
        this.message = message;
        this.timeCreated = timeCreated;
        this.imageUri = imageUri;
    }

    public UserPostDtoOut(int id, String message, Date timeCreated, String imageUri, Set<UserPostCommentDtoOut> comments) {
        this.id = id;
        this.message = message;
        this.timeCreated = timeCreated;
        this.imageUri = imageUri;
        this.comments = comments;
    }

    //if comments or likers should be added, use setters
    public UserPostDtoOut(UserPost post) {
        id = post.getId();
        message = post.getMessage();
        timeCreated = post.getTimeCreated();
        imageUri = post.getImageUri();
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Set<UserPostCommentDtoOut> getComments() {
        return comments;
    }

    public void setComments(Set<UserPostCommentDtoOut> comments) {
        this.comments = comments;
    }

    public Set<UserDtoOut> getLikers() {
        return likers;
    }

    public void setLikers(Set<UserDtoOut> likers) {
        this.likers = likers;
    }
}
