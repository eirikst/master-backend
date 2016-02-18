package com.andreasogeirik.model.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "events")
public class Event {

    private int id;
    private String name;
    private String location;
    private String description;
    private Date timeCreated;
    private Date timeStart;
    private Date timeEnd;
    private String imageURI;
    private User admin;
    private Set<User> users = new HashSet<User>(0);
    private Set<EventPost> posts = new HashSet<EventPost>(0);

    public Event() {
    }

    public Event(String name, String location, String description, Date timeStart) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.timeStart = timeStart;
    }

    public Event(String name, String location, String description, Date timeStart,
                 Date timeEnd, String imageURI) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.imageURI = imageURI;
    }

    public Event(String name, String location, String description, Date timeStart, String imageURI) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.timeStart = timeStart;
        this.imageURI = imageURI;
    }

    public Event(String name, String location, String description, Date timeCreated, Date timeStart,
                 Date timeEnd, String imageURI, User admin) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.timeCreated = timeCreated;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.imageURI = imageURI;
        this.admin = admin;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "event_id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(nullable = false)
    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    public Set<EventPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<EventPost> posts) {
        this.posts = posts;
    }
}
