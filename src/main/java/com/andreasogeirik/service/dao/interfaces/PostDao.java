package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.UserPost;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface PostDao {
    int newUserPost(String message, String imageUri, int userId);
    int comment(String message, int postId, int userId);
    UserPost findById(int id);
    int likePost(int postId, int userId);
    }
