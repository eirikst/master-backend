package com.andreasogeirik.tools;

/**
 * Created by Andreas on 10.03.2016.
 */
public class UserExistenceException extends RuntimeException {
    public UserExistenceException() {
        super();
    }
    public UserExistenceException(String message) {
        super(message);
    }
    public UserExistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserExistenceException(Throwable cause) {
        super(cause);
    }
}
