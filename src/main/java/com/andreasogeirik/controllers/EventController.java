package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.EventDto;
import com.andreasogeirik.model.dto.outgoing.EventDtoOut;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventDao eventDao;

    /**
     * Creates event
     * @param event JSON representation of the event to create
     * @return JSON representation of the event with the ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<EventDtoOut> createEvent(@RequestBody EventDto event) {
        EventDtoOut eventOut;
        if (event.getTimeEnd() == null){
            eventOut = new EventDtoOut(eventDao.createEvent(event.toEventNoTimeEnd(),
                    ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));
        }
        else{
            eventOut = new EventDtoOut(eventDao.createEvent(event.toEvent(),
                    ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));
        }

        return new ResponseEntity(eventOut, HttpStatus.CREATED);
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