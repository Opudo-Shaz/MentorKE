package app.utility.validation;

import app.model.Mentor;
import app.model.User;
import jakarta.enterprise.context.Dependent;

import java.util.regex.Pattern;

/**
 * MentorValidator - Validates Mentor entities
 *
 * Uses @Dependent scope since:
 * - Validator is stateless
 * - All validations are method-level
 * - Avoids Weld proxy issues
 */
@Dependent
@ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTOR)
public class MentorValidator implements Validator<Mentor> {

    private static final int MIN_SPECIALIZATION_LENGTH = 3;
    private static final int MAX_SPECIALIZATION_LENGTH = 150;

    private static final int MIN_YEARS_EXPERIENCE = 0;
    private static final int MAX_YEARS_EXPERIENCE = 80;

    private static final int MAX_BIO_LENGTH = 500;
    private static final int MAX_QUALIFICATIONS_LENGTH = 500;
    private static final int MAX_EXPERTISE_LENGTH = 1000;

    private static final String PHONE_PATTERN =
            "^[+]?[0-9\\-()\\s]{7,20}$";

    private static final Pattern PHONE_REGEX =
            Pattern.compile(PHONE_PATTERN);

    @Override
    public ValidationResult validate(Mentor mentor) {

        ValidationResult result = new ValidationResult("Mentor");

        if (mentor == null) {
            result.addError("Mentor object cannot be null");
            return result;
        }

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
            result.addError(
                    "Specialization must be at least "
                            + MIN_SPECIALIZATION_LENGTH
                            + " characters long"
            );
        }

        if (specialization.length() > MAX_SPECIALIZATION_LENGTH) {
            result.addError(
                    "Specialization cannot exceed "
                            + MAX_SPECIALIZATION_LENGTH
                            + " characters"
            );
        }

        // Supports tech names like C++, C#, UI/UX, Node.js, etc.
        if (!specialization.matches("^[a-zA-Z0-9\\s&,.#/+()\\-]+$")) {
            result.addError(
                    "Specialization contains invalid characters"
            );
        }
    }

    /**
     * Validate expertise field
     */
    private void validateExpertise(Mentor mentor, ValidationResult result) {

        String expertise = mentor.getExpertise();

        if (expertise == null || expertise.trim().isEmpty()) {
            result.addWarning(
                    "Expertise description is recommended"
            );
            return;
        }

        expertise = expertise.trim();

        if (expertise.length() < 10) {
            result.addWarning(
                    "Expertise description should be more detailed"
            );
        }

        if (expertise.length() > MAX_EXPERTISE_LENGTH) {
            result.addError(
                    "Expertise description cannot exceed "
                            + MAX_EXPERTISE_LENGTH
                            + " characters"
            );
        }
    }

    /**
     * Validate years of experience
     */
    private void validateYearsOfExperience(
            Mentor mentor,
            ValidationResult result
    ) {

        Integer yearsOfExperience = mentor.getYearsOfExperience();

        if (yearsOfExperience == null) {
            result.addError("Years of experience is required");
            return;
        }

        if (yearsOfExperience < MIN_YEARS_EXPERIENCE) {
            result.addError(
                    "Years of experience cannot be negative"
            );
        }

        if (yearsOfExperience > MAX_YEARS_EXPERIENCE) {
            result.addError(
                    "Years of experience cannot exceed "
                            + MAX_YEARS_EXPERIENCE
            );
        }

        if (yearsOfExperience == 0) {
            result.addWarning(
                    "Mentor with 0 years of experience may need review"
            );
        }
    }

    /**
     * Validate bio field
     */
    private void validateBio(Mentor mentor, ValidationResult result) {

        String bio = mentor.getBio();

        if (bio == null || bio.trim().isEmpty()) {
            result.addWarning(
                    "Bio is recommended for better mentee matching"
            );
            return;
        }

        bio = bio.trim();

        if (bio.length() < 20) {
            result.addWarning(
                    "Bio should be more descriptive"
            );
        }

        if (bio.length() > MAX_BIO_LENGTH) {
            result.addError(
                    "Bio cannot exceed "
                            + MAX_BIO_LENGTH
                            + " characters"
            );
        }
    }

    /**
     * Validate qualifications field
     */
    private void validateQualifications(
            Mentor mentor,
            ValidationResult result
    ) {

        String qualifications = mentor.getQualifications();

        if (qualifications == null || qualifications.trim().isEmpty()) {
            result.addWarning(
                    "Qualifications are recommended to build credibility"
            );
            return;
        }

        qualifications = qualifications.trim();

        if (qualifications.length() < 3) {
            result.addWarning(
                    "Qualifications should be more descriptive"
            );
        }

        if (qualifications.length() > MAX_QUALIFICATIONS_LENGTH) {
            result.addError(
                    "Qualifications cannot exceed "
                            + MAX_QUALIFICATIONS_LENGTH
                            + " characters"
            );
        }
    }

    /**
     * Validate phone number format
     */
    private void validatePhoneNumber(
            Mentor mentor,
            ValidationResult result
    ) {

        String phoneNumber = mentor.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            result.addWarning(
                    "Phone number is recommended for mentee contact"
            );
            return;
        }

        phoneNumber = phoneNumber.trim();

        if (!PHONE_REGEX.matcher(phoneNumber).matches()) {
            result.addError(
                    "Phone number format is invalid. " +
                            "Example: +254712345678 or 0712-345-678"
            );
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

        status = status.trim().toUpperCase();

        switch (status) {

            case "ACTIVE":
            case "INACTIVE":
            case "PENDING":
                break;

            default:
                result.addError(
                        "Status must be ACTIVE, INACTIVE, or PENDING"
                );
        }
    }
}