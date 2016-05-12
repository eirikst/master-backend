package com.andreasogeirik.beans;

import com.andreasogeirik.security.AuthenticationSuccessHandlerImpl;
import com.andreasogeirik.service.dao.*;
import com.andreasogeirik.service.dao.interfaces.*;
import com.andreasogeirik.service.gcm.GcmService;
import com.andreasogeirik.service.image.ImageServiceImpl;
import com.andreasogeirik.service.image.interfaces.ImageService;
import com.andreasogeirik.service.scheduled.NotificationScheduler;
import com.andreasogeirik.tools.InputManager;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Created by eirikstadheim on 29/01/16.
 */
@Configuration
public class BeanConfig {
    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }

    @Bean
    public PostDao postDao() {
        return new PostDaoImpl();
    }

    @Bean
    public EventDao eventDao() {
        return new EventDaoImpl();
    }

    @Bean
    public LogDao logDao() {
        return new LogDaoImpl();
    }

    @Bean
    public MiscDao miscDao() {return new MiscDaoImpl();}

    @Bean
    public ImageService imageService() {
        return new ImageServiceImpl();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration().configure()
                .buildSessionFactory();
    }

    //Create a transaction manager. Du tror du ikke trenger denne, men det gjør du bror
    @Bean
    public HibernateTransactionManager txManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public InputManager inputManager() {
        return new InputManager();
    }

    //BCrypt hash
    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler failureHandler(){
        return new SimpleUrlAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandlerImpl successHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

    @Bean
    public GcmService gcmService() {
        return new GcmService();
    }
}
