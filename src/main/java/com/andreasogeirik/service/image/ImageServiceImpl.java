package com.andreasogeirik.service.image;

import com.andreasogeirik.model.dto.incoming.ImageDto;
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
    public String saveImage(ImageDto imageDto) {
        if (imageDto.getEncodedImage() != null) {
            try {
                byte[] imageByteArray = Base64.decodeBase64(imageDto.getEncodedImage());
                String filePath = RandomStringUtils.randomAlphanumeric(20) + "." + "jpg";
                FileOutputStream imageOutFile = new FileOutputStream(filePath);
                imageOutFile.write(imageByteArray);
                imageOutFile.close();
                System.out.println("Image Successfully Stored");
                return filePath;
            } catch (FileNotFoundException fnfe) {
                System.out.println("Image Path not found" + fnfe);
            } catch (IOException ioe) {
                System.out.println("Exception while converting the Image " + ioe);
            }
        }
        return null;
    }
}
