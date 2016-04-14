package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.CommentLike;
import com.andreasogeirik.model.entities.Comment;
import com.andreasogeirik.service.dao.interfaces.CommentDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by eirikstadheim on 29/01/16.
 */

public class CommentDaoImpl implements CommentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @Override
    public void like(int commentId, int userId) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Comment comment = session.get(Comment.class, commentId);

        User user = session.get(User.class, userId);

        CommentLike like = new CommentLike(user, comment);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }
}
