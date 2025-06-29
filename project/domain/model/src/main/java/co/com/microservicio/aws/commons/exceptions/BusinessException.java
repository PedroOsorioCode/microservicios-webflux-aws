package co.com.microservicio.aws.commons.exceptions;

import java.io.Serial;
import co.com.microservicio.aws.commons.error.Error;
import co.com.microservicio.aws.commons.enums.BusinessExceptionMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Error error;
    private final BusinessExceptionMessage typeBusinessException;

    public BusinessException(Error error) {
        super(error.getErrors().get(0).getMessage());
        this.error = error;
        this.typeBusinessException = BusinessExceptionMessage.BUSINESS_ERROR;
    }

    public BusinessException(Throwable error, BusinessExceptionMessage typeBusinessException) {
        super(typeBusinessException.getDescription(), error);
        this.error = Error.builder().build();
        this.typeBusinessException = typeBusinessException;
    }

    public BusinessException(BusinessExceptionMessage typeBusinessException) {
        super(typeBusinessException.getDescription());
        this.error = Error.builder().build();
        this.typeBusinessException = typeBusinessException;
    }
}
