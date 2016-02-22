package com.andreasogeirik.service.image;

import com.andreasogeirik.service.image.interfaces.ImageService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageServiceImpl implements ImageService {

    @Override
    public String saveImage(String image) {
        if (image != null) {
            try {
                byte[] imageByteArray = Base64.decodeBase64(image);

                FileOutputStream imageOutFile = new FileOutputStream(RandomStringUtils.randomAlphanumeric(20) + "." + "jpg");
                imageOutFile.write(imageByteArray);
                imageOutFile.close();

                System.out.println("Image Successfully Stored");
                return "";
            } catch (FileNotFoundException fnfe) {
                System.out.println("Image Path not found" + fnfe);
            } catch (IOException ioe) {
                System.out.println("Exception while converting the Image " + ioe);
            }
        }
        return null;
    }
}
