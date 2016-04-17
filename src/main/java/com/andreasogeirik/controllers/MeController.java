package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.PostDto;
import com.andreasogeirik.model.dto.outgoing.*;
import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.PostDao;
import com.andreasogeirik.tools.*;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/me")
public class MeController {
    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private EventDao eventDao;

    /**
     * Get the user entity of the logged in user
     * @return User entity of logged in user as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<UserDtoOut> getMe() {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        return new ResponseEntity<UserDtoOut>(new UserDtoOut(userDao.findById(userId)), HttpStatus.OK);
    }


    /**
     * Creates a Post for the logged in user
     * @param post Post represented as JSON
     * @return the Post represented as JSON with ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/posts",method = RequestMethod.PUT)
    public ResponseEntity<PostDtoOut> post(@RequestBody PostDto post) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        Post postEntityOut = postDao.userPost(post.getMessage(), "", userId, userId);
        PostDtoOut postOut = new PostDtoOut(postEntityOut);

        return new ResponseEntity<PostDtoOut>(postOut, HttpStatus.CREATED);
    }

    /**
     * Gets the a given number of Posts(10 right now) for the logged in user, with an offset specified
     * @param start offset
     * @return list of 10(or less, if no more present) user userPost objects as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public ResponseEntity<List<PostDtoOut>> getPosts(@RequestParam(value = "start") int start) {
        if(start < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

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
            Set<UserDtoOutSmall> likers = new HashSet<>();
            Iterator<PostLike> likeIt = posts.get(i).getLikes().iterator();
            while(likeIt.hasNext()) {
                likers.add(new UserDtoOutSmall(likeIt.next().getUser()));
            }


            postOut.setLikers(likers);
            postsOut.add(postOut);
        }

        return new ResponseEntity<>(postsOut, HttpStatus.OK);
    }

    /**
     * Updates a user's password with USER authorization
     * @param currentPassword the previous password
     * @param newPassword the new password
     * @return HttpStatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> updatePassword(@RequestParam(value = "currentPassword") String currentPassword, @RequestParam(value = "newPassword") String newPassword) {
        userDao.updatePassword(currentPassword, newPassword, ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets friendships of the logged in user(including requests).
     * @return JSONArray with Friendship objects.
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/friendships", method = RequestMethod.GET)
    public ResponseEntity<Set<FriendshipDtoOut>> getFriendships() {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();


        List<Friendship> friendships = userDao.findFriendsAndRequests(userId);

        Set<FriendshipDtoOut> friendshipsOut = new HashSet<FriendshipDtoOut>();

        Iterator<com.andreasogeirik.model.entities.Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            friendshipsOut.add(new FriendshipDtoOut((Friendship)it.next(), userId));
        }

        return new ResponseEntity<Set<FriendshipDtoOut>>(friendshipsOut, HttpStatus.OK);
    }

    /**
     * Finds the events where logged in user is admin
     * @return list of events
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ResponseEntity<Set<EventDtoOut>> findMyEvents() {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();


        List<Event> events = eventDao.getAdminEvents(userId);

        Set<EventDtoOut> eventsOut = new HashSet<>();
        for(Event event: events) {
            EventDtoOut eventOut = new EventDtoOut(event);
            eventOut.setAdmin(new UserDtoOut(event.getAdmin()));
            for(com.andreasogeirik.model.entities.User user: event.getUsers()) {
                eventOut.getUsers().add(new UserDtoOut(user));
            }
            eventsOut.add(eventOut);
        }

        return new ResponseEntity<Set<EventDtoOut>>(eventsOut, HttpStatus.OK);
    }

    /**
     * Finds the past events where logged in user is admin
     * @param offset number of events to skip from the start
     * @return list of past events
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/events/past", method = RequestMethod.GET)
    public ResponseEntity<Set<EventDtoOut>> findMyEventsPast(@RequestParam(value = "offset") int offset) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();


        List<Event> events = eventDao.getAdminEventsPast(userId, offset);

        Set<EventDtoOut> eventsOut = new HashSet<>();
        for(Event event: events) {
            EventDtoOut eventOut = new EventDtoOut(event);
            eventOut.setAdmin(new UserDtoOut(event.getAdmin()));
            for(com.andreasogeirik.model.entities.User user: event.getUsers()) {
                eventOut.getUsers().add(new UserDtoOut(user));
            }
            eventsOut.add(eventOut);
        }

        return new ResponseEntity<Set<EventDtoOut>>(eventsOut, HttpStatus.OK);
    }

    /**
     * Retrieves the recommended events for the user
     * @param offset start fetching after "offset" number of events
     * @return JSON representation of the set of events
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/events/recommended", method = RequestMethod.GET)
    public ResponseEntity<Set<EventDtoOut>> getRecommendedEvents(@RequestParam(value="offset") int offset) {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        List<Event> events = eventDao.getRecommendedEvents(userId, offset);

        Set<EventDtoOut> eventsOut = new HashSet<>();
        for(Event event: events) {
            EventDtoOut eventOut = new EventDtoOut(event);
            eventOut.setAdmin(new UserDtoOut(event.getAdmin()));
            for(com.andreasogeirik.model.entities.User user: event.getUsers()) {
                eventOut.getUsers().add(new UserDtoOut(user));
            }
            eventsOut.add(eventOut);
        }

        return new ResponseEntity(eventsOut, HttpStatus.OK);
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
        return new ResponseEntity<Status>(new Status(-1, "Email already exists in the system"), HttpStatus.CONFLICT);
    }

}