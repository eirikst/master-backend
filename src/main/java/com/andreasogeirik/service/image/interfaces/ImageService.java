package com.andreasogeirik.service.image.interfaces;

/**
 * Created by Andreas on 18.02.2016.
 */
public interface ImageService {
    String saveImage(byte[] byteImage);
    String saveThumb(byte[] byteImage, int targetSize);
    byte[] getImage(String imageUri);
}
