package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.utility.email.EmailTemplateUtil;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class EmailObserverBean {

    private static final Logger logger = AppLogger.getLogger(EmailObserverBean.class);

    @Inject
    private EmailReminderBean emailBean;

    @Asynchronous
    public void onUserRegistered(@Observes UserRegisteredEvent event) {
        logger.info("User registered event received for: {}", event.getName());

        try {
            String templateName;
            String subject;
            Map<String, String> values = new HashMap<>();
            values.put("name", event.getName());
            values.put("role", event.getRole());
            values.put("specialization", event.getSpecialization() != null ? event.getSpecialization() : "");

            if (event.hasTempPassword()) {
                // Admin-created account — send temp password + force reset
                templateName = "admin-created-account.html";
                subject = "Your MentorKE Account Has Been Created";
                values.put("tempPassword", event.getTempPassword());
            } else if ("MENTOR".equals(event.getRole())) {
                templateName = "mentor-email.html";
                subject = "Welcome to MentorKE - " + event.getRole();
            } else if ("MENTEE".equals(event.getRole())) {
                templateName = "mentee-email.html";
                subject = "Welcome to MentorKE - " + event.getRole();
            } else {
                templateName = "welcome-email.html";
                subject = "Welcome to MentorKE - " + event.getRole();
            }

            // Load template
            String template = EmailTemplateUtil.loadTemplate(templateName);

            // Populate template
            String html = EmailTemplateUtil.populateTemplate(template, values);

            // Send email
            emailBean.sendEmail(event.getEmail(), subject, html);

            logger.info("Email scheduled for: {}", event.getName());

        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}