package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserRole;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.EmailExistsException;
import com.andreasogeirik.tools.InputManager;
import com.andreasogeirik.tools.InvalidInputException;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
     * Finds the set of friends of a user
     */
    public Set<User> findFriends(int userId) {
        Session session = sessionFactory.openSession();

        //the given user can be set on both friend1 and friend2, needs two queries
        String hql = "FROM Friendship F WHERE F.status = " + Friendship.FRIENDS + " AND F.friend1.id = " + userId;
        Query query = session.createQuery(hql);
        List<Friendship> friendships = (List<Friendship>)query.list();

        String hql2 = "FROM Friendship F WHERE F.status = " + Friendship.FRIENDS + " AND F.friend2.id = " + userId;
        Query query2 = session.createQuery(hql2);
        List<Friendship> friendships2 = (List<Friendship>)query2.list();

        Set<User> friends = new HashSet<>();

        Iterator<Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            User friend = it.next().getFriend2();
            Hibernate.initialize(friend);
            friends.add(friend);
        }

        Iterator<Friendship> it2 = friendships2.iterator();
        while(it2.hasNext()) {
            User friend = it2.next().getFriend1();
            Hibernate.initialize(friend);
            friends.add(friend);
        }

        session.close();
        return friends;
    }

    /*
     * Finds the set of friends of a user presented as i friendship, so that the status is also returned.
     * Status is used to see friend requests as well as friendships
     */
    public List<Friendship> findFriendships(int userId) {
        Session session = sessionFactory.openSession();

        //the given user can be set on both friend1 and friend2, needs two queries
        String hql = "FROM Friendship F WHERE (F.status = " + Friendship.FRIEND1_REQUEST_FRIEND2 + " OR F.status = " +
                Friendship.FRIEND2_REQUEST_FRIEND1 + ") AND (F.friend1.id = " + userId + " OR F.friend2.id = " +
                userId + ")";
        Query query = session.createQuery(hql);
        List<Friendship> friendships = (List<Friendship>)query.list();

        Set<Friendship> friends = new HashSet<>();

        Iterator<Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            Friendship friendship = it.next();
            Hibernate.initialize(friendship.getFriend1());
            Hibernate.initialize(friendship.getFriend2());
        }

        session.close();
        return friendships;
    }
}
