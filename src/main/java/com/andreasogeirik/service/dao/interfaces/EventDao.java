package com.andreasogeirik.service.dao.interfaces;

import java.util.Date;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    int INVALID_NAME = -1;
    int INVALID_LOCATION = -2;
    int INVALID_DESCRIPTION = -3;
    int INVALID_TIME_START = -4;
    int INVALID_TIME_END = -5;
    int INVALID_IMAGE_URI = -6;
    int USER_NOT_FOUND = -7;

    int createEvent(String name, String location, String description, Date timeStart, Date timeEnd, String imageURI,
                        String adminUsername);
}
