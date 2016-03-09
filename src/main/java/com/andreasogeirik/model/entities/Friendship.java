package com.andreasogeirik.model.entities;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "friendship", uniqueConstraints=
@UniqueConstraint(columnNames={"friend1_id", "friend2_id"}))
public class Friendship {
    public static final int FRIENDS = 1;
    public static final int FRIEND1_REQUEST_FRIEND2 = 2;
    public static final int FRIEND2_REQUEST_FRIEND1 = 3;

    private int id;
    private Date friendsSince;
    private int status;
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

    @Column(name="status", nullable=false)
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(name="friends_since", nullable=false)
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

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", friendsSince=" + friendsSince +
                ", status=" + status +
                ", friend1=" + friend1 +
                ", friend2=" + friend2 +
                '}';
    }
}
