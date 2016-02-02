package com.andreasogeirik.model;

import javax.persistence.*;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "likes")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Likes {

    private int id;
    private User user;

    public Likes() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "likes_id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
