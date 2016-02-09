package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserPost;
import com.andreasogeirik.model.entities.UserPostComment;
import com.andreasogeirik.model.entities.UserPostLike;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    public List<UserPost> findPosts(int userId, int start, int quantity) {
        Session session = sessionFactory.openSession();

        String hql = "FROM UserPost P WHERE P.user.id = " + userId;
        Query query = session.createQuery(hql);
        query.setMaxResults(quantity);
        query.setFirstResult(start);
        List<UserPost> posts = (List<UserPost>)query.list();

        for(int i = 0; i < posts.size(); i++) {
            Hibernate.initialize(posts.get(i).getComments());
            Hibernate.initialize(posts.get(i).getLikes());
            Iterator<UserPostLike> it = posts.get(i).getLikes().iterator();
            while(it.hasNext()) {
                Hibernate.initialize(it.next().getUser());
            }
        }

        session.close();

        return posts;
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
