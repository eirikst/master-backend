package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.PostLike;

/**
 * Created by eirikstadheim on 11/04/16.
 */
public class PostLikeDtoOut {
    private int likeId;
    private PostDtoOutSmall post;
    private UserDtoOutSmall user;

    public PostLikeDtoOut() {
    }

    public PostLikeDtoOut(PostLike like) {
        this.likeId = like.getId();
        this.post = new PostDtoOutSmall(like.getPost());
        this.user = new UserDtoOutSmall(like.getUser());
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public PostDtoOutSmall getPost() {
        return post;
    }

    public void setPost(PostDtoOutSmall post) {
        this.post = post;
    }

    public UserDtoOutSmall getUser() {
        return user;
    }

    public void setUser(UserDtoOutSmall user) {
        this.user = user;
    }
}
