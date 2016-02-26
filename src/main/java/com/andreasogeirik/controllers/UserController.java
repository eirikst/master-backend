package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserDto;
import com.andreasogeirik.model.dto.outgoing.*;
import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPostDao postDao;

    @Autowired
    private EventDao eventDao;

    /**
     * Creates a user with USER authorization
     * @param user the user object(JSON)
     * @return the user object as JSON with ID
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<UserDtoOut> createUser(@RequestBody UserDto user) {
        UserDtoOut userOut = new UserDtoOut(userDao.createUser(user.toUser()));

        return new ResponseEntity<UserDtoOut>(userOut, HttpStatus.CREATED);
    }

    /**
     * Gets the a given number of posts(10 right now) of the user specified, with an offset specified
     * @param userId id of user as integer
     * @param start offset
     * @return list of 10(or less, if no more present) user post objects as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/posts", method = RequestMethod.GET)
    public ResponseEntity<List<UserPostDtoOut>> getPosts(@PathVariable(value = "userId") int userId,
                                                         @RequestParam(value = "start") int start) {
        if(start < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        List<UserPost> posts = postDao.findPosts(userId, start, Constants.NUMBER_OF_POSTS_RETURNED);

        List<UserPostDtoOut> postsOut = new ArrayList<UserPostDtoOut>();

        for(int i = 0; i < posts.size(); i++) {
            UserPostDtoOut postOut = new UserPostDtoOut(posts.get(i));

            //iterate comments
            Set<CommentDtoOut> comments = new HashSet<>();
            Iterator<UserPostComment> it = posts.get(i).getComments().iterator();
            while(it.hasNext()) {
                comments.add(new CommentDtoOut(it.next()));
            }
            postOut.setComments(comments);

            //iterate likes
            Set<UserDtoOut> likers = new HashSet<>();
            Iterator<UserPostLike> likeIt = posts.get(i).getLikes().iterator();
            while(likeIt.hasNext()) {
                likers.add(new UserDtoOut(likeIt.next().getUser()));
            }


            postOut.setLikers(likers);
            postsOut.add(postOut);
        }

        return new ResponseEntity<>(postsOut, HttpStatus.OK);
    }


    /**
     * Gets friendships of the specified user. Ignores friend requests, as this is personal to the users.
     * @param userId user-id of user to get the friend list of
     * @return JSONArray with Friendship objects.
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/friendships", method = RequestMethod.GET)
    public ResponseEntity<Set<FriendshipDtoOut>> getFriends(@PathVariable(value = "userId") int userId) {
        List<Friendship> friendships = userDao.findFriends(userId);

        Set<FriendshipDtoOut> friendshipsOut = new HashSet<FriendshipDtoOut>();

        Iterator<com.andreasogeirik.model.entities.Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            friendshipsOut.add(new FriendshipDtoOut((Friendship)it.next(), userId));
        }

        return new ResponseEntity<Set<FriendshipDtoOut>>(friendshipsOut, HttpStatus.OK);
    }


    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/friendships", method = RequestMethod.PUT)
    public ResponseEntity<FriendshipDtoOut> friendRequest(@PathVariable(value = "userId") int userId) {

        int loggedInUserId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        Friendship friendship = userDao.addFriendRequest(loggedInUserId, userId);

        return new ResponseEntity<FriendshipDtoOut>(new FriendshipDtoOut(friendship, loggedInUserId), HttpStatus.CREATED);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/friendships/{id}/accept", method = RequestMethod.POST)
    public ResponseEntity acceptFriend(@PathVariable(value = "id") int friendshipId) {

        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        userDao.acceptFriendRequest(friendshipId, userId);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/friendships/{id}/reject", method = RequestMethod.POST)
    public ResponseEntity rejectFriend(@PathVariable(value = "id") int friendshipId) {

        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        userDao.removeFriendship(friendshipId, userId);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/friendships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity unFriend(@PathVariable(value = "id") int friendshipId) {

        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        userDao.removeFriendship(friendshipId, userId);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/events/attending", method = RequestMethod.GET)
    public ResponseEntity getAttendingEvents(@PathVariable(value = "userId") int userId) {

        List<Event> events = eventDao.getAttendingEvents(userId);

        List<EventDtoOut> eventsOut = new ArrayList<EventDtoOut>();
        Iterator<Event> it = events.iterator();
        while(it.hasNext()) {
            eventsOut.add(new EventDtoOut(it.next()));
        }

        return new ResponseEntity<>(eventsOut, HttpStatus.OK);
    }



    /*
     * Exception handling
     */
    @ResponseStatus(value=HttpStatus.CONFLICT, reason="Constraint violation")  // 409
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<Status> constraintViolation() {
        return new ResponseEntity<Status>(new Status(-2, "Some persistance constraint occured"),
                HttpStatus.BAD_REQUEST);
    }

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
        return new ResponseEntity<Status>(new Status(-4, "Email already exists in the system"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Status> formatViolation(IllegalArgumentException e) {
        return new ResponseEntity<Status>(new Status(-3, "Entity not found."), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Status> formatViolation(EntityNotFoundException e) {
        return new ResponseEntity<Status>(new Status(-5, "Entity not found."), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Status> formatViolation(EntityConflictException e) {
        return new ResponseEntity<Status>(new Status(-5, "Conflicting entities."), HttpStatus.CONFLICT);
    }
}