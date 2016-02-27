package com.andreasogeirik;

import com.andreasogeirik.tools.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        try {
            Constants.BACKEND_URL =  "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/";
            System.out.println("BACKEND IP SET TO " + InetAddress.getLocalHost().getHostAddress());
        }
        catch(UnknownHostException e) {
            System.out.println("Unknown host. " + e);
        }
    }
}