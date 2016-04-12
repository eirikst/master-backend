package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.CommentLike;

/**
 * Created by eirikstadheim on 11/04/16.
 */
public class CommentLikeDtoOut {
    private int likeId;
    private CommentDtoOutSmall comment;
    private UserDtoOutSmall user;

    public CommentLikeDtoOut() {
    }

    public CommentLikeDtoOut(CommentLike like) {
        this.likeId = like.getId();
        this.comment = new CommentDtoOutSmall(like.getComment());
        this.user = new UserDtoOutSmall(like.getUser());
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public CommentDtoOutSmall getComment() {
        return comment;
    }

    public void setComment(CommentDtoOutSmall comment) {
        this.comment = comment;
    }

    public UserDtoOutSmall getUser() {
        return user;
    }

    public void setUser(UserDtoOutSmall user) {
        this.user = user;
    }
}
