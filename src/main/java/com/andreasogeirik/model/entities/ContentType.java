package com.andreasogeirik.model.entities;

public enum ContentType {
    USER_REGISTERED(0), CREATE_EVENT(1), MODIFY_EVENT(2), PARTICIPATE_EVENT(3), POST_EVENT(4), COMMENT_EVENT(5),
    POST_USER(6), COMMENT_USER(7), FRIENDSHIP(8), LIKE_USER_POST(9), LIKE_EVENT_POST(10), LIKE_USER_COMMENT(11),
    LIKE_EVENT_COMMENT(12), DELETE_EVENT(13);

    private int number;

    ContentType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}