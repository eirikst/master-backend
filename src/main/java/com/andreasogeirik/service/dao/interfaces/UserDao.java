package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.User;

import java.util.Set;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    User createUser(User user);
    User createAdminUser(User user);
    User findByEmail(String email);
    User findById(int id);
    Set<User> findFriends(int userId);
}
