package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.Event;
import com.andreasogeirik.model.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by eirikstadheim on 01/02/16.
 */
public class EventDaoImpl implements EventDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public int createEvent(String name, String location, String description, Date timeStart, Date timeEnd,
                           String imageURI, String adminUsername) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(User.class);
        User admin = (User)criteria.add(Restrictions.eq("email", adminUsername))
                .uniqueResult();


        Event event = new Event(name, location, description, new Date(), timeStart, timeEnd, imageURI, admin);

        session.save(event);

        session.getTransaction().commit();
        session.close();

        return 1;
    }
}
