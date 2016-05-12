package com.andreasogeirik.service.dao;

import com.andreasogeirik.model.entities.Misc;
import com.andreasogeirik.service.dao.interfaces.MiscDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by andrena on 12.05.2016.
 */
public class MiscDaoImpl implements MiscDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void createMisc(Misc misc) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(misc);
        session.getTransaction().commit();
        session.close();
    }
}
