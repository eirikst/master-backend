package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.Event;
import com.andreasogeirik.model.User;
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
    private UserDao userDao;

    @Override
    public int newEvent(String name, String location, String description, Date timeStart, Date timeEnd, String imageURI,
                        int adminId) {
        User admin = userDao.findById(adminId);//denne inni transaction??

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Event event = new Event(name, location, description, new Date(), timeStart, timeEnd, imageURI, admin);

        session.save(event);

        session.getTransaction().commit();
        session.close();

        return 1;
    }
}
