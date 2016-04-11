package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.EventDto;
import com.andreasogeirik.model.dto.incoming.PostDto;
import com.andreasogeirik.model.dto.outgoing.CommentDtoOut;
import com.andreasogeirik.model.dto.outgoing.EventDtoOut;
import com.andreasogeirik.model.dto.outgoing.PostDtoOut;
import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.model.entities.Comment;
import com.andreasogeirik.model.entities.Post;
import com.andreasogeirik.model.entities.PostLike;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private PostDao postDao;

    /**
     * Creates event
     * @param event JSON representation of the event to create
     * @return JSON representation of the event with the ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<EventDtoOut> createEvent(@RequestBody EventDto event) {
        EventDtoOut eventOut = new EventDtoOut(eventDao.createEvent(event.toEvent(),
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));
        return new ResponseEntity(eventOut, HttpStatus.CREATED);
    }

    /**
     * Updates an event
     * @param event JSON representation of the event to update
     * @return JSON representation of the event with the ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}", method = RequestMethod.POST)
    public ResponseEntity<EventDtoOut> updateEvent(@PathVariable(value = "eventId") int eventId, @RequestBody EventDto event) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        EventDtoOut eventOut = new EventDtoOut(eventDao.updateEvent(userId, eventId, event.toEvent()));
        return new ResponseEntity(eventOut, HttpStatus.OK);
    }

    /**
     * Deletes an event
     * @param eventId, the ID of the event to be deleted
     * @return HttpStatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}", method = RequestMethod.DELETE)
    public ResponseEntity<HttpStatus> deleteEvent(@PathVariable(value = "eventId") int eventId) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        eventDao.deleteEvent(userId, eventId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Retrieves an event by id
     * @param eventId
     * @return JSON representation of the event with the ID
     */
//    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public ResponseEntity<EventDtoOut> getEvent(@PathVariable(value="eventId") int eventId) {

        return new ResponseEntity(new EventDtoOut(eventDao.getEvent(eventId)), HttpStatus.OK);
    }

    /**
     * Adds a user to an event
     * @param eventId
     * @return JSON representation of the attended event
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}/attend", method = RequestMethod.POST)
    public ResponseEntity<EventDtoOut> attendEvent(@PathVariable(value = "eventId") int eventId) {

        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        EventDtoOut eventDtoOut = new EventDtoOut(eventDao.attendEvent(eventId, userId));

        return new ResponseEntity(eventDtoOut, HttpStatus.OK);
    }

    /**
     * Removes a user from an event
     * @param eventId
     * @return JSON representation of the attended event
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}/unattend", method = RequestMethod.POST)
    public ResponseEntity<EventDtoOut> unAttendEvent(@PathVariable(value = "eventId") int eventId) {

        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        EventDtoOut eventDtoOut = new EventDtoOut(eventDao.unAttendEvent(eventId, userId));

        return new ResponseEntity(eventDtoOut, HttpStatus.OK);
    }

    /**
     * Creates a Post on the specified event for the logged in user
     * @param post Post represented as JSON
     * @param eventId id of the event to post to
     * @return the Post represented as JSON with ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{eventId}/posts",method = RequestMethod.PUT)
    public ResponseEntity<PostDtoOut> post(@RequestBody PostDto post, @PathVariable(value = "eventId") int eventId) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        Post postEntityOut = postDao.eventPost(post.getMessage(), post.getImageUri(), userId, eventId);
        PostDtoOut postOut = PostDtoOut.newInstanceWithoutEvent(postEntityOut);

        return new ResponseEntity<PostDtoOut>(postOut, HttpStatus.CREATED);
    }


    /**
     * Gets the a given number of Posts(10 right now) for the logged in user, with an offset specified
     * @param start offset
     * @return list of 10(or less, if no more present) user userPost objects as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/{eventId}/posts")
    public ResponseEntity<List<PostDtoOut>> getPosts(@PathVariable(value = "eventId") int eventId,
                                                     @RequestParam(value = "start") int start) {
        if(start < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        List<Post> posts = postDao.findPostsEvent(eventId, start, Constants.NUMBER_OF_POSTS_RETURNED);

        List<PostDtoOut> postsOut = new ArrayList<PostDtoOut>();

        for(int i = 0; i < posts.size(); i++) {
            PostDtoOut postOut = new PostDtoOut(posts.get(i));

            //iterate comments
            Set<CommentDtoOut> comments = new HashSet<>();
            Iterator<Comment> it = posts.get(i).getComments().iterator();
            while(it.hasNext()) {
                Comment commentEntity = it.next();
                CommentDtoOut comment = new CommentDtoOut(commentEntity);
                comment.setUser(new UserDtoOut(commentEntity.getUser()));
                comments.add(comment);
            }
            postOut.setComments(comments);

            //iterate likes
            Set<UserDtoOut> likers = new HashSet<>();
            Iterator<PostLike> likeIt = posts.get(i).getLikes().iterator();
            while(likeIt.hasNext()) {
                likers.add(new UserDtoOut(likeIt.next().getUser()));
            }


            postOut.setLikers(likers);
            postsOut.add(postOut);
        }

        return new ResponseEntity<>(postsOut, HttpStatus.OK);
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

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Status> entityConflict(InvalidInputException e) {
        return new ResponseEntity<Status>(new Status(0, e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Status> formatViolation(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<Status>(new Status(-1, "Input of wrong type(eg. string when expecting integer)"),
                HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Entity with the provided ID was not found")  // 409
    @ExceptionHandler(EntityNotFoundException.class)
    public void entityNotFound() {}
}