package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserCommentLike;
import com.andreasogeirik.model.entities.UserPostComment;
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

        UserPostComment comment = session.get(UserPostComment.class, commentId);

        User user = session.get(User.class, userId);

        UserCommentLike like = new UserCommentLike(user, comment);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }
}
