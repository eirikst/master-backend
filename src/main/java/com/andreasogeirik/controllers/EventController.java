package com.andreasogeirik.controllers;

import com.andreasogeirik.model.Greeting;
import com.andreasogeirik.service.dao.EventDao;
import com.andreasogeirik.service.dao.UserDao;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventDao eventDao;

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Status> createEvent(@RequestParam(value="name") String name,
                                              @RequestParam(value="location") String location,
                                              @RequestParam(value="description") String description,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value="timeStart") Date timeStart,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value="timeEnd") Date timeEnd,
                                              @RequestParam(value="imageURI") String imageURI,
                                              @RequestParam(value="adminId") int adminId,
                                              HttpServletResponse response) throws IOException {

        int status = eventDao.newEvent(name, location, description, timeStart, timeEnd, imageURI, adminId);

        if(status == EventDao.INVALID_NAME) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid name"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.INVALID_LOCATION) {
            return new ResponseEntity<Status>(new Status(-2, "Invalid location"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.INVALID_DESCRIPTION) {
            return new ResponseEntity<Status>(new Status(-3, "Invalid description"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.INVALID_TIME_START) {
            return new ResponseEntity<Status>(new Status(-4, "Invalid timeStart"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.INVALID_TIME_END) {
            return new ResponseEntity<Status>(new Status(-5, "Invalid timeEnd"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.INVALID_IMAGE_URI) {
            return new ResponseEntity<Status>(new Status(-6, "Invalid image URI"), HttpStatus.BAD_REQUEST);
        }
        if(status == EventDao.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-7, "User does not exist"), HttpStatus.BAD_REQUEST);
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