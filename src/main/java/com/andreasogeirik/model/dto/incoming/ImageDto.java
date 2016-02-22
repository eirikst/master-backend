package com.andreasogeirik.model.dto.incoming;

/**
 * Created by Andreas on 19.02.2016.
 */
public class ImageDto {
    private String encodedImage;

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
