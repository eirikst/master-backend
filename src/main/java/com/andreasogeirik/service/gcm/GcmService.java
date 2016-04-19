package com.andreasogeirik.service.gcm;

import com.andreasogeirik.model.entities.Friendship;
import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by eirikstadheim on 06/04/16.
 */
public class GcmService {
    private Logger logger = Logger.getLogger(getClass().getName());
    private ObjectMapper mapper;

    public static final String DEFAULT_ACTION = "default";
    public static final String EVENT_ACTION = "event";

    @Autowired
    private UserDao userDao;

    public GcmService() {
        mapper = new ObjectMapper();
    }


    public void notifyFriendRequest(String name, int userId) {
        notifyUser(name + " har sendt deg en venneforespørsel!", userId, DEFAULT_ACTION, -1);
    }

    public void notifyFriendAccepted(String name, int userId) {
        notifyUser(name + " har godtatt din venneforespørsel!", userId, DEFAULT_ACTION, -1);
    }

    public void notifyInactiveWeek(Set<Integer> ids) {
        for(Integer id: ids) {
            notifyUser("Meld deg på eller lag en aktivitet denne uka, så blir uka topp :)", id, DEFAULT_ACTION, -1);
        }
    }

    public void notifyNewEvent(int adminId, String firstname, String lastname, int eventId) {
        List<Friendship> friendships = userDao.findFriends(adminId);
        Set<Integer> ids = userDao.findFriendsIds(adminId);

        for(Integer id: ids) {
            notifyUser(firstname + " har akkurat opprettet en aktivitet!", id, EVENT_ACTION, eventId);
        }
    }

    /*
     * Only for test purposes
     */
    public void notifyTest(String msg, int userId) {
        notifyUser(msg, userId, DEFAULT_ACTION, -1);
    }

    /*
     * General purpose notification service, that receives the msg to display and the user's id
     */
    private void notifyUser(String msg, int userId, String action, int actionId) {action:
        new Thread() {
            public void run() {
                String data = "{ \"data\": {\"msg\":\"" + msg + "\", \"action\":\"" + action + "\", \"actionId\":\"" + actionId + "\"}";

                Set<String> gcmTokens = userDao.getGcmTokensByUserId(userId);

                for(String token: gcmTokens) {
                    ResponseEntity<String> response;
                    RestTemplate template = new RestTemplate();
                    ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json; charset=utf-8");
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
