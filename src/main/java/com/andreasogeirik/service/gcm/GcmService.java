package com.andreasogeirik.service.gcm;

import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by eirikstadheim on 06/04/16.
 */
public class GcmService {
    private Logger logger = Logger.getLogger(getClass().getName());
    private ObjectMapper mapper;

    @Autowired
    private UserDao userDao;

    public GcmService() {
        mapper = new ObjectMapper();
    }


    public void notifyFriendRequest(String name, int userId) {
        notifyUser(name + " har sendt deg en venneforespørsel!", userId);
    }

    public void notifyFriendAccepted(String name, int userId) {
        notifyUser(name + " har godtatt din venneforespørsel!", userId);
    }

    /*
     * Only for test purposes
     */
    public void notifyTest(String msg, int userId) {
        notifyUser(msg, userId);
    }

    /*
     * General purpose notification service, that receives the msg to display and the user's id
     */
    private void notifyUser(String msg, int userId) {
        new Thread() {
            public void run() {
                String data = "{ \"data\": {\"msg\":\"" + msg + "\"}";

                Set<String> gcmTokens = userDao.getGcmTokensByUserId(userId);

                for(String token: gcmTokens) {
                    ResponseEntity<String> response;
                    RestTemplate template = new RestTemplate();
                    ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "application/json");
                    headers.add("Authorization", "key=" + "AIzaSyAzGkBvA_kYil5hLdPSv-26MdezrXJZfqo");

                    HttpEntity<String> entity = new HttpEntity(data + ",\"to\" : \"" + token + "\"}", headers);
                    try {
                        response = template.exchange("https://gcm-http.googleapis.com/gcm/send", HttpMethod.POST, entity, String.class);

                        final JsonNode node = mapper.readTree(response.getBody());

                        //if node not registered
                        if ("0".equals(node.get("success").asText())) {
                            final JsonNode arrNode = node.get("results");

                            for (JsonNode node1 : arrNode) {
                                if (node1.get("error").asText().equals("NotRegistered") || node1.get("error").asText().equals("InvalidRegistration")) {
                                    userDao.removeGcmToken(userId, token);
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.warning("Gcm failed at " + new Date() + " for user " + userId + " and token " + token +
                                ". Reason: " + e.getMessage());
                    }
                }
            }
        }.start();
    }
}
