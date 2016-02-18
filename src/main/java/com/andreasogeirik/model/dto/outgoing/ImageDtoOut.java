package com.andreasogeirik.model.dto.outgoing;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageDtoOut {
    String imageUri;

    public ImageDtoOut(String imageUrl) {
        this.imageUri = imageUrl;
    }

    public String getImageUrl() {
        return imageUri;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUri = imageUrl;
    }
}
