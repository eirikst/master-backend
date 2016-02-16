package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.Event;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    Event createEvent(Event event, int adminId);
}
