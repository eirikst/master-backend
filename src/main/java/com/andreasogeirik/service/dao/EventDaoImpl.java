package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.Event;
import com.andreasogeirik.model.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.tools.Codes;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.Criteria;
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

    @Override
    public int createEvent(Event event, int adminId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User admin = session.get(User.class, adminId);


        int status = 0;
        if(admin != null) {
            event.setTimeCreated(new Date());
            event.setAdmin(admin);
            session.save(event);
            status = 1;
        }
        else {
            status = Codes.USER_NOT_FOUND;
        }

        session.getTransaction().commit();
        session.close();

        return status;
    }
}
