package app.utility.validation;

import app.model.Mentee;
import jakarta.enterprise.context.Dependent;
import java.util.regex.Pattern;


@Dependent
@ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
public class MenteeValidator implements Validator<Mentee> {

    private static final int MIN_FIELD_LENGTH = 2;
    private static final int MAX_FIELD_LENGTH = 150;
    private static final String PHONE_PATTERN = "^[0-9+\\-() ]{7,20}$";
    private static final Pattern PHONE_REGEX = Pattern.compile(PHONE_PATTERN);
    private static final String[] VALID_EDUCATION_LEVELS = {
            "Primary", "Secondary", "High School", "Certificate",
            "Diploma", "Undergraduate", "Postgraduate", "PhD", "Other"
    };

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


     // Validate user ID reference
    private void validateUserId(Mentee mentee, ValidationResult result) {
        if (mentee.getUserId() == null || mentee.getUserId().trim().isEmpty()) {
            result.addError("User ID is required for mentee profile");
        }

        try {
            Integer.parseInt(mentee.getUserId());
        } catch (NumberFormatException e) {
            result.addError("User ID must be a valid integer");
        }
    }


     // Validate education level

    private void validateEducationLevel(Mentee mentee, ValidationResult result) {
        String educationLevel = mentee.getEducationLevel();

        if (educationLevel == null || educationLevel.trim().isEmpty()) {
            result.addError("Education level is required");
            return;
        }

        educationLevel = educationLevel.trim();

        boolean validLevel = false;
        for (String level : VALID_EDUCATION_LEVELS) {
            if (level.equalsIgnoreCase(educationLevel)) {
                validLevel = true;
                break;
            }
        }

        if (!validLevel) {
            result.addError("Invalid education level. Valid options: " + String.join(", ", VALID_EDUCATION_LEVELS));
        }
    }

     // Validate field of study

    private void validateFieldOfStudy(Mentee mentee, ValidationResult result) {
        String fieldOfStudy = mentee.getFieldOfStudy();

        if (fieldOfStudy == null || fieldOfStudy.trim().isEmpty()) {
            result.addError("Field of study is required");
            return;
        }

        fieldOfStudy = fieldOfStudy.trim();

        if (fieldOfStudy.length() < MIN_FIELD_LENGTH) {
            result.addError("Field of study must be at least " + MIN_FIELD_LENGTH + " characters long");
        }

        if (fieldOfStudy.length() > MAX_FIELD_LENGTH) {
            result.addError("Field of study cannot exceed " + MAX_FIELD_LENGTH + " characters");
        }

        if (!fieldOfStudy.matches("^[a-zA-Z\\s&,.-]+$")) {
            result.addError("Field of study can only contain letters, spaces, and special characters (& , . -)");
        }
    }


     // Validate learning goals

    private void validateLearningGoals(Mentee mentee, ValidationResult result) {
        String learningGoals = mentee.getLearningGoals();

        if (learningGoals == null || learningGoals.trim().isEmpty()) {
            result.addError("Learning goals are required");
            return;
        }

        learningGoals = learningGoals.trim();

        if (learningGoals.length() < 10) {
            result.addError("Learning goals should be descriptive (at least 10 characters)");
        }

        if (learningGoals.length() > 1000) {
            result.addError("Learning goals cannot exceed 1000 characters");
        }
    }


     // Validate phone number format

    private void validatePhoneNumber(Mentee mentee, ValidationResult result) {
        String phoneNumber = mentee.getPhoneNumber();

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(phoneNumber).matches()) {
                result.addError("Phone number format is invalid. Use format like: +254712345678 or 0712-345-678");
            }
        } else {
            result.addWarning("Phone number is recommended for mentor contact");
        }
    }

     // Validate mentor ID if assigned

    private void validateMentorId(Mentee mentee, ValidationResult result) {
        String mentorId = mentee.getMentorId();

        if (mentorId != null && !mentorId.trim().isEmpty()) {
            try {
                Integer.parseInt(mentorId);
            } catch (NumberFormatException e) {
                result.addError("Mentor ID must be a valid integer");
            }
        }
        // mentorId can be null initially (mentor assignment happens later)
    }


     // Validate status field

    private void validateStatus(Mentee mentee, ValidationResult result) {
        String status = mentee.getStatus();

        if (status == null || status.trim().isEmpty()) {
            result.addError("Status is required");
            return;
        }

        status = status.trim().toLowerCase();

        if (!status.equals("active") && !status.equals("inactive") && !status.equals("pending") && !status.equals("assigned")) {
            result.addError("Status must be one of: Active, Inactive, Pending, Assigned");
        }
    }
}

