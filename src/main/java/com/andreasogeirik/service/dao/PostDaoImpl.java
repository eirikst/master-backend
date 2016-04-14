package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.EntityConflictException;
import com.andreasogeirik.tools.EntityNotFoundException;
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
import java.util.Set;

/**
 * Created by eirikstadheim on 29/01/16.
 */

public class PostDaoImpl implements PostDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InputManager inputManager;

    /*
    * Finds a Post entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public Post findById(int id) {
        Session session = sessionFactory.openSession();

        Post post = session.get(Post.class, id);

        session.close();

        return post;
    }

    @Override
    @Transactional
    public Post userPost(String message, String imageUri, int writerId, int userId) {
        if(!inputManager.isValidPost(message)) {
            throw new InvalidInputException("Invalid post message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = new Post();
        post.setMessage(message);
        post.setImageUri(imageUri);
        post.setTimeCreated(new Date());

        User writer = session.get(User.class, writerId);
        if(writer == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Could not find writer");
        }
        post.setWriter(writer);

        User user = session.get(User.class, userId);
        if(user == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Could not find user");
        }
        post.setUser(user);

        session.save(post);

        session.getTransaction().commit();
        session.close();

        return post;
    }

    @Override
    @Transactional
    public Post eventPost(String message, String imageUri, int writerId, int eventId) {
        if(!inputManager.isValidPost(message)) {
            throw new InvalidInputException("Invalid post message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = new Post();
        post.setMessage(message);
        post.setImageUri(imageUri);
        post.setTimeCreated(new Date());

        User writer = session.get(User.class, writerId);
        if(writer == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Could not find user");
        }
        post.setWriter(writer);

        Event event = session.get(Event.class, eventId);
        if(event == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Could not find event");
        }
        post.setEvent(event);
        session.save(post);

        Hibernate.initialize(post.getWriter());
        Hibernate.initialize(post.getUser());
        Hibernate.initialize(post.getEvent());

        session.getTransaction().commit();
        session.close();

        return post;
    }

    @Override
    public void removePost(int id, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = session.get(Post.class, id);
        if(post.getWriter().getId() == userId) {
            session.delete(post);
        }
        else {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not the writer of the post");
        }

        session.getTransaction().commit();
        session.close();
    }

    @Override
    @Transactional
    public Comment comment(Comment comment, int postId, int userId) {
        if(!inputManager.isValidComment(comment.getMessage())) {
            throw new InvalidInputException("Invalid comment message format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = session.get(Post.class, postId);
        if(post == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Specified post not found");
        }

        User user = session.get(User.class, userId);
        if(user == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Specified user not found");
        }


        comment.setUser(user);
        comment.setPost(post);
        comment.setTimeCreated(new Date());
        session.save(comment);

        session.getTransaction().commit();
        session.close();

        return comment;
    }

    @Override
    public void removeComment(int id, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Comment comment = session.get(Comment.class, id);
        if(comment == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Cannot find comment");
        }

        if(comment.getUser().getId() == userId) {
            //delete
            session.delete(comment);
        }
        else {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not the writer of the comment");
        }

        session.getTransaction().commit();
        session.close();
    }

    @Transactional
    @Override
    public void likePost(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = session.get(Post.class, postId);
        User user = session.get(User.class, userId);

        PostLike like = new PostLike(user, post);
        session.save(like);

        Hibernate.initialize(post.getUser());

        session.getTransaction().commit();
        session.close();
    }

    @Transactional
    @Override
    public void likeComment(int commentId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Comment comment = session.get(Comment.class, commentId);
        User user = session.get(User.class, userId);

        CommentLike like = new CommentLike(user, comment);
        session.save(like);

        session.getTransaction().commit();
        session.close();
    }

    @Transactional
    @Override
    public void removeCommentLike(int commentId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Comment comment = session.get(Comment.class, commentId);
        if(comment == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Comment not found");
        }

        Set<CommentLike> likes = comment.getLikes();

        for(CommentLike like: likes) {
            if(like.getUser().getId() == userId) {
                session.delete(like);
                likes.remove(like);
                session.getTransaction().commit();
                session.close();
                return;
            }
        }

        //if code runs here, no likes by the user was found => conflict
        session.getTransaction().commit();
        session.close();
        throw new EntityNotFoundException("The user did not like the comment in the first place");
    }

    @Transactional
    @Override
    public void removePostLike(int postId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Post post = session.get(Post.class, postId);
        if(post == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Post not found");
        }

        Set<PostLike> likes = post.getLikes();

        for(PostLike like: likes) {
            if(like.getUser().getId() == userId) {
                session.delete(like);
                session.getTransaction().commit();
                session.close();
                return;
            }
        }

        //if code runs here, no likes by the user was found => conflict
        session.getTransaction().commit();
        session.close();
        throw new EntityNotFoundException("The user did not like the post in the first place");
    }


    @Transactional
    @Override
    public List<Post> findPostsUser(int userId, int offset, int quantity) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "FROM Post P WHERE P.user.id = " + userId + " ORDER BY P.timeCreated desc";
        Query query = session.createQuery(hql);
        query.setMaxResults(quantity);
        query.setFirstResult(offset);
        List<Post> posts = (List<Post>)query.list();

        for(int i = 0; i < posts.size(); i++) {
            Hibernate.initialize(posts.get(i).getComments());
            Hibernate.initialize(posts.get(i).getLikes());
            Iterator<PostLike> itLikes = posts.get(i).getLikes().iterator();
            while(itLikes.hasNext()) {
                Hibernate.initialize(itLikes.next().getUser());
            }

            Iterator<Comment> itComments = posts.get(i).getComments().iterator();
            while(itComments.hasNext()) {
                Comment comment = itComments.next();
                Hibernate.initialize(comment);
                Hibernate.initialize(comment.getUser());

                for(CommentLike like: comment.getLikes()) {
                    Hibernate.initialize(like);
                }
            }

            Hibernate.initialize(posts.get(i).getWriter());
            Hibernate.initialize(posts.get(i).getEvent());
            Hibernate.initialize(posts.get(i).getUser());
        }

        session.getTransaction().commit();
        session.close();

        return posts;
    }

    @Transactional
    @Override
    public List<Post> findPostsEvent(int eventId, int offset, int quantity) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "FROM Post P WHERE P.event.id = " + eventId + " ORDER BY P.timeCreated desc";
        Query query = session.createQuery(hql);
        query.setMaxResults(quantity);
        query.setFirstResult(offset);
        List<Post> posts = (List<Post>)query.list();

        for(int i = 0; i < posts.size(); i++) {
            Hibernate.initialize(posts.get(i).getComments());
            Hibernate.initialize(posts.get(i).getLikes());
            Iterator<PostLike> it = posts.get(i).getLikes().iterator();
            while(it.hasNext()) {
                Hibernate.initialize(it.next().getUser());
            }

            Iterator<Comment> itComments = posts.get(i).getComments().iterator();
            while(itComments.hasNext()) {
                Comment comment = itComments.next();
                Hibernate.initialize(comment);

                for(CommentLike like: comment.getLikes()) {
                    Hibernate.initialize(like);
                }
            }

            Hibernate.initialize(posts.get(i).getWriter());
            Hibernate.initialize(posts.get(i).getEvent());
            Hibernate.initialize(posts.get(i).getUser());
        }

        session.getTransaction().commit();
        session.close();

        return posts;
    }
}
