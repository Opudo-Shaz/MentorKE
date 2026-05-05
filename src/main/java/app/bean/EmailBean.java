package app.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

@ApplicationScoped
public class EmailBean {

    private final String username = "patikalostandfound@gmail.com";
    private final String password = "rtos djvp dque xtqr";


    public void sendEmail(String to, String subject, String body) {
        System.out.println("Attempting to send email to: " + to);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.required", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "MentorKE"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Support HTML content
            if (body.contains("<html>") || body.contains("<!DOCTYPE")) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }

            Transport.send(message);
            System.out.println("[EmailBean] ✓ Email sent successfully to " + to);

        } catch (AuthenticationFailedException e) {
            System.err.println("[EmailBean] ✗ Authentication failed!");
            System.err.println("  Update credentials in EmailBean: username and password");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("[EmailBean] ✗ Error sending email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[EmailBean] ✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}