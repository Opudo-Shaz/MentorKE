package app.utility.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationResult {
    private boolean valid;
    private final List<String> errorMessages;
    private final List<String> warningMessages;
    private final String validatedEntity;
    private final long validationTime;

    public ValidationResult(String validatedEntity) {
        this.valid = true;
        this.validatedEntity = validatedEntity;
        this.errorMessages = new ArrayList<>();
        this.warningMessages = new ArrayList<>();
        this.validationTime = System.currentTimeMillis();
    }


    public void addError(String error) {
        Objects.requireNonNull(error, "Error message cannot be null");
        this.errorMessages.add(error);
        this.valid = false;
    }

    public void addErrors(List<String> errors) {
        Objects.requireNonNull(errors, "Errors list cannot be null");
        this.errorMessages.addAll(errors);
        if (!errors.isEmpty()) {
            this.valid = false;
        }
    }

    public void addWarning(String warning) {
        Objects.requireNonNull(warning, "Warning message cannot be null");
        this.warningMessages.add(warning);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages);
    }

    public List<String> getWarningMessages() {
        return new ArrayList<>(warningMessages);
    }

    public String getValidatedEntity() {
        return validatedEntity;
    }

    public long getValidationTime() {
        return validationTime;
    }

    public int getErrorCount() {
        return errorMessages.size();
    }

    public int getWarningCount() {
        return warningMessages.size();
    }

    public String getFirstError() {
        return errorMessages.isEmpty() ? null : errorMessages.get(0);
    }

    public String getAllErrorsAsString() {
        return String.join(", ", errorMessages);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", validatedEntity='" + validatedEntity + '\'' +
                ", errorCount=" + errorMessages.size() +
                ", warningCount=" + warningMessages.size() +
                ", errorMessages=" + errorMessages +
                ", warningMessages=" + warningMessages +
                '}';
    }
}

