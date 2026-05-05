package app.utility.validation;

import app.model.Mentor;
import jakarta.enterprise.context.Dependent;
import java.util.regex.Pattern;

/**
 * MentorValidator - Validates Mentor entities
 *
 * Uses @Dependent scope since:
 * - Validator is stateless (no shared state)
 * - All validations are method-level
 * - Avoids Weld WELD-001435 proxy issues
 */
@Dependent
@ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTOR)
public class MentorValidator implements Validator<Mentor> {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_SPECIALIZATION_LENGTH = 3;
    private static final int MAX_SPECIALIZATION_LENGTH = 150;
    private static final int MIN_YEARS_EXPERIENCE = 0;
    private static final int MAX_YEARS_EXPERIENCE = 80;
    private static final String PHONE_PATTERN = "^[0-9+\\-() ]{7,20}$";
    private static final Pattern PHONE_REGEX = Pattern.compile(PHONE_PATTERN);

    @Override
    public ValidationResult validate(Mentor mentor) {
        ValidationResult result = new ValidationResult("Mentor");

        if (mentor == null) {
            result.addError("Mentor object cannot be null");
            return result;
        }

        validateUserId(mentor, result);
        validateSpecialization(mentor, result);
        validateExpertise(mentor, result);
        validateYearsOfExperience(mentor, result);
        validateBio(mentor, result);
        validateQualifications(mentor, result);
        validatePhoneNumber(mentor, result);
        validateStatus(mentor, result);

        return result;
    }

    @Override
    public ValidatorQualifier.ValidationChoice getValidationChoice() {
        return ValidatorQualifier.ValidationChoice.MENTOR;
    }


    // Validate user ID reference

    private void validateUserId(Mentor mentor, ValidationResult result) {
        if (mentor.getUserId() == null || mentor.getUserId().trim().isEmpty()) {
            result.addError("User ID is required for mentor profile");
        }

        try {
            Integer.parseInt(mentor.getUserId());
        } catch (NumberFormatException e) {
            result.addError("User ID must be a valid integer");
        }
    }

    /**
     * Validate specialization field
     */
    private void validateSpecialization(Mentor mentor, ValidationResult result) {
        String specialization = mentor.getSpecialization();

        if (specialization == null || specialization.trim().isEmpty()) {
            result.addError("Specialization is required");
            return;
        }

        specialization = specialization.trim();

        if (specialization.length() < MIN_SPECIALIZATION_LENGTH) {
            result.addError("Specialization must be at least " + MIN_SPECIALIZATION_LENGTH + " characters long");
        }

        if (specialization.length() > MAX_SPECIALIZATION_LENGTH) {
            result.addError("Specialization cannot exceed " + MAX_SPECIALIZATION_LENGTH + " characters");
        }

        if (!specialization.matches("^[a-zA-Z\\s&,.-]+$")) {
            result.addError("Specialization can only contain letters, spaces, and special characters (& , . -)");
        }
    }

    /**
     * Validate expertise field
     */
    private void validateExpertise(Mentor mentor, ValidationResult result) {
        String expertise = mentor.getExpertise();

        if (expertise != null && !expertise.trim().isEmpty()) {
            if (expertise.length() < 10) {
                result.addWarning("Expertise description should be more detailed (at least 10 characters)");
            }

            if (expertise.length() > 1000) {
                result.addError("Expertise description cannot exceed 1000 characters");
            }
        }
    }

    /**
     * Validate years of experience
     */
    private void validateYearsOfExperience(Mentor mentor, ValidationResult result) {
        Integer yearsOfExperience = mentor.getYearsOfExperience();

        if (yearsOfExperience == null) {
            result.addError("Years of experience is required");
            return;
        }

        if (yearsOfExperience < MIN_YEARS_EXPERIENCE) {
            result.addError("Years of experience cannot be negative");
        }

        if (yearsOfExperience > MAX_YEARS_EXPERIENCE) {
            result.addError("Years of experience cannot exceed " + MAX_YEARS_EXPERIENCE);
        }

        if (yearsOfExperience == 0) {
            result.addWarning("Mentor with 0 years of experience may need additional review");
        }
    }

    /**
     * Validate bio field
     */
    private void validateBio(Mentor mentor, ValidationResult result) {
        String bio = mentor.getBio();

        if (bio == null || bio.trim().isEmpty()) {
            result.addWarning("Bio is recommended for better mentee matching");
        } else {
            if (bio.length() < 20) {
                result.addWarning("Bio should be more descriptive (at least 20 characters)");
            }

            if (bio.length() > 500) {
                result.addError("Bio cannot exceed 500 characters");
            }
        }
    }

    /**
     * Validate qualifications field
     */
    private void validateQualifications(Mentor mentor, ValidationResult result) {
        String qualifications = mentor.getQualifications();

        if (qualifications == null || qualifications.trim().isEmpty()) {
            result.addWarning("Qualifications are recommended to build credibility");
        } else {
            if (qualifications.length() < 5) {
                result.addError("Qualifications should be more specific");
            }

            if (qualifications.length() > 500) {
                result.addError("Qualifications cannot exceed 500 characters");
            }
        }
    }

    /**
     * Validate phone number format
     */
    private void validatePhoneNumber(Mentor mentor, ValidationResult result) {
        String phoneNumber = mentor.getPhoneNumber();

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(phoneNumber).matches()) {
                result.addError("Phone number format is invalid. Use format like: +254712345678 or 0712-345-678");
            }
        } else {
            result.addWarning("Phone number is recommended for mentee contact");
        }
    }

    /**
     * Validate status field
     */
    private void validateStatus(Mentor mentor, ValidationResult result) {
        String status = mentor.getStatus();

        if (status == null || status.trim().isEmpty()) {
            result.addError("Status is required");
            return;
        }

        status = status.trim().toLowerCase();

        if (!status.equals("active") && !status.equals("inactive") && !status.equals("pending")) {
            result.addError("Status must be one of: Active, Inactive, Pending");
        }
    }
}

