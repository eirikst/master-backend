package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.Event;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    int createEvent(Event event, int adminId);
}
