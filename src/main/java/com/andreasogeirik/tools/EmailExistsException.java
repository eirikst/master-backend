package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class EmailExistsException extends RuntimeException {
    public EmailExistsException() {
        super();
    }
    public EmailExistsException(String message) {
        super(message);
    }
    public EmailExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmailExistsException(Throwable cause) {
        super(cause);
    }
}
