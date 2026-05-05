package app.utility.validation;


public interface Validator<T> {
    ValidationResult validate(T entity);

    ValidatorQualifier.ValidationChoice getValidationChoice();
}

