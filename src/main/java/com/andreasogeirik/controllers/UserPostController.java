package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.CommentDto;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.Status;
import com.andreasogeirik.tools.Codes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("user/post")
public class UserPostController {

    @Autowired
    private UserPostDao postDao;

    @RequestMapping(value = "/{postId}/comment", method = RequestMethod.PUT)
    public ResponseEntity<Status> comment(@RequestBody CommentDto comment,
                                          @PathVariable(value="postId") int postId) throws IOException {

        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();


        int status = postDao.comment(comment.toUserPostComment(), postId, 14213);

        if(status == Codes.INVALID_COMMENT_MESSAGE) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid message"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.POST_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-2, "Post not found"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-3, "User not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{postId}/like", method = RequestMethod.PUT)
    public ResponseEntity<Status> comment(@PathVariable(value = "postId") int postId) throws IOException {

        int status = postDao.likePost(postId, 1123213);

        if(status == Codes.POST_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-1, "Post not found"), HttpStatus.BAD_REQUEST);
        }
        if(status == Codes.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-2, "User not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public void constraintViolation() {}

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Input length violation")  // 400
    @ExceptionHandler(org.hibernate.exception.DataException.class)
    public void inputLengthViolation() {}}