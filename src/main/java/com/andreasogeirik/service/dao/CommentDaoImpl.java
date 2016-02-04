package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.interfaces.CommentDao;
import com.andreasogeirik.tools.Codes;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by eirikstadheim on 29/01/16.
 */

public class CommentDaoImpl implements CommentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void like(int commentId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserPostComment comment = session.get(UserPostComment.class, commentId);

        User user = session.get(User.class, userId);

        UserCommentLike like = new UserCommentLike(user, comment);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }
}
