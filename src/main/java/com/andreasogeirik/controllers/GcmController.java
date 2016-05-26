package com.andreasogeirik.controllers;

import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.gcm.GcmService;
import com.andreasogeirik.tools.EntityConflictException;
import com.andreasogeirik.tools.EntityNotFoundException;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.hibernate.exception.DataException;
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

    /**
     * Adds the specified gcm token to the logged in user, so that the user can retrieve notifications
     * @param gcmToken token to add
     * @return status ok
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addGCMToken(@RequestParam(value = "gcmToken") String gcmToken) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        userDao.registerGcmToken(userId, gcmToken);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Removes the specified gcm token from the logged in user, so that he no longer retrieves notifications on that
     * device
     * @param gcmToken token to remove
     * @return status ok
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity removeGCMToken(@RequestParam(value = "gcmToken") String gcmToken) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        userDao.removeGcmToken(userId, gcmToken);
        return new ResponseEntity(HttpStatus.OK);
    }

    //should not be documented
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity sendGCM(@RequestParam(value = "msg") String msg, @RequestParam(value = "userId") int userId) {
        gcmService.notifyTest(msg, userId);
        return new ResponseEntity(HttpStatus.OK);
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