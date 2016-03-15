package com.andreasogeirik.service.image;

import com.andreasogeirik.service.image.interfaces.ImageService;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageServiceImpl implements ImageService {

    @Override
    public String saveImage(byte[] byteImage) {
        if (byteImage != null) {
            String randomFileName = RandomStringUtils.randomAlphanumeric(20);
            File file = new File("img/");
            if(file.mkdir()){
                System.out.println("The img folder didn't exist. A new was created");
            }
            try {
                FileOutputStream imageOutFile = new FileOutputStream("img/" + randomFileName + ".jpg");
                imageOutFile.write(byteImage);
                imageOutFile.close();
                System.out.println("Image Successfully Stored");
                return randomFileName;
            } catch (FileNotFoundException fnfe) {
                System.out.println("Image Path not found" + fnfe);
            } catch (IOException ioe) {
                System.out.println("Exception while converting the Image " + ioe);
            }
        }
        return null;
    }

    @Override
    public byte[] getImage(String imageUri) {
        File file = new File("img/" + imageUri + ".jpg");
        byte[] b = new byte[(int) file.length()];
        try {
            InputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            return b;
//            return Base64.encodeBase64String(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
