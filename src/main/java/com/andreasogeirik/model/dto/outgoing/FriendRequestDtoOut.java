package com.andreasogeirik.model.dto.outgoing;

import com.andreasogeirik.model.entities.User;

import java.util.Date;

/**
 * Created by eirikstadheim on 16/02/16.
 */
public class FriendRequestDtoOut {
    private Date friendsSince;
    private boolean myRequest;
    private UserDtoOut friend;

    public FriendRequestDtoOut(Date friendsSince, boolean myRequest, User friend) {
        this.friendsSince = friendsSince;
        this.myRequest = myRequest;
        this.friend = new UserDtoOut(friend);
    }

    public Date getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(Date friendsSince) {
        this.friendsSince = friendsSince;
    }

    public boolean isMyRequest() {
        return myRequest;
    }

    public void setMyRequest(boolean myRequest) {
        this.myRequest = myRequest;
    }

    public UserDtoOut getFriend() {
        return friend;
    }

    public void setFriend(UserDtoOut friend) {
        this.friend = friend;
    }
}
