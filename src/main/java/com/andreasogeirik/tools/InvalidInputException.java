package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException() {
        super();
    }
    public InvalidInputException(String message) {
        super(message);
    }
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
