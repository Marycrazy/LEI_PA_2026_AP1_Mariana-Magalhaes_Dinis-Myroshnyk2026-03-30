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

/**
 * Orchestrator service class that handles sending system confirmation emails
 * via secure SMTP channels.
 */
public class Email {

    private static Session session;

    /**
     * Internal utility to establish an authenticated security parameters handshake connection
     * workspace instance linking with standard Gmail SMTP relays.
     *
     * @param props configuration settings provider containing core service identity keys
     * @return a new authenticated mailing workspace session setup pointer {@link Session}
     */
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

    /**
     * Sends a transactional email notifying a user that their account creation request
     * is pending review by an administrator.
     *
     * @param props     configuration manager holding target credentials properties maps
     * @param toAddress destination address string where message delivery is directed
     * @param userName  recipient profile display identification name string
     * @throws MessagingException if server authentication fails or network routing drops the transaction
     */
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