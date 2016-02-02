package com.andreasogeirik.service.dao;

import java.util.Date;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    public static final int INVALID_NAME = -1;
    public static final int INVALID_LOCATION = -2;
    public static final int INVALID_DESCRIPTION = -3;
    public static final int INVALID_TIME_START = -4;
    public static final int INVALID_TIME_END = -5;
    public static final int INVALID_IMAGE_URI = -6;
    public static final int USER_NOT_FOUND = -7;

    public int newEvent(String name, String location, String description, Date timeStart, Date timeEnd, String imageURI,
                        int adminId);
}
