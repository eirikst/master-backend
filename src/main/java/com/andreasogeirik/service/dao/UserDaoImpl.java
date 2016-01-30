package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.User;
import com.andreasogeirik.model.UserRole;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

/**
 * Created by eirikstadheim on 29/01/16.
 */

/*
 * !!!!!!!!!!!!!!!!
 * Se på transaction-handlingen her etterhvert
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
    public int newUser(String username, String password, String email) {
        return newUser(username, password, email, ROLE_USER);
    }

    /*
   * Creates new user with role ADMIN
   */
    @Override
    public int newAdminUser(String username, String password, String email) {
        return newUser(username, password, email, ROLE_ADMIN);
    }

    /*
     * Creates a new user in the DB, with given username, password, email and role
     */
    private int newUser(String username, String password, String email, int role) {
        if(!inputManager.isValidUsername(username)) {
            return INVALID_USERNAME;
        }
        if(!inputManager.isValidPassword(password)) {
            return INVALID_PASSWORD;
        }
        if(!inputManager.isValidEmail(email)) {
            return INVALID_EMAIL;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //These are called here to include them in the transaction that adds the new user, to avoid duplication
        if(usernameExists(username, session)) {
            return USERNAME_EXISTS;
        }
        if(emailExists(email, session)) {
            return EMAIL_EXISTS;
        }

        User user = new User(username, passwordEncoder.encode(password), email, true);//true for enabled user

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

        return OK;
    }

    /*
     * Checks if a user with given username exists. An open session must be provided, with an ongoing transaction
     */
    private boolean usernameExists(String username, Session session) {
        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("username", username))
                .uniqueResult();

        return user != null;
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
     * Checks if a user with given username exists
     */
    private boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    /*
     * Finds a User entity based on username
     */
    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        Session session = sessionFactory.openSession();

        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("username", username))
                .uniqueResult();
        if(user != null) {
            Hibernate.initialize(user.getUserRole());
        }

        session.close();

        return user;
    }
}
