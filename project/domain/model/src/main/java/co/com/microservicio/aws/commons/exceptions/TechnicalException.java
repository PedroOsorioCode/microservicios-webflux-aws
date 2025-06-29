package co.com.microservicio.aws.commons.exceptions;

import java.io.Serial;
import co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final TechnicalExceptionMessage typeTechnicalException;
    private final String reason;

    public TechnicalException(Throwable error, TechnicalExceptionMessage technicalExceptionMessage) {
        super(technicalExceptionMessage.getDescription(), error);
        this.typeTechnicalException = technicalExceptionMessage;
        this.reason = technicalExceptionMessage.getDescription();
    }

    public TechnicalException(TechnicalExceptionMessage technicalExceptionEnum) {
        super(technicalExceptionEnum.getMessage());
        this.typeTechnicalException = technicalExceptionEnum;
        this.reason = technicalExceptionEnum.getDescription();
    }

    public TechnicalException(String reason, TechnicalExceptionMessage errorMessage) {
        super(errorMessage.getMessage());
        this.reason = reason;
        this.typeTechnicalException = errorMessage;
    }
}