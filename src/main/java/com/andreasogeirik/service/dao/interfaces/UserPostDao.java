package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.UserPost;
import com.andreasogeirik.model.UserPostComment;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserPostDao {
    int newUserPost(UserPost post, int userId);
    int comment(UserPostComment comment, int postId, int userId);
    UserPost findById(int id);
    int likePost(int postId, int userId);
    }
