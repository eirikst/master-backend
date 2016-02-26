package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.Event;

import java.util.List;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    Event createEvent(Event event, int adminId);
    List<Event> getAttendingEvents(int userId);
    List<Event> getAdminEvents(int userId);
}
