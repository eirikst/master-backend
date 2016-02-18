package com.andreasogeirik.service.image;

import com.andreasogeirik.model.dto.incoming.ImageDto;
import com.andreasogeirik.service.image.interfaces.ImageService;
import com.andreasogeirik.tools.InvalidInputException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageServiceImpl implements ImageService {

    @Override
    public String saveImage(ImageDto image) {
        if (image != null) {
            String encodedImage = image.getEncodedImage();
            try {
                byte[] decoded = Base64.getDecoder().decode(encodedImage);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(decoded));
                File outputfile = new File("pic.jpg");
                ImageIO.write(img, "jpg", outputfile);
                File file = new File("pic.jpg");
                return file.toPath().toString();
            } catch (IOException e) {
                throw new InvalidInputException("Could not decode image");
            }
        }
        return null;
    }
}
