package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.UserPost;
import com.andreasogeirik.model.entities.UserPostComment;

import java.util.List;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserPostDao {
    void createUserPost(UserPost post, int userId);
    void comment(UserPostComment comment, int postId, int userId);
    UserPost findById(int id);
    List<UserPost> findPosts(int userId, int start, int quantity);
    void like(int postId, int userId);
    }
