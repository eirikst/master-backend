package com.andreasogeirik.service.dao.interfaces;

import com.andreasogeirik.model.entities.Event;

import java.util.List;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public interface EventDao {
    Event createEvent(Event event, int adminId);
    Event getEvent(int eventId);
    Event attendEvent(int eventId, int userId);
    Event unAttendEvent(int eventId, int userId);
    List<Event> getAttendingEvents(int userId);
    List<Event> getAttendedEvents(int userId, int start);
    List<Event> getAdminEvents(int userId);
    List<Event> getAdminEventsPast(int userId, int offset);
    List<Event> getRecommendedEvents(int userId, int offset);
}
