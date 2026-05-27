package app.soapapi;

import app.bean.UserBean;
import app.model.Mentee;
import app.model.Mentor;
import app.model.User;
import app.utility.logging.AppLogger;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.ejb.Stateless;
import org.slf4j.Logger;

import java.util.List;

@WebService(
        serviceName = "UserWebService",
        name = "UserWebServicePort",
        targetNamespace = "http://soapapi.app/",
        portName = "UserWebServicePort"
)
@Stateless
public class UserWebService {

    private static final Logger logger =
            AppLogger.getLogger(UserWebService.class);

    @Inject
    private UserBean userBean;

    // ── CREATE USER ───────────────────────────────────────────
    @WebMethod(operationName = "registerUser")
    @WebResult(name = "result")
    public UserSoapResponse registerUser(
            @WebParam(name = "username") String username,
            @WebParam(name = "email") String email,
            @WebParam(name = "password") String password,
            @WebParam(name = "role") String role,
            // Mentor fields
            @WebParam(name = "specialization") String specialization,
            @WebParam(name = "expertise") String expertise,
            @WebParam(name = "yearsOfExperience") Integer yearsOfExperience,
            @WebParam(name = "bio") String bio,
            @WebParam(name = "qualifications") String qualifications,
            @WebParam(name = "phoneNumber") String phoneNumber,
            // Mentee fields
            @WebParam(name = "educationLevel") String educationLevel,
            @WebParam(name = "fieldOfStudy") String fieldOfStudy,
            @WebParam(name = "learningGoals") String learningGoals,
            @WebParam(name = "mentorId") String mentorId) {

        try {
            logger.info("[SOAP] Registering user: {} with role: {}", username, role);

            User user;

            if ("mentor".equalsIgnoreCase(role)) {
                Mentor mentor = new Mentor();
                mentor.setSpecialization(specialization);
                mentor.setExpertise(expertise);
                mentor.setYearsOfExperience(yearsOfExperience);
                mentor.setBio(bio);
                mentor.setQualifications(qualifications);
                mentor.setPhoneNumber(phoneNumber);
                user = mentor;

            } else if ("mentee".equalsIgnoreCase(role)) {
                Mentee mentee = new Mentee();
                mentee.setEducationLevel(educationLevel);
                mentee.setFieldOfStudy(fieldOfStudy);
                mentee.setLearningGoals(learningGoals);
                mentee.setPhoneNumber(phoneNumber);
                mentee.setMentorId(mentorId);
                user = mentee;

            } else {
                return new UserSoapResponse(false, "Invalid role: " + role, null);
            }

            // Set base fields
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            userBean.registerUser(user);

            logger.info("[SOAP] User registered successfully, ID: {}", user.getId());
            return new UserSoapResponse(true, "User registered successfully", String.valueOf(user.getId()));

        } catch (Exception e) {
            logger.error("[SOAP] Error registering user", e);
            return new UserSoapResponse(false, "Error: " + e.getMessage(), null);
        }
    }

    // ── GET USER BY ID ────────────────────────────────────────
    @WebMethod(operationName = "getUserById")
    @WebResult(name = "user")
    public UserSoapDto getUserById(
            @WebParam(name = "userId") String userId) {

        try {
            logger.info("[SOAP] Fetching user by ID: {}", userId);

            User user = userBean.getUserById(userId);
            if (user == null) return null;

            return UserSoapDto.fromEntity(user);

        } catch (Exception e) {
            logger.error("[SOAP] Error fetching user by ID", e);
            return null;
        }
    }

    // ── GET USER BY USERNAME ──────────────────────────────────
    @WebMethod(operationName = "getUserByUsername")
    @WebResult(name = "user")
    public UserSoapDto getUserByUsername(
            @WebParam(name = "username") String username) {

        try {
            logger.info("[SOAP] Fetching user by username: {}", username);

            User user = userBean.getUserByUsername(username);
            if (user == null) return null;

            return UserSoapDto.fromEntity(user);

        } catch (Exception e) {
            logger.error("[SOAP] Error fetching user by username", e);
            return null;
        }
    }

