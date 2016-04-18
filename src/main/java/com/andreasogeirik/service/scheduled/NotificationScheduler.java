package com.andreasogeirik.service.scheduled;

import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.gcm.GcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by eirikstadheim on 18/04/16.
 */
@Component
public class NotificationScheduler {
    @Autowired
    private GcmService gcm;

    @Autowired
    private UserDao userDao;

    @Scheduled(fixedDelay = 10000)
    private void notifyInactivity () {
        Set<Integer> tokens = userDao.findGcmTokensForUsersWithNoUpcomingEvents();
        gcm.notifyInactiveWeek(tokens);
    }
}
