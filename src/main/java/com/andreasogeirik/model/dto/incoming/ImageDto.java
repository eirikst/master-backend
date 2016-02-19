package com.andreasogeirik.model.dto.incoming;

/**
 * Created by Andreas on 19.02.2016.
 */
public class ImageDto {
    private String image;
    private String filename;

    public ImageDto() {
    }

    public ImageDto(String image, String filename) {
        this.image = image;
        this.filename = filename;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
