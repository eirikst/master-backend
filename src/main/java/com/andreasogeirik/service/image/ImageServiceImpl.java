package com.andreasogeirik.service.image;

import com.andreasogeirik.service.image.interfaces.ImageService;
import org.apache.commons.lang.RandomStringUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageServiceImpl implements ImageService {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    @Override
    public String saveImage(byte[] byteImage) {
        if (byteImage != null) {
            String randomFileName = RandomStringUtils.randomAlphanumeric(20);
            File file = new File("img/");
            if(file.mkdir()){
                logger.warning("The img folder didn't exist. A new was created");
            }
            try {
                FileOutputStream imageOutFile = new FileOutputStream("img/" + randomFileName + ".jpg");
                imageOutFile.write(byteImage);
                imageOutFile.close();
                logger.info("Image Successfully Stored");
                return randomFileName;
            } catch (FileNotFoundException fnfe) {
                logger.warning("Image Path not found" + fnfe);
            } catch (IOException ioe) {
                logger.warning("Exception while converting the Image " + ioe);
            }
        }
        return null;
    }

    @Override
    public String saveThumb(byte[] byteImage, int targetSize) {
        if (byteImage != null) {
            // Creates a stream from byteImage
            ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
            try {
                // Creates a buffered image from stream
                BufferedImage img = ImageIO.read(in);
                // Scales the image
                BufferedImage scaledImg = Scalr.resize(img, 216);
                String randomFileName = RandomStringUtils.randomAlphanumeric(20);

                // Write buffer to file
                FileOutputStream imageOutFile = new FileOutputStream("img/" + randomFileName + ".jpg");
                ImageIO.write(scaledImg, "jpg", imageOutFile);
                imageOutFile.close();
                logger.info("Image Successfully Stored");
                in.close();
                return randomFileName;
            } catch (IOException e) {
                return null;
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
