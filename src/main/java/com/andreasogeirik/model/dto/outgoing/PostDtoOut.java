package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Post;

import java.util.*;

/**
 * Created by eirikstadheim on 06/02/16.
 */
public class PostDtoOut {
    private int id;
    private String message;
    private Date timeCreated;
    private String imageUri;
    private UserDtoOut writer;
    private UserDtoOut user;
    private EventDtoOut event;
    private Set<CommentDtoOut> comments;
    private Set<UserDtoOut> likers;


    public PostDtoOut() {
    }

    //if comments or likers should be added, use setters
    public PostDtoOut(Post post) {
        id = post.getId();
        message = post.getMessage();
        timeCreated = post.getTimeCreated();
        imageUri = post.getImageUri();

        if(post.getWriter() != null) {
            writer = new UserDtoOut(post.getWriter());
        }

        if(post.getUser() != null) {
            user = new UserDtoOut(post.getUser());
        }
        if(post.getEvent() != null) {
            event = new EventDtoOut(post.getEvent());
        }
    }

    public static PostDtoOut newInstanceWithoutEvent(Post post) {
        PostDtoOut postOut = new PostDtoOut();
        postOut.id = post.getId();
        postOut.message = post.getMessage();
        postOut.timeCreated = post.getTimeCreated();
        postOut.imageUri = post.getImageUri();

        if(post.getWriter() != null) {
            postOut.writer = new UserDtoOut(post.getWriter());
        }

        if(post.getUser() != null) {
            postOut.user = new UserDtoOut(post.getUser());
        }

        return postOut;
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

    public UserDtoOut getWriter() {
        return writer;
    }

    public void setWriter(UserDtoOut writer) {
        this.writer = writer;
    }

    public UserDtoOut getUser() {
        return user;
    }

    public void setUser(UserDtoOut user) {
        this.user = user;
    }

    public EventDtoOut getEvent() {
        return event;
    }

    public void setEvent(EventDtoOut event) {
        this.event = event;
    }

    public Set<CommentDtoOut> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDtoOut> comments) {
        this.comments = comments;
    }

    public Set<UserDtoOut> getLikers() {
        return likers;
    }

    public void setLikers(Set<UserDtoOut> likers) {
        this.likers = likers;
    }
}
