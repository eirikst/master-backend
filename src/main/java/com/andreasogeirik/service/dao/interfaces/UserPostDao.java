package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.UserPost;
import com.andreasogeirik.model.UserPostComment;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserPostDao {
    void createUserPost(UserPost post, int userId);
    void comment(UserPostComment comment, int postId, int userId);
    UserPost findById(int id);
    void like(int postId, int userId);
    }
