package com.andreasogeirik.controllers;

import com.andreasogeirik.service.image.interfaces.ImageService;
import com.andreasogeirik.tools.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Andreas on 18.02.2016.
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /*
    * Save image
    */
    @PreAuthorize(value = "hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> saveImage(@RequestBody byte[] image) throws IOException {
        String imageUrl = Constants.BACKEND_URL + "image/" + imageService.saveImage(image);

        return new ResponseEntity(imageUrl, HttpStatus.OK);
    }

    /**
     * Retrieves an event by id
     * @param imageUri
     * @return JSON representation of the event with the ID
     */
//    @PreAuthorize(value="hasAuthority('USER')")
    @RequestMapping(value = "{imageUri}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getEvent(@PathVariable(value="imageUri") String imageUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity(imageService.getImage(imageUri), headers, HttpStatus.OK);
    }
}
