package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Event;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.tools.InputManager;
import com.andreasogeirik.tools.InvalidInputException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public class EventDaoImpl implements EventDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    InputManager inputManager;

    @Override
    public void createEvent(Event event, int adminId) {
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
        if(event.getTimeEnd().before(new Date())) {
            throw new InvalidInputException("Invalid end time");
        }
        if(event.getTimeEnd().before(event.getTimeStart())) {
            throw new InvalidInputException("End time cannot be before start time");
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User admin = session.get(User.class, adminId);


        event.setTimeCreated(new Date());
        event.setAdmin(admin);
        session.save(event);

        session.getTransaction().commit();
        session.close();
    }
}
