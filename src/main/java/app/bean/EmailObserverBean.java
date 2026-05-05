package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.utility.email.EmailTemplateUtil;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.Map;

@Stateless
public class EmailObserverBean {

    @Inject
    private EmailBean emailBean;

    @Asynchronous
    public void onUserRegistered(@Observes UserRegisteredEvent event) {
        System.out.println("[EmailObserverBean] User registered event received for: " + event.getName());

        try {
            // Load appropriate template based on role
            String templateName = "welcome-email.html";
            if ("MENTOR".equals(event.getRole())) {
                templateName = "mentor-email.html";
            } else if ("MENTEE".equals(event.getRole())) {
                templateName = "mentee-email.html";
            }

            // Load template
            String template = EmailTemplateUtil.loadTemplate(templateName);

            // Prepare template variables
            Map<String, String> values = Map.of(
                "name", event.getName(),
                "role", event.getRole(),
                "specialization", event.getSpecialization() != null ? event.getSpecialization() : ""
            );

            // Populate template
            String html = EmailTemplateUtil.populateTemplate(template, values);

            // Send email
            String subject = "Welcome to MentorKE - " + event.getRole();
            emailBean.sendEmail(event.getEmail(), subject, html);

            System.out.println("[EmailObserverBean] Email scheduled for: " + event.getName());

        } catch (Exception e) {
            System.err.println("[EmailObserverBean] Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}