package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Comment;
import com.andreasogeirik.model.entities.CommentLike;
import com.andreasogeirik.model.entities.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 08/02/16.
 */
public class CommentDtoOut {
    private int id;
    private String message;
    private Date timeCreated;
    private UserDtoOut user;
    private Set<UserDtoOutSmall> likers = new HashSet<>();
    //set med likes må inn en gang


    public CommentDtoOut() {
    }

    //does not add user, use setter for that
    public CommentDtoOut(Comment comment) {
        this.id = comment.getId();
        this.message = comment.getMessage();
        this.timeCreated = comment.getTimeCreated();
        this.user = new UserDtoOut(comment.getUser());
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

    public Set<UserDtoOutSmall> getLikers() {
        return likers;
    }

    public void setLikers(Set<UserDtoOutSmall> likers) {
        this.likers = likers;
    }

    public void setLikersFromEntity(Set<CommentLike> likersEntity) {
        for(CommentLike like: likersEntity) {
            likers.add(new UserDtoOutSmall(like.getUser()));
        }
    }
}
