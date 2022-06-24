package com.phunghung29.securitydemo.service;

import com.phunghung29.securitydemo.Util.Utils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
public class MailService {
//    HttpTransport
//    public void senEmial(String email, String subject, String content, String type)
//    {
//        try {
//            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//            Properties mailCredential = Utils.loadProperties("mail.credential.properties");
//            Properties props = new Properties();
//            Session session = Session.getDefaultInstance(props, null);
//            MimeMessage mailContent = new MimeMessage(session);
//            assert mailCredential != null;
//            mailContent.setFrom(new InternetAddress(mailCredential.getProperty("email")));
//            mailContent.addRecipient(javax.mail.Message.RecipientType.TO,
//                    new InternetAddress(email));
//            mailContent.setSubject(subject, "utf-8");
//            mailContent.setContent(content, type);
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            mailContent.writeTo(buffer);
//            byte[] rawMessageBytes = buffer.toByteArray();
//            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
//            Message message = new Message();
//            message.setRaw(encodedEmail);
//            // Create send message
//            message = service.users().messages().send("me", message).execute();
//        } catch (GoogleJsonResponseException e) {
//            throw new MailServiceException("Unable to send message: " + e.getDetails());
//        } catch (MessagingException | IOException e) {
//            throw new MailServiceException(e.getMessage());
//        }
//    }
}
