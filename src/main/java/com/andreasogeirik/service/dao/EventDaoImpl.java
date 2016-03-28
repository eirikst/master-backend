package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Event;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.tools.*;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.*;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public class EventDaoImpl implements EventDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    InputManager inputManager;

    @Override
    public Event createEvent(Event event, int userId) {
        if(!inputManager.isValidEventName(event.getName())) {
            throw new InvalidInputException("Invalid name format");
        }
        if(!inputManager.isValidEventDescription(event.getDescription())) {
            throw new InvalidInputException("Invalid description format");
        }
        if(!inputManager.isValidLocation(event.getLocation())) {
            throw new InvalidInputException("Invalid location format");
        }
        if(event.getTimeStart().before(new Date())) {
            throw new InvalidInputException("Invalid start time");
        }
        if (event.getTimeEnd() != null){
            if(event.getTimeEnd().before(new Date())) {
                throw new InvalidInputException("Invalid end time");
            }
            if(event.getTimeEnd().before(event.getTimeStart())) {
                throw new InvalidInputException("End time cannot be before start time");
            }
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User admin = session.get(User.class, userId);
        event.setTimeCreated(new Date());
        event.setAdmin(admin);


        Set<User> users = new HashSet<>();
        users.add(admin);

        event.setUsers(users);

        session.save(event);

        session.getTransaction().commit();
        session.close();
        return event;
    }

    @Override
    public Event updateEvent(int userId, int eventId, Event event) {

        if(!inputManager.isValidEventName(event.getName())) {
            throw new InvalidInputException("Invalid name format");
        }
        if(!inputManager.isValidEventDescription(event.getDescription())) {
            throw new InvalidInputException("Invalid description format");
        }
        if(!inputManager.isValidLocation(event.getLocation())) {
            throw new InvalidInputException("Invalid location format");
        }
        if(event.getTimeStart().before(new Date())) {
            throw new InvalidInputException("Invalid start time");
        }
        if (event.getTimeEnd() != null){
            if(event.getTimeEnd().before(new Date())) {
                throw new InvalidInputException("Invalid end time");
            }
            if(event.getTimeEnd().before(event.getTimeStart())) {
                throw new InvalidInputException("End time cannot be before start time");
            }
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Event contextEvent = session.get(Event.class, eventId);

        // Check if user is the admin of the event
        if (contextEvent.getAdmin().getId() != userId){
            session.close();
            throw new EntityConflictException("The user is not allowed to update this event");
        }
        Hibernate.initialize(contextEvent.getUsers());
        contextEvent.updateAttributes(event);

        session.update(contextEvent);
        session.getTransaction().commit();
        session.close();

        return contextEvent;
    }

    @Override
    public Event deleteEvent(int userId, int eventId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Event event = session.get(Event.class, eventId);
        if (event.getAdmin().getId() != userId){
            session.close();
            throw new EntityConflictException("The user is not allowed to update this event");
        }

        session.delete(event);

        session.getTransaction().commit();
        session.close();

        return null;
    }

    @Override
    public Event getEvent(int eventId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Event event = session.get(Event.class, eventId);

        if (event == null){
            throw new EntityNotFoundException("The requested event does not exist");
        }

        Hibernate.initialize(event.getUsers());

        if (event == null){
            throw new EntityNotFoundException();
        }
        session.close();
        return event;
    }

    @Override
    public Event attendEvent(int eventId, int userId) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        Event event = session.get(Event.class, eventId);

        if (event == null){
            throw new EntityNotFoundException("The requested event does not exist");
        }
        Hibernate.initialize(event.getUsers());
        Set<User> users = event.getUsers();

        users.add(user);
        session.save(event);

        session.getTransaction().commit();
        session.close();
        return event;
    }

    @Override
    public Event unAttendEvent(int eventId, int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        Event event = session.get(Event.class, eventId);

        if (event == null){
            throw new EntityNotFoundException("The requested event does not exist");
        }
        Hibernate.initialize(event.getUsers());
        Set<User> users = event.getUsers();

        users.remove(user);

        session.save(event);

        session.getTransaction().commit();
        session.close();
        return event;
    }

    @Override
    public List<Event> getAttendingEvents(int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E JOIN E.users U WHERE (:user) = U AND E.timeStart > (:timeStart)";

        Query query = session.createQuery(hql).setParameter("user", user).setParameter("timeStart", new Date());

        List<Event> events = query.list();

        if(events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.close();
        return events;
    }

    @Override
    public List<Event> getAttendedEvents(int userId, int start) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E JOIN E.users U WHERE (:user) = U AND E.timeStart < (:timeStart) ORDER BY " +
                "E.timeStart DESC, E.id";

        Query query = session.createQuery(hql).setParameter("user", user).setParameter("timeStart", new Date());
        query.setFirstResult(start);
        query.setMaxResults(Constants.NUMBER_OF_EVENTS_RETURNED);

        List<Event> events = query.list();

        if(events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.close();
        return events;
    }

    @Override
    public List<Event> getAdminEvents(int userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User admin = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E WHERE E.admin = (:admin) AND timeStart > (:now)";

        Query query = session.createQuery(hql).setParameter("admin", admin).setDate("now", new Date());

        List<Event>
                events = query.list();

        if(events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.close();
        return events;
    }

    @Override
    public List<Event> getAdminEventsPast(int userId, int offset) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User admin = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E WHERE E.admin = (:admin) AND timeStart < (:now) ORDER BY E.timeStart DESC, E.id";

        Query query = session.createQuery(hql).setParameter("admin", admin).setDate("now", new Date()).setFirstResult(offset).setMaxResults
                (Constants.NUMBER_OF_EVENTS_RETURNED);

        List<Event>
                events = query.list();

        if(events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.close();
        return events;
    }

    /*
     * TODO: does not really recommend, only returns all coming events
     */
    @Override
    public List<Event> getRecommendedEvents(int userId, int offset) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT E FROM Event E WHERE E.timeStart > (:date) ORDER BY E.timeStart ASC, E.id ASC";

        Query query = session.createQuery(hql).setDate("date", new Date()).setFirstResult(offset).setMaxResults
                (Constants.NUMBER_OF_EVENTS_RETURNED);

        List<Event> events = query.list();

        if(events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.close();
        return events;    }
}
