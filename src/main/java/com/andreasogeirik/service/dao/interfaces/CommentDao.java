package com.andreasogeirik.service.dao.interfaces;


/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface CommentDao {
    void like(int commentId, int userId);
    }