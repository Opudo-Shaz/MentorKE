package app.bean;

import app.model.Mentee;
import app.model.Mentor;
import app.utility.helper.PasswordUtil;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Map;



@Stateless
@Named("registrationService")
public class RegistrationService {

    private static final Logger logger = AppLogger.getLogger(RegistrationService.class);

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    public RegistrationService() {

        logger.debug("[RegistrationService] Initialized");
    }

  
    public void registerUser(String username, String password, String email, String role, Map<String, String> roleSpecificData) 
            throws SQLException {
        
        logger.info("[RegistrationService] Starting registration: username={}, role={}", username, role);
        
        if ("mentor".equalsIgnoreCase(role)) {
            registerMentorFlow(username, password, email, roleSpecificData);
        } else if ("mentee".equalsIgnoreCase(role)) {
            registerMenteeFlow(username, password, email, roleSpecificData);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        
        logger.info("[RegistrationService] Registration completed for user: {}", username);
    }

    /**
     * Mentor-specific registration workflow
     */
    private void registerMentorFlow(String username, String password, String email, Map<String, String> data) throws SQLException {
        logger.debug("[RegistrationService] Processing mentor registration");
        
        Mentor mentor = new Mentor();
        mentor.setUsername(username);
        mentor.setPassword(preparePassword(password));
        mentor.setEmail(email);
        mentor.setRole("mentor");
        mentor.setStatus("Active");
        mentor.setSpecialization(data.get("specialization"));
        mentor.setExpertise(data.get("expertise"));
        mentor.setBio(data.get("bio"));
        mentor.setQualifications(data.get("qualifications"));
        mentor.setPhoneNumber(data.get("phoneNumber"));
        
        // Parse yearsOfExperience - business logic moved from Servlet
        String yearsExpStr = data.get("yearsOfExperience");
        if (yearsExpStr != null && !yearsExpStr.isEmpty()) {
            try {
                int yearsExp = Integer.parseInt(yearsExpStr);
                mentor.setYearsOfExperience(yearsExp);
                logger.debug("[RegistrationService] Parsed years of experience: {}", yearsExp);
            } catch (NumberFormatException e) {
                logger.warn("[RegistrationService] Invalid years of experience: {}", yearsExpStr);
                throw new IllegalArgumentException("Years of experience must be a valid number");
            }
        }
        
        // Delegate to MentorBean for validation, persistence, events, audit trail
        mentorBean.add(mentor);
    }

    /**
     * Mentee-specific registration workflow
     */
    private void registerMenteeFlow(String username, String password, String email, Map<String, String> data) throws SQLException {
        logger.debug("[RegistrationService] Processing mentee registration");
        
        Mentee mentee = new Mentee();
        mentee.setUsername(username);
        mentee.setPassword(preparePassword(password));
        mentee.setEmail(email);
        mentee.setRole("mentee");
        mentee.setStatus("Active");
        mentee.setEducationLevel(data.get("educationLevel"));
        mentee.setFieldOfStudy(data.get("fieldOfStudy"));
        mentee.setLearningGoals(data.get("learningGoals"));
        mentee.setPhoneNumber(data.get("phoneNumber"));
        
        // Delegate to MenteeBean for validation, persistence, events, audit trail
        menteeBean.add(mentee);
    }

    /**
     * Extracts role-specific parameters from the form data
    */
    public Map<String, String> extractRoleSpecificData(String role, Map<String, String> formData) {
        logger.debug("[RegistrationService] Extracting role-specific data for role: {}", role);
        
        Map<String, String> roleData = new java.util.HashMap<>();
        
        if ("mentor".equalsIgnoreCase(role)) {
            roleData.put("specialization", formData.get("specialization"));
            roleData.put("expertise", formData.get("expertise"));
            roleData.put("yearsOfExperience", formData.get("yearsOfExperience"));
            roleData.put("bio", formData.get("bio"));
            roleData.put("qualifications", formData.get("qualifications"));
            roleData.put("phoneNumber", formData.get("phoneNumber"));
        } else if ("mentee".equalsIgnoreCase(role)) {
            roleData.put("educationLevel", formData.get("educationLevel"));
            roleData.put("fieldOfStudy", formData.get("fieldOfStudy"));
            roleData.put("learningGoals", formData.get("learningGoals"));
            roleData.put("phoneNumber", formData.get("phoneNumber"));
        }
        
        logger.debug("[RegistrationService] Extracted {} role-specific parameters", roleData.size());
        return roleData;
    }

    private String preparePassword(String password) {
        if (PasswordUtil.needsHashing(password)) {
            return PasswordUtil.hashPassword(password);
        }

        return password;
    }
}
