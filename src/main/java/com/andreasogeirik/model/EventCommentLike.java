package com.andreasogeirik.model;

import javax.persistence.*;

;

/**
 * Created by eirikstadheim on 01/02/16.
 */
@Entity
@Table(name = "event_comment_likes", uniqueConstraints=@UniqueConstraint(columnNames={"comment_id", "user_id"}))
public class EventCommentLike {

    private int id;
    private User user;
    private EventPostComment comment;

    public EventCommentLike() {
    }

    public EventCommentLike(User user, EventPostComment comment) {
        this.user = user;
        this.comment = comment;
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
    @JoinColumn(name = "comment_id")
    public EventPostComment getComment() {
        return comment;
    }

    public void setComment(EventPostComment comment) {
        this.comment = comment;
    }
}
