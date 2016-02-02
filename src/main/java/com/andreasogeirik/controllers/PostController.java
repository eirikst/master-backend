package com.andreasogeirik.controllers;

import com.andreasogeirik.model.Greeting;
import com.andreasogeirik.service.dao.PostDao;
import com.andreasogeirik.service.dao.UserDao;
import com.andreasogeirik.tools.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostDao postDao;


    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Status> post(@RequestParam(value="message") String message,
                                             @RequestParam(value="imageUri") String imageUri,
                                             @RequestParam(value="userId") int userId,
                                             HttpServletResponse response) throws IOException {

        int status = postDao.newPost(message, imageUri, userId);

        if(status == PostDao.INVALID_MESSAGE) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid message"), HttpStatus.BAD_REQUEST);
        }
        if(status == PostDao.INVALID_URI) {
            return new ResponseEntity<Status>(new Status(-2, "Invalid URI"), HttpStatus.BAD_REQUEST);
        }
        if(status == PostDao.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-3, "User not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{postId}/comment", method = RequestMethod.PUT)
    public ResponseEntity<Status> comment(@RequestParam(value="message") String message,
                                          @PathVariable(value="postId") int postId,
                                            @RequestParam(value="userId") int userId,
                                             HttpServletResponse response) throws IOException {

        int status = postDao.comment(message, postId, userId);

        if(status == PostDao.INVALID_COMMENT_MESSAGE) {
            return new ResponseEntity<Status>(new Status(-1, "Invalid message"), HttpStatus.BAD_REQUEST);
        }
        if(status == PostDao.POST_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-2, "Post not found"), HttpStatus.BAD_REQUEST);
        }
        if(status == PostDao.USER_NOT_FOUND) {
            return new ResponseEntity<Status>(new Status(-3, "User not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Status>(new Status(1, "Created"), HttpStatus.CREATED);
    }




    /*
     *  Methods for testing purposes
     */
    @RequestMapping(method = RequestMethod.GET)
    public Greeting testGet(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(1L, "Hei, " + name);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Greeting testPost(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(1L, "Hei, " + name);
    }
}