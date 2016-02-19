package com.andreasogeirik.controllers;

import com.andreasogeirik.model.dto.incoming.ImageDto;
import com.andreasogeirik.service.image.interfaces.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> saveImage(@RequestBody ImageDto image) throws IOException {

        String imageUrl = imageService.saveImage(image.getImage(), image.getFilename());

        return new ResponseEntity(imageUrl, HttpStatus.OK);
    }
}
