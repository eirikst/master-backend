package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserRole;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.gcm.GcmService;
import com.andreasogeirik.tools.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public class UserDaoImpl implements UserDao {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InputManager inputManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GcmService gcmService;

    /*
     * Creates new user with role USER
     */
    @Override
    public User createUser(User user) {
        return createUser(user, ROLE_USER);
    }


    /*
   * Creates new user with role ADMIN
   */
    @Override
    public User createAdminUser(User user) {
        return createUser(user, ROLE_ADMIN);
    }

    /*
     * Creates a new user in the DB, with given username, password, email and role
     */
    private User createUser(User user, int role) {
        if(!inputManager.isValidEmail(user.getEmail())) {
            throw new InvalidInputException("Invalid email format");
        }
        if(!inputManager.isValidPassword(user.getPassword())) {
            throw new InvalidInputException("Invalid password format");
        }
        if(!inputManager.isValidName(user.getFirstname())) {
            throw new InvalidInputException("Invalid firstname format");
        }
        if(!inputManager.isValidName(user.getLastname())) {
            throw new InvalidInputException("Invalid lastname format");
        }
        if(!inputManager.isValidLocation(user.getLocation())) {
            throw new InvalidInputException("Invalid location format");
        }

        user.setTimeCreated(new Date());//created now
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //This is called here to include them in the transaction that adds the new user, to avoid duplication
        if(emailExists(user.getEmail(), session)) {
            throw new EmailExistsException("Email already exists in system");
        }

        session.save(user);

        UserRole userRole = new UserRole(user, "USER");
        session.save(userRole);

        if(role == ROLE_ADMIN) {
            //admins have both role USER and ADMIN
            UserRole adminRole = new UserRole(user, "ADMIN");
            session.save(adminRole);
        }

        session.getTransaction().commit();
        session.close();

        return user;
    }

    /*
 * Updates a user
 */
    public User updateUser(String firstname, String lastname, String location, String imageUri, int userId) {
        if(!inputManager.isValidName(firstname)) {
            throw new InvalidInputException("Invalid firstname format");
        }
        if(!inputManager.isValidName(lastname)) {
            throw new InvalidInputException("Invalid lastname format");
        }
        if(!inputManager.isValidLocation(location)) {
            throw new InvalidInputException("Invalid location format");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);

        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setLocation(location);
        user.setImageUri(imageUri);

        session.save(user);
        session.getTransaction().commit();
        session.close();

        return user;
    }


    /*
     * Checks if a user with given email exists. An open session must be provided, with an ongoing transaction
     */
    private boolean emailExists(String email, Session session) {
        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("email", email))
                .uniqueResult();

        return user != null;
    }

    /*
     * Finds a User entity based on email. Also initializes the user's roles(getUserRoles)
     */
    @Transactional(readOnly = true)
    @Override
    public User findByEmail(String email) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("email", email))
                .uniqueResult();
        if(user != null) {
            Hibernate.initialize(user.getUserRole());
        }

        session.close();

        return user;
    }

    /*
    * Finds a User entity based on id
    */
    @Transactional(readOnly = true)
    @Override
    public User findById(int id) {
        Session session = sessionFactory.openSession();
        User user = session.get(User.class, id);
        session.close();
        return user;
    }


    /*
     * Finds all friends and requests to/from the user(as friendships)
     */
    public List<Friendship> findFriendsAndRequests(int userId) {
        Session session = sessionFactory.openSession();

        //the given user can be set on both friend1 and friend2, needs two queries
        String hql = "FROM Friendship F WHERE  F.friend1.id = " + userId + " OR F.friend2.id = " + userId;
        Query query = session.createQuery(hql);
        List<Friendship> friendships = (List<Friendship>)query.list();

        Iterator<Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            Friendship friendship = it.next();
            Hibernate.initialize(friendship.getFriend1());
            Hibernate.initialize(friendship.getFriend2());
            Hibernate.initialize(friendship.getStatus());
            Hibernate.initialize(friendship.getFriendsSince());
        }

        session.close();
        return friendships;
    }

    /*
     * Finds all friends of the specified user, ignores requests
     */
    public List<Friendship> findFriends(int userId) {
        Session session = sessionFactory.openSession();

        //the given user can be set on both friend1 and friend2, needs two queries
        String hql = "FROM Friendship F WHERE F.status = " + Friendship.FRIENDS + " AND (F.friend1.id = " + userId +
                " OR F.friend2.id = " + userId + ")";
        Query query = session.createQuery(hql);
        List<Friendship> friendships = (List<Friendship>)query.list();

        Iterator<Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            Friendship friendship = it.next();
            Hibernate.initialize(friendship.getFriend1());
            Hibernate.initialize(friendship.getFriend2());
            Hibernate.initialize(friendship.getStatus());
            Hibernate.initialize(friendship.getFriendsSince());
        }

        session.close();
        return friendships;
    }

    /*
     * The request is from userId1 to userId2
     */
    @Override
    public Friendship addFriendRequest(int userId1, int userId2) {
        if(userId1 == userId2) {
            throw new EntityConflictException("Id 1 and Id 2 is both " + userId1);
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User friend1 = session.get(User.class, userId1);
        User friend2 = session.get(User.class, userId2);

        if(friend1 == null || friend2 == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Entity not found");
        }

        Friendship friendship = new Friendship();
        friendship.setFriendsSince(new Date());


        //set the lowest id first, so that mutual friend requests is avoided(both cannot request each other)
        if(userId1 < userId2) {
            friendship.setFriend1(friend1);
            friendship.setFriend2(friend2);
            friendship.setStatus(Friendship.FRIEND1_REQUEST_FRIEND2);

        }
        else {
            friendship.setFriend1(friend2);
            friendship.setFriend2(friend1);
            friendship.setStatus(Friendship.FRIEND2_REQUEST_FRIEND1);
        }

        session.save(friendship);

        String nameOfRequester = friend1.getFirstname() + " " + friend1.getLastname();

        session.getTransaction().commit();
        session.close();

        //call gcm service to notify user

        gcmService.notifyFriendRequest(nameOfRequester, userId2);

        return friendship;
    }

    @Override
    public void acceptFriendRequest(int friendshipId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        int userIdToNotify;
        String nameToNotify;

        Friendship friendship = session.get(Friendship.class, friendshipId);

        if(friendship == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Friendship with id " + friendshipId + " not found");
        }
        else if(friendship.getFriend2().getId() == userId) {
            friendship.setStatus(Friendship.FRIENDS);
            session.save(friendship);
            userIdToNotify = friendship.getFriend1().getId();
            nameToNotify = friendship.getFriend1().getFirstname();
        }
        else if(friendship.getFriend1().getId() == userId) {
            friendship.setStatus(Friendship.FRIENDS);
            session.save(friendship);
            userIdToNotify = friendship.getFriend2().getId();
            nameToNotify = friendship.getFriend2().getFirstname();
        }
        else {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user with user id " + userId + " is not part of the friendship");
        }

        session.getTransaction().commit();
        session.close();

        //notify user
        gcmService.notifyFriendAccepted(nameToNotify, userIdToNotify);
    }

    /*
     * When unfriending and reject friend request
     */
    @Override
    public void removeFriendship(int friendshipId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Friendship friendship = session.get(Friendship.class, friendshipId);

        if(friendship == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Friendship with id " + friendshipId + " not found");
        }
        else if(friendship.getFriend1().getId() == userId || friendship.getFriend2().getId() == userId) {
            session.delete(friendship);
        }
        else {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user with user id " + userId + " is not part of the friendship");
        }

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<User> searchUsers(String name, int offset) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        String hql = "FROM User U WHERE lower(concat(U.firstname, ' ', U.lastname)) LIKE lower((:name))";
        Query query = session.createQuery(hql).setParameter("name", "%" + name + "%");

        query.setFirstResult(offset);
        query.setMaxResults(Constants.NUMBER_OF_USERS_RETURNED_SEARCH);


        List<User> users = (List<User>)query.list();

        session.getTransaction().commit();
        session.close();
        return users;
    }

    @Override
    public Set<String> getGcmTokensByUserId(int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        Set<String> gcmTokens = new HashSet<String>(0);

        for(String token: user.getGcmTokens()) {
            gcmTokens.add(token);
        }

        session.getTransaction().commit();
        session.close();

        return gcmTokens;
    }

    @Override
    public void registerGcmToken(int userId, String gcmToken) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        user.getGcmTokens().add(gcmToken);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void removeGcmToken(int userId, String gcmToken) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        user.getGcmTokens().remove(gcmToken);

        session.getTransaction().commit();
        session.close();
    }
}
