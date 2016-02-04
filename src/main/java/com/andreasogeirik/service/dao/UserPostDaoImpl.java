package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.InputManager;
import com.andreasogeirik.tools.Codes;
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
    public int newUserPost(UserPost post, int userId) {
        if(!inputManager.isValidPost(post.getMessage())) {
            return Codes.INVALID_POST_MESSAGE;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        int status = 0;
        if(user != null) {
            post.setTimeCreated(new Date());
            post.setUser(user);
            session.save(post);
            status = Codes.OK;
        }
        else {
            status = Codes.USER_NOT_FOUND;
        }
        session.getTransaction().commit();
        session.close();

        return status;
    }

    @Override
    public int comment(UserPostComment comment, int postId, int userId) {
        if(!inputManager.isValidComment(comment.getMessage())) {
            return Codes.INVALID_COMMENT_MESSAGE;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPost post = session.get(UserPost.class, postId);

        User user = session.get(User.class, userId);

        int status = 0;
        if(post != null) {
            if (user != null) {
                comment.setUser(user);
                comment.setPost(post);
                comment.setTimeCreated(new Date());
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
    public int likePost(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPost post = session.get(UserPost.class, postId);

        User user = session.get(User.class, userId);

        if(post != null) {
            if(user != null) {
                UserPostLike like = new UserPostLike(user, post);
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
