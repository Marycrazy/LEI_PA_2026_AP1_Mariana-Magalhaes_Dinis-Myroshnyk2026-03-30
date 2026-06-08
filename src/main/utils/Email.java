package main.utils;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import main.PropertiesManager;

public class Email {

    private static Session session;

    private static Session buildSession(PropertiesManager props) {
        String username = props.getProperty("email");
        String password = props.getProperty("key");

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", "smtp.gmail.com");
        mailProps.put("mail.smtp.port", "465");
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.ssl.enable", "true");

        return Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static void sendRegistrationEmail(PropertiesManager props, String toAddress, String userName)
            throws MessagingException {
        if (session == null) session = buildSession(props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(props.getProperty("email")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        msg.setSubject("Registration confirmation");
        msg.setText("Hello " + userName + ",\n\nYour account is awaiting approval. Please wait while an admin reviews your request.");
        Transport.send(msg);
    }
}