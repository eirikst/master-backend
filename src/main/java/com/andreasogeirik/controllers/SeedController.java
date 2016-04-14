package com.andreasogeirik.controllers;

import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.EmailExistsException;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

import static com.andreasogeirik.tools.Constants.BACKEND_URL;

@RestController
@RequestMapping("/seed")
public class SeedController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private EventDao eventDao;

    /**
     * Seed this shit
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> seedThisShit() {
        userDao.createAdminUser(new com.andreasogeirik.model.entities.User("admin", "pwd", true, "Eirik", "Stadheim",
                "Trondheim", new Date()));
        userDao.updateUser("Eirik", "Stadheim", "Trondheim", BACKEND_URL + "image/0-eirik", BACKEND_URL + "image/0S-eirik", 1);
        userDao.createUser(new com.andreasogeirik.model.entities.User("andreas", "pwd", true, "Andreas", "Næss",
                "Trondheim", new Date()));
        userDao.updateUser("Andreas", "Næss", "Trondheim", BACKEND_URL + "image/0-andreas", BACKEND_URL + "image/0S-andreas", 2);
        userDao.createUser(new com.andreasogeirik.model.entities.User("mikael", "pwd", true, "Mikael", "Reiersølmoen",
                "Trondheim", new Date()));
        userDao.updateUser("Mikael", "Reiersølmoen", "Trondheim", BACKEND_URL + "image/0-mikael", BACKEND_URL + "image/0S-mikael", 3);
        userDao.createUser(new com.andreasogeirik.model.entities.User("shari", "pwd", true, "Shahariar", "Kabir Bhuiyan",
                "Trondheim", new Date()));
        userDao.updateUser("Shahariar", "Kabir Bhuiyan", "Trondheim", BACKEND_URL + "image/0-shari", BACKEND_URL + "image/0S-shari", 4);
        userDao.createUser(new com.andreasogeirik.model.entities.User("hamadul", "pwd", true, "Rakib", "Hamadul",
                "Trondheim", new Date()));
        userDao.updateUser("Rakib", "Hamadul", "Trondheim", BACKEND_URL + "image/0-hamadul", BACKEND_URL + "image/0S-hamadul", 5);

        userDao.addFriendRequest(1, 2);
        userDao.acceptFriendRequest(1, 2);

        userDao.addFriendRequest(1, 3);
        userDao.acceptFriendRequest(2, 3);

        userDao.addFriendRequest(3, 2);
        userDao.acceptFriendRequest(3, 2);

        userDao.addFriendRequest(4, 2);
        userDao.acceptFriendRequest(4, 2);

        userDao.addFriendRequest(4, 1);

        userDao.addFriendRequest(5, 3);
        userDao.acceptFriendRequest(6, 3);

        userDao.addFriendRequest(5, 4);
        userDao.acceptFriendRequest(7, 4);

        userDao.addFriendRequest(1, 5);
        userDao.acceptFriendRequest(8, 5);

        userDao.addFriendRequest(3, 4);
        userDao.acceptFriendRequest(9, 4);


        Calendar twoDays = new GregorianCalendar();
        twoDays.add(Calendar.DAY_OF_MONTH, 2);

        Calendar fourDays = new GregorianCalendar();
        fourDays.add(Calendar.DAY_OF_MONTH, 4);
        fourDays.add(Calendar.HOUR, 4);

        Calendar aWeek = new GregorianCalendar();
        aWeek.add(Calendar.DAY_OF_MONTH, 2);
        aWeek.add(Calendar.HOUR, 7);


        eventDao.createEvent(new Event("Tur til Geitfjellet", "Trondheim", "Vi møtes i Ilaparken", twoDays.getTime(),
                null, BACKEND_URL + "image/01-geitfjellet", BACKEND_URL + "image/01S-geitfjellet", 3), 1);
        eventDao.createEvent(new Event("Tur til Våttakammen", "Trondheim", "Vi møtes i Ilaparken", twoDays.getTime(),
                null, BACKEND_URL + "image/01-vattakammen", BACKEND_URL + "image/01S-vattakammen", 2), 2);
        eventDao.createEvent(new Event("Skitur på Gautefall", "Gautefall", "Vi møtes ved skiheisen", fourDays.getTime(),
                null, BACKEND_URL + "image/01-gautefall", BACKEND_URL + "image/01S-gautefall", 3), 3);
        eventDao.createEvent(new Event("Skitur i Heidal", "Heidal", "Vi møtes i Ilaparken", fourDays.getTime(),
                null, BACKEND_URL + "image/01-hytta", BACKEND_URL + "image/01S-hytta", 3), 4);
        eventDao.createEvent(new Event("Tur til Glittertind", "Jotunheimen", "Vi møtes på Glitterheim",
                twoDays.getTime(), null, BACKEND_URL + "image/01-jotunheimen", BACKEND_URL + "image/01S-jotunheimen", 3), 5);
        eventDao.createEvent(new Event("Svømming i Russvatnet", "Jotunheimen", "Vi på møtes Bessheim", aWeek.getTime(),
                null, BACKEND_URL + "image/01-russvatnet", BACKEND_URL + "image/01S-russvatnet",  2), 1);
        eventDao.createEvent(new Event("Spasere til Nidarosdomen", "Trondheim", "Vi møtes i på Torget", aWeek.getTime(),
                null, BACKEND_URL + "image/01-nidarosdomen", BACKEND_URL + "image/01S-nidarosdomen",  1), 2);
        eventDao.createEvent(new Event("Sykle til Estenstadhytta", "Trondheim", "Vi møtes på Dragvoll", aWeek.getTime(),
                null, BACKEND_URL + "image/01-estenstadhytta", BACKEND_URL + "image/01S-estenstadhytta", 3), 3);

        eventDao.attendEvent(1, 1);
        eventDao.attendEvent(2, 1);
        eventDao.attendEvent(3, 1);
        eventDao.attendEvent(4, 1);
        eventDao.attendEvent(5, 1);
        eventDao.attendEvent(6, 1);

        eventDao.attendEvent(7, 2);
        eventDao.attendEvent(2, 2);
        eventDao.attendEvent(3, 2);
        eventDao.attendEvent(4, 2);
        eventDao.attendEvent(5, 2);
        eventDao.attendEvent(6, 2);

        eventDao.attendEvent(7, 3);
        eventDao.attendEvent(8, 3);
        eventDao.attendEvent(3, 3);
        eventDao.attendEvent(4, 3);
        eventDao.attendEvent(5, 3);
        eventDao.attendEvent(6, 3);

        eventDao.attendEvent(7, 4);
        eventDao.attendEvent(8, 4);
        eventDao.attendEvent(1, 4);
        eventDao.attendEvent(2, 4);
        eventDao.attendEvent(5, 4);
        eventDao.attendEvent(6, 4);

        eventDao.attendEvent(7, 5);
        eventDao.attendEvent(8, 5);
        eventDao.attendEvent(1, 5);
        eventDao.attendEvent(2, 5);
        eventDao.attendEvent(3, 5);
        eventDao.attendEvent(4, 5);


        return new ResponseEntity<String>("ok", HttpStatus.OK);
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

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Status> formatViolation(EmailExistsException e) {
        return new ResponseEntity<Status>(new Status(-1, "Email already exists in the system"), HttpStatus.CONFLICT);
    }

}