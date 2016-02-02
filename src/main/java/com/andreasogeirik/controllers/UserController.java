package com.andreasogeirik.controllers;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.UserDao;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;


    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Status> createUser(@RequestParam(value="email") String email,
                                             @RequestParam(value="password") String password,
                                             @RequestParam(value="firstname") String firstname,
                                             @RequestParam(value="lastname") String lastname,
                                             @RequestParam(value="location") String location,
                                             HttpServletResponse response) throws IOException {

        int status = userDao.newUser(email, password, firstname, lastname, location);

        if(status == UserDao.INVALID_PASSWORD) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid password"), HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_EMAIL) {
            return new ResponseEntity<Status>(new Status(-2, "Invalid email"), HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_FIRSTNAME) {
            return new ResponseEntity<Status>(new Status(-3, "Invalid username"), HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_LASTNAME) {
            return new ResponseEntity<Status>(new Status(-4, "Invalid lastname"), HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_LOCATION) {
            return new ResponseEntity<Status>(new Status(-5, "Invalid location"), HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.USERNAME_EXISTS) {
            return new ResponseEntity<Status>(new Status(-6, "Username already exists in the system"), HttpStatus.CONFLICT);
        }
        if(status == UserDao.EMAIL_EXISTS) {
            return new ResponseEntity<Status>(new Status(-7, "Email already exists in the system"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
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