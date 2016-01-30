package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 29/01/16.
 */
public class InputManager {
    public static final int USERNAME_LENGTH = 45;
    public static final int PASSWORD_LENGTH = 60;
    public static final int EMAIL_LENGTH = 60;

    public boolean isValidUsername(String username) {
        return !username.isEmpty() && username.length() <= USERNAME_LENGTH;
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 3 && password.length() <= PASSWORD_LENGTH;
    }

    //Needs more restrictions
    public boolean isValidEmail(String email) {
        return !email.isEmpty() && email.length() <= EMAIL_LENGTH;
    }

}
