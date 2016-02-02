package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.Post;
import com.andreasogeirik.model.User;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface PostDao {
    public static final int INVALID_MESSAGE = -1;
    public static final int INVALID_URI = -2;
    public static final int USER_NOT_FOUND = -3;
    public static final int POST_NOT_FOUND = -4;
    public static final int INVALID_COMMENT_MESSAGE = -5;


    public int newPost(String message, String imageUri, int userId);
    public int comment(String message, int postId, int userId);
    public Post findById(int id);
    }
