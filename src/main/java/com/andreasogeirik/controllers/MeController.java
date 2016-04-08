package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserPostDto;
import com.andreasogeirik.model.dto.outgoing.*;
import com.andreasogeirik.model.entities.*;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.EventDao;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.Constants;
import com.andreasogeirik.tools.EmailExistsException;
import com.andreasogeirik.tools.InvalidInputException;
import com.andreasogeirik.tools.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
    private UserPostDao postDao;

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
     * Gets the a given number of UserPosts(10 right now) for the logged in user, with an offset specified
     * @param start offset
     * @return list of 10(or less, if no more present) user post objects as JSON
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/posts", method = RequestMethod.GET)
    public ResponseEntity<List<UserPostDtoOut>> getPosts(@RequestParam(value = "start") int start) {
        if(start < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        List<UserPost> posts = postDao.findPosts(userId, start, Constants.NUMBER_OF_POSTS_RETURNED);

        List<UserPostDtoOut> postsOut = new ArrayList<UserPostDtoOut>();

        for(int i = 0; i < posts.size(); i++) {
            UserPostDtoOut postOut = new UserPostDtoOut(posts.get(i));

            //iterate comments
            Set<UserPostCommentDtoOut> comments = new HashSet<>();
            Iterator<UserPostComment> it = posts.get(i).getComments().iterator();
            while(it.hasNext()) {
                comments.add(new UserPostCommentDtoOut(it.next()));
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
     * Creates a UserPost for the logged in user
     * @param post UserPost represented as JSON
     * @return the UserPost represented as JSON with ID
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/posts",method = RequestMethod.PUT)
    public ResponseEntity<Status> post(@RequestBody UserPostDto post) {

        postDao.createUserPost(post.toPost(),
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    /**
     * Updates a user's password with USER authorization
     * @param prevPassword the previous password
     * @param newPassword the new password
     * @return HttpStatus
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> updatePassword(@RequestParam(value = "prevPassword") String prevPassword, @RequestParam(value = "newPassword") String newPassword) {
        userDao.updatePassword(prevPassword, newPassword, ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
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

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Status> formatViolation(EmailExistsException e) {
        return new ResponseEntity<Status>(new Status(-1, "Email already exists in the system"), HttpStatus.CONFLICT);
    }

}