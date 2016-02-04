package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.CommentDto;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@RestController
@RequestMapping("user/post")
public class UserPostController {

    @Autowired
    private UserPostDao postDao;

    @RequestMapping(value = "/{postId}/comment", method = RequestMethod.PUT)
    public ResponseEntity<Status> comment(@RequestBody CommentDto comment,
                                          @PathVariable(value="postId") int postId) throws IOException {

        postDao.comment(comment.toUserPostComment(), postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{postId}/like", method = RequestMethod.PUT)
    public ResponseEntity<Status> like(@PathVariable(value = "postId") int postId) throws IOException {

        postDao.likePost(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }


    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public void constraintViolation() {
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Input length violation")  // 400
    @ExceptionHandler(org.hibernate.exception.DataException.class)
    public void inputLengthViolation() {
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Status> inputViolation(InvalidInputException e) {
        return new ResponseEntity<Status>(new Status(0, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Status> formatViolation(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<Status>(new Status(-1, "Input of wrong type(eg. string when expecting integer)"),
                HttpStatus.BAD_REQUEST);
    }
}