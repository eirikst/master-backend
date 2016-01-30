package com.andreasogeirik.controllers;

import com.andreasogeirik.model.*;
import com.andreasogeirik.service.dao.UserDao;
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
    public ResponseEntity<String> createUser(@RequestParam(value="username") String username,
                                             @RequestParam(value="password") String password,
                                             @RequestParam(value="email") String email,
                                             HttpServletResponse response) throws IOException {

        int status = userDao.newUser(username, password, email);

        if(status == UserDao.INVALID_USERNAME) {
            System.out.println("invalid username");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_PASSWORD) {
            System.out.println("invalid pwd");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(status == UserDao.INVALID_EMAIL) {
            System.out.println("invalid email");
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