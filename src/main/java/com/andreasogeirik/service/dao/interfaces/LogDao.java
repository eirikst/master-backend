package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.LogElement;

import java.util.List;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface LogDao {
    List<LogElement> getLog(int userId, int offset);
    List<LogElement> getNewLogElements(int userId, int lastLogId);

    void userRegistered(int userId);
    void eventCreated(int eventId);
    void eventModified(int eventId);
    void eventAttended(int eventId, int userId);
    void eventPosted(int eventId, int userId, String msg);
    void eventCommented(int postId, int writerId, String msg);
    void userPosted(int userId, int writerId, String msg);
    void userCommented(int userId, int writerId, int postId, String msg);
    void friendshipAccepted(int userId1, int userId2);
    void postLiked(int userId, int postId);
    void commentLiked(int userId, int commentId);
}