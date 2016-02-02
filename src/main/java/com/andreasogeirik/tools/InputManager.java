package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public class InputManager {
    public static final int EMAIL_LENGTH = 60;
    public static final int PASSWORD_LENGTH = 60;
    public static final int NAME_LENGTH = 60;
    public static final int LOCATION_LENGTH = 60;
    public static final int URI_LENGTH = 200;
    public static final int POST_LENGTH = 1000;
    public static final int COMMENT_LENGTH = 500;


    //Needs more restrictions
    public boolean isValidEmail(String email) {
        return !email.isEmpty() && email.length() <= EMAIL_LENGTH;
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 3 && password.length() <= PASSWORD_LENGTH;
    }

    public boolean isValidName(String name) {
        return !name.isEmpty() && name.length() <= NAME_LENGTH;
    }

    public boolean isValidLocation(String location) {
        return !location.isEmpty() && location.length() <= LOCATION_LENGTH;
    }

    public boolean isValidURI(String Uri) {
        return Uri.length() >= URI_LENGTH;
    }

    public boolean isValidPost(String message) {
        return message.length() >= POST_LENGTH;
    }

    public boolean isValidComment(String message) {
        return message.length() >= COMMENT_LENGTH;
    }
}