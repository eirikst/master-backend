package com.andreasogeirik.model.dto.incoming;

/**
 * Created by eirikstadheim on 03/02/16.
 */
public class PostLikeDto {
    int userId;
    int postId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
