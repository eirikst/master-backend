package com.andreasogeirik.service;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



/**
 * Created by eirikstadheim on 07/04/16.
 */
public class EmailNotifier {
    private static Logger logger = Logger.getLogger(EmailNotifier.class.getSimpleName());

    public static void sendEmail(String msg) {

        final String username = "sportydul123@gmail.com";
        final String password = "Arif1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("sportydul123@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("eirik_stadheim@outlook.com"));
            message.setSubject("Login");
            message.setText(msg);

            Transport.send(message);

        } catch (MessagingException e) {
            logger.warning("Mail not sent. " + e);
            throw new RuntimeException(e);
        }
    }



}
