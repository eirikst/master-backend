package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Comment;

import java.util.Date;

/**
 * Created by eirikstadheim on 08/02/16.
 */
public class CommentDtoOutSmall {
    private int id;

    public CommentDtoOutSmall() {
    }

    //does not add user, use setter for that
    public CommentDtoOutSmall(Comment comment) {
        this.id = comment.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
