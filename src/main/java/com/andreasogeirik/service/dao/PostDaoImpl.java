package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.InputManager;
import com.andreasogeirik.tools.Codes;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by eirikstadheim on 29/01/16.
 */

public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InputManager inputManager;

    @Autowired
    private UserDao userDao;//trenger den denne da? fjerne de

    @Override
    public int newUserPost(String message, String imageUri, int userId) {
        User user = userDao.findById(userId);//denne inni transaction??

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPost post = new UserPost(message, new Date(), imageUri, user);

        session.save(post);

        session.getTransaction().commit();
        session.close();

        return 1;
    }

    @Override
    public int comment(String message, int postId, int userId) {
        User user = userDao.findById(userId);//denne inni transaction??
        UserPost post = findById(postId);//denne inni transaction??

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPostComment comment = new UserPostComment(message, new Date(), user, post);

        session.save(comment);

        session.getTransaction().commit();
        session.close();

        return 1;
    }

    /*
    * Finds a UserPost entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public UserPost findById(int id) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(UserPost.class);
        UserPost post = (UserPost)criteria.add(Restrictions.eq("id", id))
                .uniqueResult();

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
