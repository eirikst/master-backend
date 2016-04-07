package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.EventPost;
import com.andreasogeirik.model.entities.EventPostComment;
import com.andreasogeirik.model.entities.EventPostLike;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.service.dao.interfaces.EventPostDao;
import com.andreasogeirik.tools.InvalidInputException;
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

    @Transactional
    @Override
    public void createEventPost(EventPost post, int userId) {
        if(!inputManager.isValidPost(post.getMessage())) {
            throw new InvalidInputException("Invalid post message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        post.setTimeCreated(new Date());
        post.setUser(user);
        session.save(post);

        session.getTransaction().commit();
        session.close();
    }

    @Transactional
    @Override
    public void comment(EventPostComment comment, int postId, int userId) {
        if(!inputManager.isValidComment(comment.getMessage())) {
            throw new InvalidInputException("Invalid comment message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        EventPost post = session.get(EventPost.class, postId);

        User user = session.get(User.class, userId);

        comment.setUser(user);
        comment.setPost(post);
        comment.setTimeCreated(new Date());
        session.save(comment);

        session.getTransaction().commit();
        session.close();
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

    @Transactional
    @Override
    public void like(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        EventPost post = session.get(EventPost.class, postId);
        User user = session.get(User.class, userId);

        EventPostLike like = new EventPostLike(user, post);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }
}
