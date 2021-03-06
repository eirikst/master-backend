package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Event;
import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.LogDao;
import com.andreasogeirik.service.gcm.GcmService;
import com.andreasogeirik.tools.*;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public class EventDaoImpl implements EventDao {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    InputManager inputManager;

    @Autowired
    private GcmService gcmService;

    @Autowired
    private LogDao logDao;


    @Override
    @Transactional
    public Event createEvent(Event event, int userId) {
        if (!inputManager.isValidEventName(event.getName())) {
            throw new InvalidInputException("Invalid name format");
        }
        if (!inputManager.isValidEventDescription(event.getDescription())) {
            throw new InvalidInputException("Invalid description format");
        }
        if (!inputManager.isValidLocation(event.getLocation())) {
            throw new InvalidInputException("Invalid location format");
        }
        if (!inputManager.isValidDifficulty(event.getDifficulty())){
            throw new InvalidInputException("Invalid difficulty format");
        }
        if (!inputManager.isValidActivityType(event.getActivityType())){
            throw new InvalidInputException("Invalid activity type format");
        }
        if (event.getTimeStart().before(new Date())) {
            throw new InvalidInputException("Invalid start time");
        }
        if (event.getTimeEnd() != null) {
            if (event.getTimeEnd().before(new Date())) {
                throw new InvalidInputException("Invalid end time");
            }
            if (event.getTimeEnd().before(event.getTimeStart())) {
                throw new InvalidInputException("End time cannot be before start time");
            }
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        User admin = session.get(User.class, userId);

        if(admin == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("Cannot find user");
        }

        event.setTimeCreated(new Date());
        event.setAdmin(admin);

        Set<User> users = new HashSet<>();
        users.add(admin);

        event.setUsers(users);
        event.setEnabled(true);

        session.save(event);

        session.getTransaction().commit();
        session.close();

        logDao.eventCreated(event.getId());
        gcmService.notifyNewEvent(admin.getId(), admin.getFirstname(), admin.getLastname(), event.getId());

        return event;
    }


    @Override
    @Transactional
    public Event updateEvent(int userId, int eventId, Event event) {

        if (!inputManager.isValidEventName(event.getName())) {
            throw new InvalidInputException("Invalid name format");
        }
        if (!inputManager.isValidEventDescription(event.getDescription())) {
            throw new InvalidInputException("Invalid description format");
        }
        if (!inputManager.isValidLocation(event.getLocation())) {
            throw new InvalidInputException("Invalid location format");
        }
        if (event.getTimeStart().before(new Date())) {
            throw new InvalidInputException("Invalid start time");
        }
        if (!inputManager.isValidDifficulty(event.getDifficulty())){
            throw new InvalidInputException("Invalid difficulty format");
        }
        if (!inputManager.isValidActivityType(event.getActivityType())){
            throw new InvalidInputException("Invalid activity type format");
        }
        if (event.getTimeEnd() != null) {
            if (event.getTimeEnd().before(new Date())) {
                throw new InvalidInputException("Invalid end time");
            }
            if (event.getTimeEnd().before(event.getTimeStart())) {
                throw new InvalidInputException("End time cannot be before start time");
            }
        }

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Event contextEvent = session.get(Event.class, eventId);

        // Check if user is the admin of the event
        if (contextEvent.getAdmin().getId() != userId) {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not allowed to update this event");
        }
        Hibernate.initialize(contextEvent.getUsers());
        contextEvent.updateAttributes(event);

        session.update(contextEvent);

        session.getTransaction().commit();
        session.close();

        logDao.eventModified(eventId);

        return contextEvent;
    }

    @Override
    @Transactional
    public void deleteEvent(int userId, int eventId) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Event event = session.get(Event.class, eventId);
        if (event.getAdmin().getId() != userId || event.getTimeStart().before(new Date())) {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not allowed to delete this event");
        }
        Hibernate.initialize(event.getUsers());

        event.setEnabled(false);

        session.getTransaction().commit();
        session.close();

        //log
        logDao.eventDeleted(event, userId);
    }

    @Override
    @Transactional
    public Event getEvent(int eventId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Event event = session.get(Event.class, eventId);

        if (event == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("The requested event does not exist");
        }

        Hibernate.initialize(event.getUsers());

        session.getTransaction().commit();
        session.close();

        return event;
    }

    @Override
    @Transactional
    public Event attendEvent(int eventId, int userId) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, userId);
        Event event = session.get(Event.class, eventId);

        if (event == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("The requested event does not exist");
        }

        if (event.getTimeStart().before(new Date())) {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not allowed to delete this event");
        }

        Hibernate.initialize(event.getUsers());
        Set<User> users = event.getUsers();

        users.add(user);
        session.save(event);

        session.getTransaction().commit();
        session.close();

        //log
        logDao.eventAttended(eventId, userId);

        return event;
    }

    @Override
    @Transactional
    public Event unAttendEvent(int eventId, int userId) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        User user = session.get(User.class, userId);
        Event event = session.get(Event.class, eventId);

        if (event == null) {
            session.getTransaction().commit();
            session.close();
            throw new EntityNotFoundException("The requested event does not exist");
        }
        if (event.getTimeStart().before(new Date())) {
            session.getTransaction().commit();
            session.close();
            throw new EntityConflictException("The user is not allowed to delete this event");
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
    @Transactional
    public List<Event> getAttendingEvents(int userId) {
        Session session = sessionFactory.openSession();


        session.beginTransaction();

        User user = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E JOIN E.users U WHERE (:user) = U AND E.timeStart > (:timeStart)";

        Query query = session.createQuery(hql).setParameter("user", user).setParameter("timeStart", new Date());

        List<Event> events = query.list();

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.getTransaction().commit();
        session.close();

        return events;
    }

    @Override
    @Transactional
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

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.getTransaction().commit();
        session.close();

        return events;
    }

    @Override
    @Transactional
    public List<Event> getAdminEvents(int userId) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        User admin = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E WHERE E.admin = (:admin) AND timeStart > (:now)";

        Query query = session.createQuery(hql).setParameter("admin", admin).setTimestamp("now", new Date());

        List<Event> events = query.list();

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.getTransaction().commit();
        session.close();

        return events;
    }

    @Override
    @Transactional
    public List<Event> getAdminEventsPast(int userId, int offset) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        User admin = session.get(User.class, userId);
        String hql = "SELECT E FROM Event E WHERE E.admin = (:admin) AND timeStart < (:now) ORDER BY E.timeStart DESC, E.id";

        Query query = session.createQuery(hql).setParameter("admin", admin).setTimestamp("now", new Date()).setFirstResult(offset).setMaxResults
                (Constants.NUMBER_OF_EVENTS_RETURNED);

        List<Event> events = query.list();

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {

                Hibernate.initialize(events.get(i).getAdmin());
                Hibernate.initialize(events.get(i).getUsers());
            }
        }

        session.getTransaction().commit();
        session.close();

        return events;
    }

    /*
     * TODO: does not really recommend, only returns all coming events
     */
    @Override
    @Transactional
    public List<Event> getRecommendedEvents(int userId, int offset) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT E FROM Event E WHERE E.timeStart > (:date) AND E.enabled = TRUE ORDER BY E.timeStart ASC, E.id ASC";

        Query query = session.createQuery(hql).setTimestamp("date", new Date()).setFirstResult(offset).setMaxResults
                (Constants.NUMBER_OF_EVENTS_RETURNED);

        List<Event> events = query.list();
        List<Event> eventsWithoutMine = new ArrayList<>();

        if (events != null) {
            for (Event event: events) {
                boolean removed = false;
                for(User user: event.getUsers()) {
                    if(user.getId() == userId) {
                        removed = true;
                        break;
                    }
                }
                if(!removed) {
                    eventsWithoutMine.add(event);
                    Hibernate.initialize(event.getAdmin());
                    Hibernate.initialize(event.getUsers());
                }
            }
        }

        session.getTransaction().commit();
        session.close();

        return eventsWithoutMine;
    }
}
