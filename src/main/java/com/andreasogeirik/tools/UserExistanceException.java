package com.andreasogeirik.tools;

/**
 * Created by Andreas on 10.03.2016.
 */
public class UserExistanceException extends RuntimeException {
    public UserExistanceException() {
        super();
    }
    public UserExistanceException(String message) {
        super(message);
    }
    public UserExistanceException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserExistanceException(Throwable cause) {
        super(cause);
    }
}
