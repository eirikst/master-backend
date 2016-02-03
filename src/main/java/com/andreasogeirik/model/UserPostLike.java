package com.andreasogeirik.model;

import javax.persistence.*;

;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "user_post_likes", uniqueConstraints=@UniqueConstraint(columnNames={"post_id", "user_id"}))
public class UserPostLike {
    private int id;
    private User user;
    private UserPost post;

    public UserPostLike() {
    }

    public UserPostLike(User user, UserPost post) {
        this.user = user;
        this.post = post;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    public UserPost getPost() {
        return post;
    }

    public void setPost(UserPost post) {
        this.post = post;
    }
}
