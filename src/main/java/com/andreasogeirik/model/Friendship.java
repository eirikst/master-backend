package com.andreasogeirik.model;

import javax.persistence.*;
import java.util.Date;

enum Status { FRIEND1_REQUESTS_FRIEND2, FRIEND2_REQUESTS_FRIEND1, FRIENDS};

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "friendship")
public class Friendship {

    private int id;
    private Date friendsSince;
    private Status status;
    private User friend1;
    private User friend2;

    public Friendship() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "friends_id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(Date timeCreated) {
        this.friendsSince = timeCreated;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend1_id", nullable = false)
    public User getFriend1() {
        return this.friend1;
    }

    public void setFriend1(User friend1) {
        this.friend1 = friend1;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend2_id", nullable = false)
    public User getFriend2() {
        return this.friend2;
    }

    public void setFriend2(User friend2) {
        this.friend2 = friend2;
    }
}
