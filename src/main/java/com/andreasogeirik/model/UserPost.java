package com.andreasogeirik.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "user_posts")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class UserPost {
    private int id;
    private String message;
    private Date timeCreated;
    private String imageUri;
    private User user;
    private Set<UserPostComment> comments = new HashSet<UserPostComment>(0);
    private Set<UserPostLike> likes = new HashSet<UserPostLike>(0);

    public UserPost() {
    }

    public UserPost(String message, Date timeCreated, String imageUri, User user) {
        this.message = message;
        this.imageUri = imageUri;
        this.timeCreated = timeCreated;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "post_id", unique = true, nullable = false)
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    public Set<UserPostComment> getComments() {
        return comments;
    }

    public void setComments(Set<UserPostComment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    public Set<UserPostLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<UserPostLike> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", timeCreated=" + timeCreated +
                ", imageUri='" + imageUri + '\'' +
                ", user=" + user +
                '}';
    }
}
