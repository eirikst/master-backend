package com.andreasogeirik.service.image.interfaces;

import com.andreasogeirik.model.dto.incoming.ImageDto;

/**
 * Created by Andreas on 18.02.2016.
 */
public interface ImageService {
    String saveImage(ImageDto image);
}
