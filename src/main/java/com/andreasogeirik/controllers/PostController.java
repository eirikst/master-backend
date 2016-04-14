package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.CommentDto;
import com.andreasogeirik.model.dto.outgoing.CommentDtoOut;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.EntityConflictException;
import com.andreasogeirik.tools.EntityNotFoundException;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestController
@RequestMapping("posts")
public class PostController {

    @Autowired
    private PostDao postDao;


    /**
     * Removes the post with specified id
     * @param postId
     * @return httpstatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}", method = RequestMethod.DELETE)
    public ResponseEntity remove(@PathVariable(value="postId") int postId) {
        postDao.removePost(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Adds a comment to the post
     * @param comment comment object as json
     * @param postId id of post to comment on
     * @return the comment with id
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/comments", method = RequestMethod.PUT)
    public ResponseEntity<CommentDtoOut> comment(@RequestBody CommentDto comment,
                                          @PathVariable(value="postId") int postId) {

        CommentDtoOut commentOut = new CommentDtoOut(postDao.comment(comment.toUserPostComment(), postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));

        return new ResponseEntity<CommentDtoOut>(commentOut, HttpStatus.CREATED);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/comments/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity removeComment(@PathVariable(value="commentId") int commentId) {
        postDao.removeComment(commentId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Adds a like to the specified post by the logged in user
     * @param postId id of post to like
     * @return the like with id
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/likes", method = RequestMethod.PUT)
    public ResponseEntity likePost(@PathVariable(value = "postId") int postId) {

        postDao.likePost(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.CREATED);
    }


    /**
     * Deletes the like
     * @param postId id of the comment to unlike
     * @return httpstatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{postId}/likes", method = RequestMethod.DELETE)
    public ResponseEntity unlikePost(@PathVariable(value = "postId") int postId) {

        postDao.removePostLike(postId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * Adds a like to the specified post by the logged in user
     * @param commentId id of post to like
     * @return the like with id
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/comments/{commentId}/likes", method = RequestMethod.PUT)
    public ResponseEntity likeComment(@PathVariable(value = "commentId") int commentId) {

        postDao.likeComment(commentId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * Deletes the like
     * @param commentId id of the comment to unlike
     * @return httpstatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/comments/{commentId}/likes", method = RequestMethod.DELETE)
    public ResponseEntity unlikeComment(@PathVariable(value = "commentId") int commentId) {

        postDao.removeCommentLike(commentId,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }


    /*
     * Exception handling
     */
    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<String> entityConflict(InvalidInputException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound(EntityNotFoundException e) {
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
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