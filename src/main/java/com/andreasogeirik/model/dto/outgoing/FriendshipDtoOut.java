package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.Friendship;

import java.util.Date;

/**
 * Created by eirikstadheim on 17/02/16.
 */
public class FriendshipDtoOut {
    public static final int FRIENDS = 1;
    public static final int I_REQUESTED = 2;
    public static final int FRIEND_REQUESTED = 3;

    private int id;
    private UserDtoOut friend;
    private Date friendsSince;
    private int status;

    public FriendshipDtoOut() {
    }

    public FriendshipDtoOut(int id, UserDtoOut friend, Date friendsSince, int status) {
        this.id = id;
        this.friend = friend;
        this.friendsSince = friendsSince;
        this.status = status;
    }

    /*
     * User id is ID of the friendship belongs to(typically logged in user or similar)
     */
    public FriendshipDtoOut(Friendship friendship, int userId) {
        id = friendship.getId();
        friendsSince = friendship.getFriendsSince();

        if (friendship.getFriend1().getId() == userId) {
            friend = new UserDtoOut(friendship.getFriend2());//set friend
            if(friendship.getStatus() == Friendship.FRIENDS) {
                status = FRIENDS;
            }
            else {
                if(friendship.getStatus() == Friendship.FRIEND1_REQUEST_FRIEND2) {
                    status = I_REQUESTED;
                }
                else {
                    status = FRIEND_REQUESTED;
                }
            }
        } else if (friendship.getFriend2().getId() == userId) {
            friend = new UserDtoOut(friendship.getFriend1());//set friend
            if(friendship.getStatus() == Friendship.FRIENDS) {
                status = FRIENDS;
            }
            else {
                if(friendship.getStatus() == Friendship.FRIEND1_REQUEST_FRIEND2) {
                    status = FRIEND_REQUESTED;
                }
                else {
                    status = I_REQUESTED;
                }
            }
        }
        else {
            throw new IllegalArgumentException("None of the friends matched the userId");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDtoOut getFriend() {
        return friend;
    }

    public void setFriend(UserDtoOut friend) {
        this.friend = friend;
    }

    public Date getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(Date friendsSince) {
        this.friendsSince = friendsSince;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
