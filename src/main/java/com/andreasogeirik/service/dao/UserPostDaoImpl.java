package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
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

public class UserPostDaoImpl implements UserPostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InputManager inputManager;

    @Override
    public void createUserPost(UserPost post, int userId) {
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

    @Override
    public void comment(UserPostComment comment, int postId, int userId) {
        if(!inputManager.isValidComment(comment.getMessage())) {
            throw new InvalidInputException("Invalid comment message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPost post = session.get(UserPost.class, postId);

        User user = session.get(User.class, userId);

        comment.setUser(user);
        comment.setPost(post);
        comment.setTimeCreated(new Date());
        session.save(comment);

        session.getTransaction().commit();
        session.close();
    }

    /*
    * Finds a UserPost entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public UserPost findById(int id) {
        Session session = sessionFactory.openSession();

        UserPost post = session.get(UserPost.class, id);

        session.close();

        return post;
    }

    @Override
    public void like(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPost post = session.get(UserPost.class, postId);
        User user = session.get(User.class, userId);

        UserPostLike like = new UserPostLike(user, post);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }
}
