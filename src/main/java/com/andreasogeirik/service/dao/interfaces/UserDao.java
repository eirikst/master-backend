package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.User;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    int createUser(User user);
    int createAdminUser(User user);
    User findByEmail(String email);
    User findById(int id);
}
