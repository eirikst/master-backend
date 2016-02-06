package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.EventPost;
import com.andreasogeirik.model.entities.EventPostComment;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface EventPostDao {
    void createEventPost(EventPost post, int userId);
    void comment(EventPostComment comment, int postId, int userId);
    EventPost findById(int id);
    void like(int postId, int userId);
    }
