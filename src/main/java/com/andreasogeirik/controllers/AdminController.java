package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserDto;
import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import com.andreasogeirik.tools.Codes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

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
    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public void constraintViolation() {}

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Input length violation")  // 400
    @ExceptionHandler(org.hibernate.exception.DataException.class)
    public void inputLengthViolation() {}


    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Status> violation(InvalidInputException e) {
        return new ResponseEntity<Status>(new Status(0, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Status> formatViolation(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<Status>(new Status(-1, "Input of wrong type(eg. string when expecting integer)"),
                HttpStatus.BAD_REQUEST);
    }
}