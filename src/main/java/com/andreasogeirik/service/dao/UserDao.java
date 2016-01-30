package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.User;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    public static final int OK = 0;
    public static final int INVALID_USERNAME = 1;
    public static final int INVALID_EMAIL = 2;
    public static final int INVALID_PASSWORD = 3;
    public static final int USERNAME_EXISTS = 4;
    public static final int EMAIL_EXISTS = 5;

    public int newUser(String username, String password, String email);
    public int newAdminUser(String username, String password, String email);
    public User findByUsername(String username);
}
