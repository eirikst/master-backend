package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.UserPostDto;
import com.andreasogeirik.model.dto.outgoing.*;
import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.UserPost;
import com.andreasogeirik.model.entities.UserPostComment;
import com.andreasogeirik.model.entities.UserPostLike;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.andreasogeirik.service.dao.interfaces.UserPostDao;
import com.andreasogeirik.tools.Constants;
import com.andreasogeirik.tools.EmailExistsException;
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
@RequestMapping("/me")
public class MeController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPostDao postDao;

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
     * Gets friendships of the logged in user.
     * @return JSONArray with Friendship objects.
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "/friendships", method = RequestMethod.GET)
    public ResponseEntity<Set<FriendshipDtoOut>> getFriendships() {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();


        List<Friendship> friendships = userDao.findFriendsAndRequests(userId);
        for(int i = 0; i < friendships.size(); i++) {
            System.out.printf(friendships.get(i).toString());
        }

        Set<FriendshipDtoOut> friendshipsOut = new HashSet<FriendshipDtoOut>();

        Iterator<com.andreasogeirik.model.entities.Friendship> it = friendships.iterator();
        while(it.hasNext()) {
            friendshipsOut.add(new FriendshipDtoOut((Friendship)it.next(), userId));
        }

        return new ResponseEntity<Set<FriendshipDtoOut>>(friendshipsOut, HttpStatus.OK);
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