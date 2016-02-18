package com.andreasogeirik.tools;

import java.util.Base64;

/**
 * Created by Andreas on 18.02.2016.
 */
public class ImageHandler {
    public static byte[] decodeBase64(String input)
    {
        return Base64.getDecoder().decode(input);
    }
}
