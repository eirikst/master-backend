package com.andreasogeirik.model.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eirikstadheim on 04/05/16.
 */
@Entity
@Table(name = "log")
public class LogElement {
    private int id;
    private User user;
    private Date time;
    private String content;
    private ContentType type;
    private int contentId;//id of the content to view when clicking the log element
    private int refId;// if of something to reference inside content: eg a comment inside a user

    public LogElement() {
    }

    public LogElement(User user, Date time, String content, ContentType type, int contentId) {
        this.user = user;
        this.time = time;
        this.content = content;
        this.type = type;
        this.contentId = contentId;
    }

    public LogElement(User user, Date time, String content, ContentType type, int contentId, int refId) {
        this.user = user;
        this.time = time;
        this.content = content;
        this.type = type;
        this.contentId = contentId;
        this.refId = refId;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "log_id", unique = true,
            nullable = false)
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }
}