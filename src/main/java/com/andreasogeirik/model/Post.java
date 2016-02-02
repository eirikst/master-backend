package com.andreasogeirik.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "posts")
public class Post {

    private int id;
    private String message;
    private Date timeCreated;
    private String imageUri;
    private User user;
    private Event event;
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<PostLikes> likes = new HashSet<PostLikes>(0);

    public Post() {
    }

    public Post(String message, Date timeCreated, String imageUri, User user) {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    public Set<PostLikes> getLikes() {
        return likes;
    }

    public void setLikes(Set<PostLikes> likes) {
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
