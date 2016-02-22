package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.User;

import java.util.List;
import java.util.Set;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public interface UserDao {
    User createUser(User user);
    User createAdminUser(User user);
    User findByEmail(String email);
    User findById(int id);

    List<Friendship> findFriendsAndRequests(int userId);
    List<Friendship> findFriends(int userId);

    Friendship addFriendRequest(int friendshipId, int userId);
    void acceptFriendRequest(int friendshipId, int userId);
    void removeFriendship(int friendshipId, int userId);
}