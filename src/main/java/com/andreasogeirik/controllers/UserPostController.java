package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.CommentDto;
import com.andreasogeirik.model.dto.outgoing.CommentDtoOut;
import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.model.dto.outgoing.UserPostDtoOut;
import com.andreasogeirik.model.entities.UserPost;
import com.andreasogeirik.model.entities.UserPostComment;
import com.andreasogeirik.model.entities.UserPostLike;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.Constants;
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
import java.util.*;

@RestController
@RequestMapping("user/post")
public class UserPostController {

    @Autowired
    private UserPostDao postDao;



    /*
     * Creates a comment to the specified post
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/comment", method = RequestMethod.PUT)
    public ResponseEntity<Status> comment(@RequestBody CommentDto comment,
                                          @PathVariable(value="postId") int postId) throws IOException {

        postDao.comment(comment.toUserPostComment(), postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    /*
     * Creates a like on the specified post from the logged in user
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/like", method = RequestMethod.PUT)
    public ResponseEntity<Status> like(@PathVariable(value = "postId") int postId) throws IOException {

        postDao.like(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }


    /*
     * Exception handling
     */
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