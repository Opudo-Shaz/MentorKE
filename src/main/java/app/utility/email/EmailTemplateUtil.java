package app.utility.email;

import java.util.Map;

import jakarta.ejb.Singleton;



@Singleton
public class EmailTemplateUtil {

    
    private EmailTemplateUtil() {
    }


    public static String loadTemplate(String templateName) {
        return switch (templateName) {
            case "mentor-email.html" -> getMentorTemplate();
            case "mentee-email.html" -> getMenteeTemplate();
            case "admin-created-account.html" -> getAdminCreatedTemplate();
            default -> getWelcomeTemplate();
        };
    }

    /**
     * Populate template by replacing ${key} with values from map
     */
    public static String populateTemplate(String template, Map<String, String> values) {
        String result = template;

        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
            }
        }

        return result;
    }

    // ===== SIMPLE HTML TEMPLATES =====

    private static String getWelcomeTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #f5f5f5; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: white; padding: 30px; }
                    .footer { background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #666; }
                    .button { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to MentorKE! </h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>${name}</strong>,</p>
                        <p>Thank you for registering on MentorKE!</p>
                        <p>Your role: <strong>${role}</strong></p>
                        <p>You can now log in and start using the platform.</p>
                        <p>If you have any questions, feel free to contact us.</p>
                        <p>Best regards,<br><strong>The MentorKE Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 MentorKE. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private static String getMentorTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #f5f5f5; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { background-color: white; padding: 30px; }
                    .footer { background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome, Mentor! </h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>${name}</strong>,</p>
                        <p>We're excited to have you as a Mentor on MentorKE!</p>
                        <p>Your specialization: <strong>${specialization}</strong></p>
                        <p>You can now:</p>
                        <ul>
                            <li>View your mentor profile</li>
                            <li>Accept mentee requests</li>
                            <li>Manage mentoring sessions</li>
                        </ul>
                        <p>Thank you for contributing to the community!</p>
                        <p>Best regards,<br><strong>The MentorKE Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 MentorKE. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private static String getMenteeTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #f5f5f5; }
                    .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
                    .content { background-color: white; padding: 30px; }
                    .footer { background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome, Mentee! 📚</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>${name}</strong>,</p>
                        <p>Welcome to MentorKE - your learning journey starts here!</p>
                        <p>You can now:</p>
                        <ul>
                            <li>Browse available mentors</li>
                            <li>Request a mentor to guide you</li>
                            <li>Schedule mentoring sessions</li>
                            <li>Track your progress</li>
                        </ul>
                        <p>Let's find the perfect mentor for you!</p>
                        <p>Best regards,<br><strong>The MentorKE Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 MentorKE. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private static String getAdminCreatedTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #f5f5f5; }
                    .header { background-color: #0d47a1; color: white; padding: 20px; text-align: center; }
                    .content { background-color: white; padding: 30px; }
                    .credentials { background-color: #f1f5f9; border-left: 4px solid #0d47a1; padding: 15px; margin: 20px 0; }
                    .button { background-color: #0d47a1; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }
                    .footer { background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your MentorKE Account</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>${name}</strong>,</p>
                        <p>An administrator has created an account for you on MentorKE as a <strong>${role}</strong>.</p>
                        <div class="credentials">
                            <p><strong>Temporary Password:</strong> ${tempPassword}</p>
                        </div>
                        <p>For your security, please log in and set a new password immediately. You will be required to change it before accessing the platform.</p>
                        <p><a class="button" href="http://mentorke.app/login">Log In &amp; Set Password</a></p>
                        <p style="color:#94a3b8;font-size:12px;margin-top:30px;">If you did not expect this email, please contact MentorKE support immediately.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 MentorKE. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}