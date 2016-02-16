package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.security.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class RootController {

    @Autowired
    private UserDao userDao;

    /*
     * Check if user is logged in. Returns the logged in user with a 200 OK if logged in. 401 Unauthorized if not.
     */
    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<UserDtoOut> logInCheck() throws IOException {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();

        UserDtoOut me = new UserDtoOut(userDao.findById(userId));
        return new ResponseEntity<UserDtoOut>(me, HttpStatus.OK);
    }
}