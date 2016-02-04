package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.EventDto;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.tools.Codes;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventDao eventDao;

    @PreAuthorize(value="hasAuthority('ADMIN')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Status> createEvent(@RequestBody EventDto event) throws IOException {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int adminId = user.getUserId();

        int status = eventDao.createEvent(event.toEvent(), adminId);

        if(status == Codes.INVALID_EVENT_NAME) {
            return new ResponseEntity<Status>(new Status(-2, "Invalid name"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_LOCATION) {
            return new ResponseEntity<Status>(new Status(-3, "Invalid location"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_DESCRIPTION) {
            return new ResponseEntity<Status>(new Status(-4, "Invalid description"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_TIME_START) {
            return new ResponseEntity<Status>(new Status(-5, "Invalid timeStart"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_TIME_END) {
            return new ResponseEntity<Status>(new Status(-6, "Invalid timeEnd"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.INVALID_IMAGE_URI) {
            return new ResponseEntity<Status>(new Status(-7, "Invalid image URI"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-8, "User does not exist"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public void constraintViolation() {}

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Input length violation")  // 400
    @ExceptionHandler(org.hibernate.exception.DataException.class)
    public void inputLengthViolation() {}}