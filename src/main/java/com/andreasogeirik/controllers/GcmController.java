package com.andreasogeirik.controllers;

import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.gcm.GcmService;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.logging.Logger;

@RestController
@RequestMapping("/gcm")
public class GcmController {
    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private UserDao userDao;

    @Autowired
    private GcmService gcmService;

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addGCMToken(@RequestParam(value = "gcmToken") String gcmToken) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        userDao.registerGcmToken(userId, gcmToken);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity removeGCMToken(@RequestParam(value = "gcmToken") String gcmToken) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        userDao.removeGcmToken(userId, gcmToken);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity sendMeGCM(@RequestParam(value = "msg") String msg) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        gcmService.notifyTest(msg, userId);
        return new ResponseEntity(HttpStatus.OK);
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