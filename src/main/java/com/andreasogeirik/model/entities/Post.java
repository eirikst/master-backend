package com.andreasogeirik.model.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "posts")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Post {
    private int id;
    private String message;
    private Date timeCreated;
    private String imageUri;
    private User writer;
    private User user;
    private Event event;
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<PostLike> likes = new HashSet<PostLike>(0);

    public Post() {
    }

    public Post(String message, String imageUri) {
        this.message = message;
        this.imageUri = imageUri;
    }

    public Post(int id, String message, String imageUri) {
        this.id = id;
        this.message = message;
        this.imageUri = imageUri;
    }

    public Post(String message, String imageUri, User writer, User user) {
        this.message = message;
        this.imageUri = imageUri;
        this.writer = writer;
        this.user = user;
    }


    public Post(String message, String imageUri, User writer, Event event) {
        this.message = message;
        this.imageUri = imageUri;
        this.writer = writer;
        this.event = event;
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
    @JoinColumn(name = "writer_id", nullable = false)
    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
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
    public Set<PostLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<PostLike> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", timeCreated=" + timeCreated +
                ", imageUri='" + imageUri + '\'' +
                ", writer=" + writer +
                ", user=" + user +
                ", event=" + event +
                ", comments=" + comments +
                ", likes=" + likes +
                '}';
    }
}
