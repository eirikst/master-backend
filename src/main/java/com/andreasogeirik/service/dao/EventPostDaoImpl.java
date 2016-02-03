package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.interfaces.EventPostDao;
import com.andreasogeirik.tools.Codes;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by eirikstadheim on 29/01/16.
 */

public class EventPostDaoImpl implements EventPostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InputManager inputManager;

    @Override
    public int newEventPost(String message, String imageUri, int userId, int eventId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        Event event = session.get(Event.class, eventId);

        int status = 0;

        if(user != null) {
            if(event != null) {
                EventPost post = new EventPost(message, new Date(), imageUri, user, event);
                session.save(post);
                status = Codes.OK;
            }
            else {
                status = Codes.EVENT_NOT_FOUND;
            }
        }
        else {
            status = Codes.USER_NOT_FOUND;
        }
        session.getTransaction().commit();
        session.close();

        return status;
    }

    @Override
    public int comment(String message, int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        EventPost post = session.get(EventPost.class, postId);

        User user = session.get(User.class, userId);

        int status = 0;
        if(post != null) {
            if (user != null) {
                EventPostComment comment = new EventPostComment(message, new Date(), user, post);
                session.save(comment);
                status = Codes.OK;
            }
            else {
                status = Codes.USER_NOT_FOUND;
            }
        }
        else {
            status = Codes.POST_NOT_FOUND;
        }

        session.getTransaction().commit();
        session.close();

        return status;
    }

    /*
    * Finds a EventPost entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public EventPost findById(int id) {
        Session session = sessionFactory.openSession();

        EventPost post = session.get(EventPost.class, id);

        session.close();

        return post;
    }

    @Override
    public int likePost(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        EventPost post = session.get(EventPost.class, postId);

        User user = session.get(User.class, userId);

        if(post != null) {
            if(user != null) {
                EventPostLike like = new EventPostLike(user, post);
                session.save(like);
            }
            else {
                return Codes.USER_NOT_FOUND;
            }
        }
        else {
            return Codes.POST_NOT_FOUND;
        }

        session.getTransaction().commit();
        session.close();

        return Codes.OK;
    }
}
