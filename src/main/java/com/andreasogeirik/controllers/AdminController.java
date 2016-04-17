package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserDto;
import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.*;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDao userDao;

    /**
     * Creates an admin user
     * @param user JSON representation of the new user
     * @return JSON representation of the new user including ID
     */
    @PreAuthorize(value="hasAuthority('ADMIN')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<UserDtoOut> createUser(@RequestBody UserDto user) {
        UserDtoOut userOut = new UserDtoOut(userDao.createAdminUser(user.toUser()));

        return new ResponseEntity<UserDtoOut>(userOut, HttpStatus.CREATED);
    }


    /*
     * Exception handling
     */
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<Status> constraintViolation(org.hibernate.exception.ConstraintViolationException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());

        return new ResponseEntity<Status>(new Status(-2, "Some persistence constraint occurred"),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity inputLengthViolation(DataException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity("Input length violation", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Status> violation(InvalidInputException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(0, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Status> formatViolation(MethodArgumentTypeMismatchException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-1, "Input of wrong type(eg. string when expecting integer)"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Status> formatViolation(IllegalArgumentException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-3, "Entity not found."), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Status> formatViolation(EntityNotFoundException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-5, "Entity not found."), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Status> formatViolation(EntityConflictException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-5, "Conflicting entities."), HttpStatus.CONFLICT);
    }
}