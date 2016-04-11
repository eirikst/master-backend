package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.PostCommentDto;
import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.model.dto.outgoing.CommentDtoOut;
import com.andreasogeirik.model.dto.outgoing.PostDtoOut;
import com.andreasogeirik.model.entities.Comment;
import com.andreasogeirik.model.entities.Post;
import com.andreasogeirik.model.entities.PostLike;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.Constants;
import com.andreasogeirik.tools.EntityConflictException;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@RestController
@RequestMapping("posts")
public class PostController {

    @Autowired
    private PostDao postDao;


    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}", method = RequestMethod.DELETE)
    public ResponseEntity remove(@PathVariable(value="postId") int postId) {
        postDao.removePost(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }




    /*
     *  TODO: Så plutselig at denne må oppdateres til å sende tilbake den nye commenten med ID. Men det gidder jeg ikke gjøre nå...
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/comments", method = RequestMethod.PUT)
    public ResponseEntity<CommentDtoOut> comment(@RequestBody PostCommentDto comment,
                                          @PathVariable(value="postId") int postId) {

        CommentDtoOut commentOut = new CommentDtoOut(postDao.comment(comment.toUserPostComment(), postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));

        return new ResponseEntity<CommentDtoOut>(commentOut, HttpStatus.CREATED);
    }

    /*
     * TODO: Må også oppdateres og sende tilbake liken med id
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/likes", method = RequestMethod.PUT)
    public ResponseEntity like(@PathVariable(value = "postId") int postId) {

        postDao.likePost(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.CREATED);
    }


    /*
     * Exception handling
     */
    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<String> entityConflict(InvalidInputException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
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