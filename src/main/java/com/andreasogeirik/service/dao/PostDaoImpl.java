package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.Comment;
import com.andreasogeirik.model.Post;
import com.andreasogeirik.model.User;
import com.andreasogeirik.tools.InputManager;
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
    private UserDao userDao;

    @Override
    public int newPost(String message, String imageUri, int userId) {
        User user = userDao.findById(userId);//denne inni transaction??

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = new Post(message, new Date(), imageUri, user);

        System.out.println(post);
        session.save(post);

        session.getTransaction().commit();
        session.close();

        return 1;
    }

    @Override
    public int comment(String message, int postId, int userId) {
        User user = userDao.findById(userId);//denne inni transaction??
        Post post = findById(postId);//denne inni transaction??

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Comment comment = new Comment(message, new Date(), user, post);

        session.save(comment);

        session.getTransaction().commit();
        session.close();

        return 1;
    }

    /*
    * Finds a Post entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public Post findById(int id) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(Post.class);
        Post post = (Post)criteria.add(Restrictions.eq("id", id))
                .uniqueResult();

        session.close();

        return post;
    }

}
