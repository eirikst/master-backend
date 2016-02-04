package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.UserDto;
import com.andreasogeirik.model.dto.UserPostDto;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import com.andreasogeirik.tools.Codes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPostDao postDao;

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Status> createUser(@RequestBody UserDto user) throws IOException {

        int status = userDao.createUser(user.toUser());

        if(status == Codes.EMAIL_EXISTS) {
            return new ResponseEntity<Status>(new Status(-1, "Email already exists in the system"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/post",method = RequestMethod.PUT)
    public ResponseEntity<Status> post(@RequestBody UserPostDto post) throws IOException {

        postDao.newUserPost(post.toPost(),
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }



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