    // ── GET ALL USERS ─────────────────────────────────────────
    @WebMethod(operationName = "getAllUsers")
    @WebResult(name = "users")
    public List<UserSoapDto> getAllUsers() {

        try {
            logger.info("[SOAP] Fetching all users");

            List<User> users = userBean.getAllUsers();

            return users.stream()
                    .map(UserSoapDto::fromEntity)
                    .toList();

        } catch (Exception e) {
            logger.error("[SOAP] Error fetching all users", e);
            return List.of();
        }
    }

    // ── UPDATE USER ───────────────────────────────────────────
    @WebMethod(operationName = "updateUser")
    @WebResult(name = "result")
    public UserSoapResponse updateUser(
            @WebParam(name = "userId") String userId,
            @WebParam(name = "username") String username,
            @WebParam(name = "email") String email,
            @WebParam(name = "password") String password,
            @WebParam(name = "status") String status,
            // Mentor fields
            @WebParam(name = "specialization") String specialization,
            @WebParam(name = "expertise") String expertise,
            @WebParam(name = "yearsOfExperience") Integer yearsOfExperience,
            @WebParam(name = "bio") String bio,
            @WebParam(name = "qualifications") String qualifications,
            @WebParam(name = "phoneNumber") String phoneNumber,
            // Mentee fields
            @WebParam(name = "educationLevel") String educationLevel,
            @WebParam(name = "fieldOfStudy") String fieldOfStudy,
            @WebParam(name = "learningGoals") String learningGoals,
            @WebParam(name = "mentorId") String mentorId) {

        try {
            logger.info("[SOAP] Updating user: {}", userId);

            // Fetch existing user to determine type
            User existingUser = userBean.getUserById(userId);
            if (existingUser == null) {
                return new UserSoapResponse(false, "User not found: " + userId, userId);
            }

            User user;

            if (existingUser instanceof Mentor) {
                Mentor mentor = new Mentor();
                mentor.setSpecialization(specialization);
                mentor.setExpertise(expertise);
                mentor.setYearsOfExperience(yearsOfExperience);
                mentor.setBio(bio);
                mentor.setQualifications(qualifications);
                mentor.setPhoneNumber(phoneNumber);
                user = mentor;

            } else if (existingUser instanceof Mentee) {
                Mentee mentee = new Mentee();
                mentee.setEducationLevel(educationLevel);
                mentee.setFieldOfStudy(fieldOfStudy);
                mentee.setLearningGoals(learningGoals);
                mentee.setPhoneNumber(phoneNumber);
                mentee.setMentorId(mentorId);
                user = mentee;

            } else {
                return new UserSoapResponse(false, "Unsupported user type", userId);
            }

            // Set base fields
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setStatus(status);

            userBean.updateUser(userId, user);

            logger.info("[SOAP] User updated successfully: {}", userId);
            return new UserSoapResponse(true, "User updated successfully", userId);

        } catch (Exception e) {
            logger.error("[SOAP] Error updating user", e);
            return new UserSoapResponse(false, "Error: " + e.getMessage(), userId);
        }
    }

    // ── DELETE USER ───────────────────────────────────────────
    @WebMethod(operationName = "deleteUser")
    @WebResult(name = "result")
    public UserSoapResponse deleteUser(
            @WebParam(name = "userId") String userId) {

        try {
            logger.info("[SOAP] Deleting user: {}", userId);

            userBean.deleteUser(userId);

            logger.info("[SOAP] User deleted successfully: {}", userId);
            return new UserSoapResponse(true, "User deleted successfully", userId);

        } catch (Exception e) {
            logger.error("[SOAP] Error deleting user", e);
            return new UserSoapResponse(false, "Error: " + e.getMessage(), userId);
        }
    }
}