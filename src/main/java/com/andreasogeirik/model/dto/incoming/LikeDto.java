package com.andreasogeirik.model.dto.incoming;

/**
 * Created by eirikstadheim on 03/02/16.
 */
public class LikeDto {
    int userId;
    int postId;

    public LikeDto() {
    }

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
