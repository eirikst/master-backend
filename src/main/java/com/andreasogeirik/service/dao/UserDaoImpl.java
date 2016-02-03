package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.User;
import com.andreasogeirik.model.UserRole;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.InputManager;
import com.andreasogeirik.tools.Codes;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public int createUser(String email, String password, String firstname, String lastname, String location) {
        return createUser(email, password, firstname, lastname, location, ROLE_USER);
    }

    /*
   * Creates new user with role ADMIN
   */
    @Override
    public int createAdminUser(String email, String password, String firstname, String lastname, String location) {
        return createUser(email, password, firstname, lastname, location, ROLE_ADMIN);
    }

    /*
     * Creates a new user in the DB, with given username, password, email and role
     */
    private int createUser(String email, String password, String firstname, String lastname, String location, int role) {
        if(!inputManager.isValidEmail(email)) {
            return Codes.INVALID_EMAIL;
        }
        if(!inputManager.isValidPassword(password)) {
            return Codes.INVALID_PASSWORD;
        }
        if(!inputManager.isValidName(firstname)) {
            return Codes.INVALID_FIRSTNAME;
        }
        if(!inputManager.isValidName(lastname)) {
            return Codes.INVALID_LASTNAME;
        }
        if(!inputManager.isValidLocation(location)) {
            return Codes.INVALID_LOCATION;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //These are called here to include them in the transaction that adds the new user, to avoid duplication
        if(emailExists(email, session)) {
            return Codes.EMAIL_EXISTS;
        }

        User user = new User(email, passwordEncoder.encode(password), true, firstname, lastname, location, new Date());//true for enabled user

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

        return Codes.OK;
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
     * Finds a User entity based on email
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

        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("id", id))
                .uniqueResult();

        session.close();

        return user;
    }

}
