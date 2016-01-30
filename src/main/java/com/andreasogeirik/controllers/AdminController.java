package com.andreasogeirik.controllers;

import com.andreasogeirik.model.Greeting;
import com.andreasogeirik.service.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDao userDao;

    @PreAuthorize(value="hasAuthority('ADMIN')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> createAdmin(@RequestParam(value="username") String username,
                                             @RequestParam(value="password") String password,
                                             @RequestParam(value="email") String email,
                                             HttpServletResponse response) throws IOException {

        int status = userDao.newAdminUser(username, password, email);

        if(status == UserDao.INVALID_USERNAME) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_PASSWORD) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_EMAIL) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.USERNAME_EXISTS) {
            return new ResponseEntity<String>(HttpStatus.CONFLICT);
        }
        if(status == UserDao.EMAIL_EXISTS) {
            return new ResponseEntity<String>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>(HttpStatus.CREATED);
    }





    
    /*
     *  Methods for testing purposes
     */
    @RequestMapping(method = RequestMethod.GET)
    public Greeting testGet(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(1L, "Hei, " + name);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Greeting testPost(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(1L, "Hei, " + name);
    }
}