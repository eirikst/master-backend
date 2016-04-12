package com.andreasogeirik.model.dto.outgoing;

/**
 * Created by andrena on 12.04.2016.
 */
public class ImageDtoOut {
    private String imageUri;
    private String thumbUri;

    public ImageDtoOut(String imageUri, String thumbUri) {
        this.imageUri = imageUri;
        this.thumbUri = thumbUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }
}
