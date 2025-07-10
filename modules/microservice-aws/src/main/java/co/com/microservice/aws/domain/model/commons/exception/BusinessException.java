package co.com.microservice.aws.domain.model.commons.exception;

import co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage;
import co.com.microservice.aws.domain.model.commons.error.Error;
import lombok.Getter;

import java.io.Serial;

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