package com.andreasogeirik.tools;

import java.util.Date;

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
    public static final int EVENT_NAME_LENGTH = 100;
    public static final int EVENT_DESCRIPTION_LENGTH = 1000;


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
        return Uri.length() <= URI_LENGTH;
    }

    public boolean isValidPost(String message) {
        return message.length() > 0 && message.length() <= POST_LENGTH;
    }

    public boolean isValidComment(String message) {
        return message.length() > 0 && message.length() <= COMMENT_LENGTH;
    }

    public boolean isValidEventName(String name) {
        return name.length() > 0 && name.length() <= EVENT_NAME_LENGTH;
    }

    public boolean isValidEventDescription(String name) {
        return name.length() > 0 && name.length() <= EVENT_DESCRIPTION_LENGTH;
    }
}