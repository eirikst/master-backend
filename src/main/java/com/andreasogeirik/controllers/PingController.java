package com.andreasogeirik.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;


@RestController
@RequestMapping("/ping")
public class PingController {
    Logger logger = Logger.getLogger(getClass().getName());

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity ping() {
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Pings itself with a fixed delay. Used because we have had trouble with azure going down when the server is
     * inactive...
     */
    /*@Scheduled(fixedDelay = 300000)
    private void pingSelf () {
        new Thread() {
            public void run() {
                ResponseEntity<String> response;
                RestTemplate template = new RestTemplate();
                ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity(null, headers);
                try {
                    response = template.exchange(Constants.BACKEND_URL + "ping", HttpMethod.GET, entity, String.class);
                    logger.info("Ping self returned OK at " + new Date());
                } catch (Exception e) {
                    logger.warning("Ping self failed at " + new Date());
                }
            }
        }.start();
    }*/
}