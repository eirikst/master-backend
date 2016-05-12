package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserDto;
import com.andreasogeirik.model.dto.outgoing.*;
import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.*;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.andreasogeirik.tools.EntityNotFoundException;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private EventDao eventDao;


    /**
     * Gets a user with given id
     * @param userId
     * @return user
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDtoOut> getUser(@PathVariable(value = "userId") int userId) {
        return new ResponseEntity<UserDtoOut>(new UserDtoOut(userDao.findById(userId)), HttpStatus.OK);
    }

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
     * Updates a user with USER authorization
     * @param user the user object(JSON)
     * @return the user object as JSON with ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDtoOut> updateUser(@RequestBody UserDto user) {
        UserDtoOut userOut = new UserDtoOut(userDao.updateUser(user.getFirstname(), user.getLastname(), user.getLocation(), user.getImageUri(), user.getThumbUri(), ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()));

        return new ResponseEntity<UserDtoOut>(userOut, HttpStatus.OK);
    }

    /**
     * Gets the a given number of posts(10 right now) of the user specified, with an offset specified
     * @param userId id of user as integer
     * @param start offset
     * @return list of 10(or less, if no more present) user userPost objects as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/posts", method = RequestMethod.GET)
    public ResponseEntity<List<PostDtoOut>> getPosts(@PathVariable(value = "userId") int userId,
                                                         @RequestParam(value = "start") int start) {
        if(start < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        List<Post> posts = postDao.findPostsUser(userId, start, Constants.NUMBER_OF_POSTS_RETURNED);

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
                comment.setLikersFromEntity(commentEntity.getLikes());
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
            Event event = it.next();
            EventDtoOut eventOut = new EventDtoOut(event);
            eventOut.setAdmin(new UserDtoOut(event.getAdmin()));
            Set<UserDtoOut> eventUsersOut = new HashSet<>();
            for(com.andreasogeirik.model.entities.User user: event.getUsers()) {
                eventUsersOut.add(new UserDtoOut(user));
            }
            eventOut.setUsers(eventUsersOut);

            eventsOut.add(eventOut);
        }

        return new ResponseEntity<>(eventsOut, HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/{userId}/events/attended", method = RequestMethod.GET)
    public ResponseEntity getAttendingEventsPast(@PathVariable(value = "userId") int userId,
                                                 @RequestParam(value = "start") int start) {

        List<Event> events = eventDao.getAttendedEvents(userId, start);

        List<EventDtoOut> eventsOut = new ArrayList<EventDtoOut>();
        Iterator<Event> it = events.iterator();
        while(it.hasNext()) {
            Event event = it.next();
            EventDtoOut eventOut = new EventDtoOut(event);
            eventOut.setAdmin(new UserDtoOut(event.getAdmin()));
            Set<UserDtoOut> eventUsersOut = new HashSet<>();
            for(com.andreasogeirik.model.entities.User user: event.getUsers()) {
                eventUsersOut.add(new UserDtoOut(user));
            }
            eventOut.setUsers(eventUsersOut);

            eventsOut.add(eventOut);
        }

        return new ResponseEntity<>(eventsOut, HttpStatus.OK);
    }

    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity searchUsers(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "offset") int offset) {
        List<com.andreasogeirik.model.entities.User> users = userDao.searchUsers(name, offset);
        List<UserDtoOut> usersOut = new ArrayList<>();

        for(com.andreasogeirik.model.entities.User user: users) {
            usersOut.add(new UserDtoOut(user));
        }

        return new ResponseEntity<>(usersOut, HttpStatus.OK);
    }


    /*
     * Exception handling
     */
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<Status> constraintViolation(org.hibernate.exception.ConstraintViolationException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());

        return new ResponseEntity<Status>(new Status(-2, "Some persistence constraint occurred"),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity inputLengthViolation(DataException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity("Input length violation", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Status> violation(InvalidInputException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(0, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Status> formatViolation(MethodArgumentTypeMismatchException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-1, "Input of wrong type(eg. string when expecting integer)"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Status> formatViolation(IllegalArgumentException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-3, "Entity not found."), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Status> formatViolation(EntityNotFoundException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-5, "Entity not found."), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Status> formatViolation(EntityConflictException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-5, "Conflicting entities."), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Status> formatViolation(EmailExistsException e) {
        Logger.getLogger(getClass().getSimpleName()).warning(e.getMessage());
        return new ResponseEntity<Status>(new Status(-4, "Email already exists in the system"), HttpStatus.CONFLICT);
    }
}