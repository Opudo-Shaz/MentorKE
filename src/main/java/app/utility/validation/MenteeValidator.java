package app.utility.validation;

import app.model.Mentee;
import jakarta.enterprise.context.Dependent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Dependent
@ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
public class MenteeValidator implements Validator<Mentee> {

    private static final int MIN_FIELD_LENGTH = 2;
    private static final int MAX_FIELD_LENGTH = 150;
    private static final int MIN_GOALS_LENGTH = 10;
    private static final int MAX_GOALS_LENGTH = 1000;

    private static final String PHONE_PATTERN = "^[0-9+\\-() ]{7,20}$";
    private static final Pattern PHONE_REGEX = Pattern.compile(PHONE_PATTERN);

    private static final Pattern FIELD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\s&,.'\\-]+$");

    private static final Set<String> VALID_EDUCATION_LEVELS = new HashSet<>(
            Arrays.asList(
                    "primary",
                    "secondary",
                    "high school",
                    "certificate",
                    "diploma",
                    "undergraduate",
                    "postgraduate",
                    "phd",
                    "other"
            )
    );

    private static final Set<String> VALID_STATUSES = new HashSet<>(
            Arrays.asList(
                    "active",
                    "inactive",
                    "pending",
                    "assigned"
            )
    );

    @Override
    public ValidationResult validate(Mentee mentee) {

        ValidationResult result = new ValidationResult("Mentee");

        if (mentee == null) {
            result.addError("Mentee object cannot be null");
            return result;
        }

        validateUserId(mentee, result);
        validateEducationLevel(mentee, result);
        validateFieldOfStudy(mentee, result);
        validateLearningGoals(mentee, result);
        validatePhoneNumber(mentee, result);
        validateMentorId(mentee, result);
        validateStatus(mentee, result);

        return result;
    }

    @Override
    public ValidatorQualifier.ValidationChoice getValidationChoice() {
        return ValidatorQualifier.ValidationChoice.MENTEE;
    }

    /**
     * Validate user ID reference
     */
    private void validateUserId(Mentee mentee, ValidationResult result) {

        String userId = safeTrim(mentee.getUserId());

        if (userId == null) {
            result.addError("User ID is required");
            return;
        }

        // User ID is a string reference to the users table
        if (userId.length() > 50) {
            result.addError("User ID exceeds maximum length");
        }
    }

    /**
     * Validate education level
     */
    private void validateEducationLevel(Mentee mentee, ValidationResult result) {

        String educationLevel = safeTrim(mentee.getEducationLevel());

        if (educationLevel == null) {
            result.addError("Education level is required");
            return;
        }

        if (!VALID_EDUCATION_LEVELS.contains(educationLevel.toLowerCase())) {
            result.addError(
                    "Invalid education level. Valid options are: " +
                            "Primary, Secondary, High School, Certificate, Diploma, " +
                            "Undergraduate, Postgraduate, PhD, Other"
            );
        }
    }

    /**
     * Validate field of study
     */
    private void validateFieldOfStudy(Mentee mentee, ValidationResult result) {

        String fieldOfStudy = safeTrim(mentee.getFieldOfStudy());

        if (fieldOfStudy == null) {
            result.addError("Field of study is required");
            return;
        }

        if (fieldOfStudy.length() < MIN_FIELD_LENGTH) {
            result.addError(
                    "Field of study must be at least " +
                            MIN_FIELD_LENGTH +
                            " characters long"
            );
        }

        if (fieldOfStudy.length() > MAX_FIELD_LENGTH) {
            result.addError(
                    "Field of study cannot exceed " +
                            MAX_FIELD_LENGTH +
                            " characters"
            );
        }

        if (!FIELD_PATTERN.matcher(fieldOfStudy).matches()) {
            result.addError(
                    "Field of study contains invalid characters"
            );
        }
    }

    /**
     * Validate learning goals
     */
    private void validateLearningGoals(Mentee mentee, ValidationResult result) {

        String learningGoals = safeTrim(mentee.getLearningGoals());

        if (learningGoals == null) {
            result.addError("Learning goals are required");
            return;
        }

        if (learningGoals.length() < MIN_GOALS_LENGTH) {
            result.addError(
                    "Learning goals should be at least " +
                            MIN_GOALS_LENGTH +
                            " characters"
            );
        }

        if (learningGoals.length() > MAX_GOALS_LENGTH) {
            result.addError(
                    "Learning goals cannot exceed " +
                            MAX_GOALS_LENGTH +
                            " characters"
            );
        }
    }

    /**
     * Validate phone number
     */
    private void validatePhoneNumber(Mentee mentee, ValidationResult result) {

        String phoneNumber = safeTrim(mentee.getPhoneNumber());

        if (phoneNumber == null) {
            result.addWarning(
                    "Phone number is recommended for mentor communication"
            );
            return;
        }

        if (!PHONE_REGEX.matcher(phoneNumber).matches()) {
            result.addError(
                    "Phone number format is invalid. Example: +254712345678"
            );
        }
    }

    /**
     * Validate mentor ID if assigned
     */
    private void validateMentorId(Mentee mentee, ValidationResult result) {

        String mentorId = safeTrim(mentee.getMentorId());

        if (mentorId == null) {
            mentee.setMentorId(null);
            return;
        }

        // Mentor ID is a string reference to the mentors table
        if (mentorId.length() > 50) {
            result.addError("Mentor ID exceeds maximum length");
        }
    }

    /**
     * Validate status
     */
    private void validateStatus(Mentee mentee, ValidationResult result) {

        String status = safeTrim(mentee.getStatus());

        if (status == null) {
            result.addError("Status is required");
            return;
        }

        if (!VALID_STATUSES.contains(status.toLowerCase())) {
            result.addError(
                    "Status must be one of: Active, Inactive, Pending, Assigned"
            );
        }
    }

    /**
     * Utility method for trimming strings safely
     */
    private String safeTrim(String value) {

        if (value == null) {
            return null;
        }

        value = value.trim();

        return value.isEmpty() ? null : value;
    }
}