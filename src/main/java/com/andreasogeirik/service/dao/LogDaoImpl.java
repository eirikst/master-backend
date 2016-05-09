package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.service.dao.interfaces.LogDao;
import com.andreasogeirik.tools.Constants;
import com.andreasogeirik.tools.EntityNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public class LogDaoImpl implements LogDao {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    private static int CREATE_EVENT = 1;
    private static int MODIFY_EVENT = 2;


    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<LogElement> getLog(int userId, int offset) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        String hql = "SELECT L FROM LogElement L WHERE L.user = (:user) ORDER BY L.time DESC, L.id DESC";

        Query query = session.createQuery(hql).setParameter("user", user).setFirstResult(offset)
                .setMaxResults(Constants.NUMBER_OF_LOG_ELEMENTS_RETURNED);

        List<LogElement> log = query.list();


        session.getTransaction().commit();
        session.close();

        return log;
    }

    @Override
    public List<LogElement> getNewLogElements(int userId, int lastLogId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        LogElement logElement = session.get(LogElement.class, lastLogId);
        if (logElement == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Cannot find the specified log element.");
        }


        User user = session.get(User.class, userId);
        String hql = "SELECT L FROM LogElement L WHERE L.user = (:user) AND L.time >= (:time) AND L.id > (:id) ORDER BY L.time DESC, L.id DESC";

        Query query = session.createQuery(hql).setParameter("user", user).setTimestamp("time", logElement.getTime()).
                setInteger("id", logElement.getId()).setMaxResults(Constants.MAX_LOG_ELEMENTS_ON_UPDATE);

        List<LogElement> log = query.list();


        session.getTransaction().commit();
        session.close();

        return log;
    }

    @Override
    public void userRegistered(int userId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                LogElement element = new LogElement(user, new Date(), "Du opprettet en bruker og logget inn for " +
                        "første gang", ContentType.USER_REGISTERED, userId);
                session.save(element);

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * To all friends of admin and to the admin himself
     */
    @Override
    public void eventCreated(int eventId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                Event event = session.get(Event.class, eventId);
                if (event == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified event.");
                }

                User user = event.getAdmin();
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the admin of the event.");
                }

                String hql = "FROM Friendship F WHERE  (F.friend1.id = " + user.getId() + " OR F.friend2.id = " +
                        user.getId() + ")" + " AND F.status = " + Friendship.FRIENDS;
                Query query = session.createQuery(hql);
                List<Friendship> friendships = (List<Friendship>) query.list();

                Set<User> receivers = new HashSet<>();

                for (Friendship friendship : friendships) {
                    if (friendship.getFriend1().getId() != user.getId()) {
                        receivers.add(friendship.getFriend1());
                    } else {
                        receivers.add(friendship.getFriend2());
                    }
                }

                Date now = new Date();

                //log to the admin of the event
                LogElement userElement = new LogElement(user, now, "Du opprettet aktiviteten " + event.getName(),
                        ContentType.CREATE_EVENT, eventId);
                session.save(userElement);

                //loop through friends of admin
                for (User receiver : receivers) {
                    LogElement element = new LogElement(receiver, now, user.getFirstname() + " " + user.getLastname() +
                            " opprettet aktiviteten " + event.getName(), ContentType.CREATE_EVENT, eventId);
                    session.save(element);
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * To all participants WITHOUT the admin, which makes the change
     */
    @Override
    public void eventModified(int eventId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                Event event = session.get(Event.class, eventId);
                if (event == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified event.");
                }

                Date now = new Date();

                User admin = event.getAdmin();

                //loop through all participants
                for (User receiver : event.getUsers()) {
                    //ignore admin
                    if(receiver.getId() != admin.getId()) {
                        LogElement element = new LogElement(receiver, now, admin.getFirstname() + " " + admin.getLastname() +
                                " endret aktiviteten " + event.getName(), ContentType.MODIFY_EVENT, eventId);
                        session.save(element);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * Notify friends of the user attending and the admin of the event
     */
    @Override
    public void eventAttended(int eventId, int userId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                Set<User> users = new HashSet<>();


                Event event = session.get(Event.class, eventId);
                if (event == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified event.");
                }

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                String hql = "FROM Friendship F WHERE  (F.friend1.id = " + user.getId() + " OR F.friend2.id = " +
                        user.getId() + ")" + " AND F.status = " + Friendship.FRIENDS;
                Query query = session.createQuery(hql);
                List<Friendship> friendships = (List<Friendship>) query.list();


                for (Friendship friendship : friendships) {
                    if (friendship.getFriend1().getId() != userId) {
                        users.add(friendship.getFriend1());
                    } else {
                        users.add(friendship.getFriend2());
                    }
                }

                Date now = new Date();

                //notify admin
                if(userId != event.getAdmin().getId()) {
                    LogElement adminElement = new LogElement(event.getAdmin(), now, user.getFirstname() + " " + user.getLastname() +
                            " deltar på " + event.getName(), ContentType.PARTICIPATE_EVENT, eventId);
                    session.save(adminElement);
                }

                for (User receiver : users) {
                    //notify friend of user
                    LogElement element = new LogElement(receiver, now, user.getFirstname() + " " + user.getLastname() +
                            " deltar på " + event.getName(), ContentType.PARTICIPATE_EVENT, eventId);
                    session.save(element);
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * Notify participants of event
     */
    @Override
    public void eventPosted(int eventId, int userId, String msg) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User writer = session.get(User.class, userId);
                if (writer == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified writer.");
                }

                Event event = session.get(Event.class, eventId);
                if (event == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified event.");
                }

                Date now = new Date();

                //notify self??
                /*LogElement writerElement = new LogElement(writer, now, "Du skrev et innlegg i " + event.getName()
                        + ": \"" + msg + "\"",
                        ContentType.POST_EVENT, event.getId());
                session.save(writerElement);*/



                for(User user: event.getUsers()) {
                    if(user.getId() != userId) {
                        LogElement element = new LogElement(user, now, writer.getFirstname() + " " + writer.getLastname()
                                + " skrev et innlegg i " + event.getName() + ": \"" + msg + "\"",
                                ContentType.POST_EVENT, event.getId());
                        session.save(element);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * Notify poster and commenters on the post
     */
    @Override
    public void eventCommented(int postId, int writerId, String msg) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User commentWriter = session.get(User.class, writerId);
                if (commentWriter == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified writer.");
                }

                Post post = session.get(Post.class, postId);
                if (post == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified post.");
                }

                User postWriter = post.getWriter();

                if(postWriter == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("The post has no writer.");
                }

                Event event = post.getEvent();

                if(event == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("This is not an event post.");
                }

                Date now = new Date();

                //notify poster
                if(postWriter.getId() != writerId) {
                    LogElement posterElement = new LogElement(postWriter, now, commentWriter.getFirstname() + " " +
                            commentWriter.getLastname() + " kommenterte innlegget ditt i " + event.getName()
                            + ": \"" + msg + "\"", ContentType.COMMENT_EVENT, event.getId());
                    session.save(posterElement);
                }


                Set<Comment> comments = post.getComments();

                //notify commenters
                for(Comment comment: comments) {
                    if(comment.getUser().getId() != writerId && comment.getUser().getId() != postWriter.getId()) {
                        if (postWriter.getId() == writerId) {
                            LogElement writerElement = new LogElement(comment.getUser(), now, commentWriter.getFirstname() + " " +
                                    commentWriter.getLastname() + " kommenterte sitt eget innlegg i " +
                                    event.getName() + ": \"" + msg + "\"", ContentType.COMMENT_EVENT, event.getId());
                            session.save(writerElement);
                        }
                        else if(postWriter.getId() == comment.getUser().getId()) {
                            LogElement writerElement = new LogElement(comment.getUser(), now, commentWriter.getFirstname() + " " +
                                    commentWriter.getLastname() + " kommenterte ditt innlegg i " + event.getName()
                                    + ": \"" + msg + "\"", ContentType.COMMENT_EVENT, event.getId());
                            session.save(writerElement);
                        }
                        else {
                            LogElement userElement = new LogElement(comment.getUser(), now, commentWriter.getFirstname() + " " +
                                    commentWriter.getLastname() +
                                    " kommenterte et innlegg i " + event.getName()  + ": \"" + msg + "\"",
                                    ContentType.COMMENT_EVENT, event.getId());
                            session.save(userElement);
                        }
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    @Override
    public void userPosted(int userId, int writerId, String msg) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                Date now = new Date();

                String hql = "FROM Friendship F WHERE  (F.friend1.id = " + user.getId() + " OR F.friend2.id = " +
                        user.getId() + ")" + " AND F.status = " + Friendship.FRIENDS;
                Query query = session.createQuery(hql);
                List<Friendship> friendships = (List<Friendship>) query.list();


                for (Friendship friendship : friendships) {
                    if (friendship.getFriend1().getId() != userId) {
                        LogElement element = new LogElement(friendship.getFriend1(), now, user.getFirstname() + " " + user.getLastname() +
                                " la ut et innlegg på siden sin: \"" + msg + "\"", ContentType.POST_USER, userId);
                        session.save(element);
                    } else {
                        LogElement element = new LogElement(friendship.getFriend2(), now, user.getFirstname() + " " + user.getLastname() +
                                " la ut et innlegg på siden sin: \"" + msg + "\"", ContentType.POST_USER, userId);
                        session.save(element);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    @Override
    public void userCommented(int userId, int writerId, int postId, String msg) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                User writer = session.get(User.class, writerId);
                if (writer == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified writer.");
                }

                Post post = session.get(Post.class, postId);
                if (post == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified post.");
                }

                Date now = new Date();

                if(userId != writerId) {
                    LogElement userElement = new LogElement(user, now, writer.getFirstname() + " " + writer.getLastname() +
                            " kommenterte på et innlegg på siden din: \"" + msg + "\"", ContentType.COMMENT_USER, userId);
                    session.save(userElement);

                    LogElement writerElement = new LogElement(writer, now, "Du kommenterte på et innlegg på siden til " +
                            user.getFirstname() + " " + user.getLastname() + ": \"" + msg + "\"",
                            ContentType.COMMENT_USER, userId);
                    session.save(writerElement);
                }

                Set<Comment> comments = post.getComments();

                for(Comment comment: comments) {
                    if(userId != writerId && comment.getUser().getId() != writerId && comment.getUser().getId() != userId) {
                        LogElement userElement = new LogElement(comment.getUser(), now, writer.getFirstname() + " " +
                                writer.getLastname() +
                                " kommenterte på et innlegg på siden til " + user.getFirstname() + " " +
                                user.getLastname() + ": \"" + msg + "\"", ContentType.COMMENT_USER, userId);
                        session.save(userElement);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    @Override
    public void friendshipAccepted(int userId1, int userId2) {

        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                Set<User> users = new HashSet<>();
                Set<User> user1Friends = new HashSet<>();
                Set<User> user2Friends = new HashSet<>();

                User user1 = session.get(User.class, userId1);
                if (user1 == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                User user2 = session.get(User.class, userId2);
                if (user2 == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                String hql = "FROM Friendship F WHERE  (F.friend1.id = " + userId1 + " OR F.friend2.id = " +
                        userId1 + ")" + " AND F.status = " + Friendship.FRIENDS;
                Query query = session.createQuery(hql);
                List<Friendship> friendships1 = (List<Friendship>) query.list();


                for (Friendship friendship : friendships1) {
                    if(friendship.getStatus() == Friendship.FRIENDS) {
                        if (friendship.getFriend1().getId() != userId1) {
                            users.add(friendship.getFriend1());
                            user1Friends.add(friendship.getFriend1());
                        } else {
                            users.add(friendship.getFriend2());
                            user1Friends.add(friendship.getFriend2());
                        }
                    }
                }

                String hql1 = "FROM Friendship F WHERE  (F.friend1.id = " + userId2 + " OR F.friend2.id = " +
                        userId2 + ")" + " AND F.status = " + Friendship.FRIENDS;
                Query query1 = session.createQuery(hql1);
                List<Friendship> friendships2 = (List<Friendship>) query1.list();


                for (Friendship friendship : friendships2) {
                    if(friendship.getStatus() == Friendship.FRIENDS) {
                        if (friendship.getFriend1().getId() != userId2) {
                            users.add(friendship.getFriend1());
                            user2Friends.add(friendship.getFriend1());
                        } else {
                            users.add(friendship.getFriend2());
                            user2Friends.add(friendship.getFriend2());
                        }
                    }
                }

                Date now = new Date();

                //add to the two friends log that they are friends
                LogElement element1 = new LogElement(user1, now, "Du ble venn med " + user2.getFirstname() +
                        " " + user2.getLastname(), ContentType.FRIENDSHIP, user2.getId());
                session.save(element1);

                LogElement element2 = new LogElement(user2, now, "Du ble venn med " + user1.getFirstname() +
                        " " + user1.getLastname(), ContentType.FRIENDSHIP, user1.getId());
                session.save(element2);


                //add to the friends of the two friends that they have become friends
                for (User receiver : users) {
                    if (receiver.getId() != user1.getId() && receiver.getId() != user2.getId()) {

                        if(user1Friends.contains(receiver)) {
                            LogElement element = new LogElement(receiver, now, user1.getFirstname() + " " +
                                    user1.getLastname() + " ble venn med " + user2.getFirstname() + " " +
                                    user2.getLastname(), ContentType.FRIENDSHIP, user1.getId());
                            session.save(element);
                        }
                        else {
                            LogElement element = new LogElement(receiver, now, user2.getFirstname() + " " +
                                    user2.getLastname() + " ble venn med " + user1.getFirstname() + " " +
                                    user1.getLastname(), ContentType.FRIENDSHIP, user2.getId());
                            session.save(element);
                        }
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * Content id is userId of user post, eventId of event post
     */
    @Override
    public void postLiked(int userId, int postId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                Post post = session.get(Post.class, postId);
                if (post == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified post.");
                }

                int length = 30;
                if(post.getMessage().length() < 31) {
                    length = post.getMessage().length();
                }
                String msg = post.getMessage().substring(0, length);
                if(post.getMessage().length() > msg.length()) {
                    msg += "...";
                }

                User writer = post.getWriter();

                if(writer == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find writer of the comment.");
                }

                if(post.getUser() != null) {
                    if(writer.getId() != userId) {
                        LogElement element = new LogElement(writer, new Date(), user.getFirstname() + " " + user.getLastname() +
                                " likte innlegget ditt: \"" + msg + "\"", ContentType.LIKE_USER_POST, post.getUser().getId());
                        session.save(element);
                    }
                }
                else if(post.getEvent() != null) {
                    if(writer.getId() != userId) {
                        LogElement element = new LogElement(writer, new Date(), user.getFirstname() + " " + user.getLastname() +
                                " likte innlegget ditt: \"" + msg + "\"", ContentType.LIKE_EVENT_POST, post.getEvent().getId());
                        session.save(element);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }

    /*
     * Content id is
     */
    @Override
    public void commentLiked(int userId, int commentId) {
        new Thread() {
            public void run() {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                User user = session.get(User.class, userId);
                if (user == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified user.");
                }

                Comment comment = session.get(Comment.class, commentId);
                if (comment == null) {
                    session.getTransaction().commit();
                    session.close();
                    throw new EntityNotFoundException("Cannot find the specified comment.");
                }


                int length = 30;
                if(comment.getMessage().length() < 31) {
                    length = comment.getMessage().length();
                }
                String msg = comment.getMessage().substring(0, length);
                if (comment.getMessage().length() > msg.length()) {
                    msg += "...";
                }

                Post post = comment.getPost();

                User writer = comment.getUser();

                if (post.getUser() != null) {
                    if (writer.getId() != userId) {
                        LogElement element = new LogElement(writer, new Date(), user.getFirstname() + " " + user.getLastname() +
                                " likte kommentaren din: \"" + msg + "\"", ContentType.LIKE_USER_COMMENT, post.getUser().getId());
                        session.save(element);
                    }
                } else if (post.getEvent() != null) {
                    if (comment.getUser().getId() != userId) {
                        LogElement element = new LogElement(writer, new Date(), user.getFirstname() + " " + user.getLastname() +
                                " likte kommentaren din: \"" + msg + "\"", ContentType.LIKE_EVENT_COMMENT, post.getEvent().getId());
                        session.save(element);
                    }
                }

                session.getTransaction().commit();
                session.close();
            }
        }.start();
    }
}
