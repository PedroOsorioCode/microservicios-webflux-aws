package co.com.microservicio.aws.utils.validator;

import co.com.microservicio.aws.utils.validator.gateways.ValidCodeRegion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class CodeRegionValidator implements ConstraintValidator<ValidCodeRegion, String> {
    private String codeRegionPattern;

    public CodeRegionValidator(@Value("${entries.regex-body-wr.code-region}") String codeRegionPattern) {
        this.codeRegionPattern = codeRegionPattern;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(codeRegionPattern);
    }
}