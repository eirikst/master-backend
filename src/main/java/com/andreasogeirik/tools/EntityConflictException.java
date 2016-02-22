package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class EntityConflictException extends RuntimeException {
    public EntityConflictException() {
        super();
    }
    public EntityConflictException(String message) {
        super(message);
    }
    public EntityConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public EntityConflictException(Throwable cause) {
        super(cause);
    }
}
