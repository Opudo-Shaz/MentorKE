package app.bean;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import app.utility.logging.AppLogger;
import org.slf4j.Logger;

@ApplicationScoped
public class EmailBean {

    private static final Logger logger = AppLogger.getLogger(EmailBean.class);

    @Resource(lookup = "java:/Mail")
    private Session mailSession;

    public void sendEmail(String to, String subject, String body) {

        logger.info("Attempting to send email to: {}", to);

        try {

            MimeMessage message =
                    new MimeMessage(mailSession);

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            message.setSubject(subject);

            // sender
            message.setFrom(
                    new InternetAddress(
                            "patikalostandfound@gmail.com",
                            "MentorKE"
                    )
            );

            // support html emails
            if (body.contains("<html>")
                    || body.contains("<!DOCTYPE")) {

                message.setContent(
                        body,
                        "text/html; charset=UTF-8"
                );

            } else {

                message.setText(body);
            }

            Transport.send(message);

            logger.info(
                    "Email sent successfully to {}",
                    to
            );

        } catch (MessagingException e) {

            logger.error(
                    "Mail error: {}",
                    e.getMessage(),
                    e
            );

        } catch (Exception e) {

            logger.error(
                    "Unexpected error: {}",
                    e.getMessage(),
                    e
            );
        }
    }
}