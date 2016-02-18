package com.andreasogeirik.model.dto.incoming;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageDto {
    String encodedImage;

    public ImageDto() {
    }

    public ImageDto(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}