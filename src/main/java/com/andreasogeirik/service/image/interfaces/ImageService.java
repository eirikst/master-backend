package com.andreasogeirik.service.image.interfaces;

/**
 * Created by Andreas on 18.02.2016.
 */
public interface ImageService {
    String saveImage(String image);
    byte[] getEncodedImage(String imageUri);
}
