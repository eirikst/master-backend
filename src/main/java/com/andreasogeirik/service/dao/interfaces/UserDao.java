package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.User;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    int createUser(String email, String password, String firstname, String lastname, String location);
    int createAdminUser(String email, String password, String firstname, String lastname, String location);
    User findByEmail(String email);
    User findById(int id);
}
