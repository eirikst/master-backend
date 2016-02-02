package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.User;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    public static final int OK = 1;
    public static final int INVALID_PASSWORD = -1;
    public static final int INVALID_EMAIL = -2;
    public static final int INVALID_FIRSTNAME = -3;
    public static final int INVALID_LASTNAME = -4;
    public static final int INVALID_LOCATION = -5;
    public static final int USERNAME_EXISTS = -6;
    public static final int EMAIL_EXISTS = -7;

    public int newUser(String email, String password, String firstname, String lastname, String location);
    public int newAdminUser(String email, String password, String firstname, String lastname, String location);
    public User findByEmail(String email);
    public User findById(int id);
}
