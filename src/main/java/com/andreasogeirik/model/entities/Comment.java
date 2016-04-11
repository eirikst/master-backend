package com.andreasogeirik.model.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "comments")
public class Comment {

    private int id;
    private String message;
    private Date timeCreated;
    private User user;
    private Post post;
    private Set<CommentLike> likes = new HashSet<CommentLike>(0);

    public Comment() {
    }

    public Comment(String message) {
        this.message = message;
    }

    public Comment(String message, Date timeCreated, User user, Post post) {
        this.message = message;
        this.timeCreated = timeCreated;
        this.user = user;
        this.post = post;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "comment_id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(nullable = false)
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment")
    public Set<CommentLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<CommentLike> likes) {
        this.likes = likes;
    }
}
