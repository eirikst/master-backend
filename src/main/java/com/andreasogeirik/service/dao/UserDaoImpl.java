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
    public int createUser(User user) {
        return createUser(user, ROLE_USER);
    }

    /*
   * Creates new user with role ADMIN
   */
    @Override
    public int createAdminUser(User user) {
        return createUser(user, ROLE_ADMIN);
    }

    /*
     * Creates a new user in the DB, with given username, password, email and role
     */
    private int createUser(User user, int role) {
        if(!inputManager.isValidEmail(user.getEmail())) {
            return Codes.INVALID_EMAIL;
        }
        if(!inputManager.isValidPassword(user.getPassword())) {
            return Codes.INVALID_PASSWORD;
        }
        if(!inputManager.isValidName(user.getFirstname())) {
            return Codes.INVALID_FIRSTNAME;
        }
        if(!inputManager.isValidName(user.getLastname())) {
            return Codes.INVALID_LASTNAME;
        }
        if(!inputManager.isValidLocation(user.getLocation())) {
            return Codes.INVALID_LOCATION;
        }

        user.setTimeCreated(new Date());//created now
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //This is called here to include them in the transaction that adds the new user, to avoid duplication
        if(emailExists(user.getEmail(), session)) {
            return Codes.EMAIL_EXISTS;
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




    //TEST
    @Override
    public void insert() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = new User("1234567890123456789012345678901234567890123456789012345678901", "bror", true, "bror", "bror",
                "location", new Date());//true for enabled user

        session.save(user);

        UserRole userRole = new UserRole(user, "USER");
        session.save(userRole);


        session.getTransaction().commit();
        session.close();
    }
}
