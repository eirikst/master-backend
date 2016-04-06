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
    User updateUser(String firstname, String lastname, String location, String imageUri, int userId);
    User createAdminUser(User user);
    User findByEmail(String email);
    User findById(int id);

    List<Friendship> findFriendsAndRequests(int userId);
    List<Friendship> findFriends(int userId);

    Friendship addFriendRequest(int userId1, int userId2);
    void acceptFriendRequest(int friendshipId, int userId);
    void removeFriendship(int friendshipId, int userId);

    List<User> searchUsers(String name, int offset);

    Set<String> getGcmTokensByUserId(int userId);
    void registerGcmToken(int userId, String gcmToken);
    void removeGcmToken(int userId, String gcmToken);
}