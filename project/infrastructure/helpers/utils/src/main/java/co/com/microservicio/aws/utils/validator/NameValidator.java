package co.com.microservicio.aws.utils.validator;

import co.com.microservicio.aws.utils.validator.gateways.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class NameValidator implements ConstraintValidator<ValidName, String> {
    private String namePattern;

    public NameValidator(@Value("${entries.regex-body-wr.name}") String namePattern) {
        this.namePattern = namePattern;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(namePattern);
    }
}