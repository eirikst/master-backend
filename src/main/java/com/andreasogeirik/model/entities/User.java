package com.andreasogeirik.model.entities;

import java.util.*;
import javax.persistence.*;

/**
 * Created by eirikstadheim on 28/01/16.
 */
@Entity
@Table(name = "users")
public class User {

    private int id;
    private String password;
    private String email;
    private boolean enabled;
    private String firstname;
    private String lastname;
    private String location;
    private String imageUri;
    private String thumbUri;
    private Date timeCreated;
    private Set<UserRole> userRole = new HashSet<UserRole>(0);
    private Set<Post> postsOnPage = new HashSet<Post>(0);
    private Set<Post> postsIWrote = new HashSet<Post>(0);
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<Event> events = new HashSet<Event>(0);
    private Set<Friendship> friends = new HashSet<Friendship>(0);
    private Set<PostLike> userPostLikes = new HashSet<PostLike>(0);
    private Set<CommentLike> commentLikes = new HashSet<CommentLike>(0);
    private Set<String> gcmTokens = new HashSet<String>(0);
    private Set<LogElement> elements = new HashSet<LogElement>(0);

    public User() {
    }

    public User(String email, String password, boolean enabled, String firstname, String lastname,
                String location, Date timeCreated) {
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.firstname = firstname;
        this.lastname = lastname;
        this.location = location;
        this.timeCreated = timeCreated;
    }


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true,
            nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "email", unique = true,
            nullable = false, length = 60)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password",
            nullable = false, length = 60)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(length = 60, nullable = false)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Column(length = 60, nullable = false)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Column(length = 100)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(length = 300)
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    @Column(nullable = false)
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    public Set<UserRole> getUserRole() {
        return this.userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<Post> getPostsOnPage() {
        return postsOnPage;
    }

    public void setPostsOnPage(Set<Post> postsOnPage) {
        this.postsOnPage = postsOnPage;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "writer")
    public Set<Post> getPostsIWrote() {
        return postsIWrote;
    }

    public void setPostsIWrote(Set<Post> postsIWrote) {
        this.postsIWrote = postsIWrote;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "friend1")
    public Set<Friendship> getFriends() {
        return friends;
    }

    public void setFriends(Set<Friendship> friends) {
        this.friends = friends;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<PostLike> getUserPostLikes() {
        return userPostLikes;
    }

    public void setUserPostLikes(Set<PostLike> likes) {
        this.userPostLikes = likes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<CommentLike> getCommentLikes() {
        return commentLikes;
    }

    public void setCommentLikes(Set<CommentLike> commentLikes) {
        this.commentLikes = commentLikes;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @ElementCollection
    public Set<String> getGcmTokens() {
        return gcmTokens;
    }

    public void setGcmTokens(Set<String> gcmTokens) {
        this.gcmTokens = gcmTokens;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<LogElement> getElements() {
        return elements;
    }

    public void setElements(Set<LogElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", location='" + location + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", thumbUri='" + thumbUri + '\'' +
                ", timeCreated=" + timeCreated +
                ", userRole=" + userRole +
                ", gcmTokens=" + gcmTokens +
                ", elements=" + elements +
                '}';
    }
}