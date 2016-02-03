package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.EventPost;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface EventPostDao {
    int newEventPost(String message, String imageUri, int userId, int eventId);
    int comment(String message, int postId, int userId);
    EventPost findById(int id);
    int likePost(int postId, int userId);
    }
