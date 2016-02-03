package com.andreasogeirik.controllers;

import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.Status;
import com.andreasogeirik.tools.Codes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                             @RequestParam(value="location") String location) throws IOException {

        int status = userDao.createUser(email, password, firstname, lastname, location);

        if(status == Codes.INVALID_PASSWORD) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid password"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_EMAIL) {
            return new ResponseEntity<Status>(new Status(-2, "Invalid email"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_FIRSTNAME) {
            return new ResponseEntity<Status>(new Status(-3, "Invalid username"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_LASTNAME) {
            return new ResponseEntity<Status>(new Status(-4, "Invalid lastname"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_LOCATION) {
            return new ResponseEntity<Status>(new Status(-5, "Invalid location"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.USERNAME_EXISTS) {
            return new ResponseEntity<Status>(new Status(-6, "Username already exists in the system"), HttpStatus.CONFLICT);
        }
        if(status == Codes.EMAIL_EXISTS) {
            return new ResponseEntity<Status>(new Status(-7, "Email already exists in the system"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public void constraintViolation() {}
}