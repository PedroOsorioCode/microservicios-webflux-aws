package co.com.microservicio.aws.api.commons;

import co.com.microservicio.aws.commons.exceptions.TechnicalException;
import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.log.TransactionLog;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

import static co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;
import static co.com.microservicio.aws.model.worldregion.util.LogMessage.MESSAGE_SERVICE;

@Component
@RequiredArgsConstructor
public class RequestUtil {
    private static final String NAME_CLASS = RequestUtil.class.getName();
    private static final String MSG_VALID_REQUEST_DATA = "Validation with errors";
    private final LoggerBuilder logger;
    private final Validator validator;

    public <T> Mono<T> checkBodyRequest(T rq, String transactionId) {
        return Mono.defer(() -> validateDto(rq, transactionId)).thenReturn(rq);
    }

    private <T> Mono<Void> validateDto(T rq, String transactionId) {
        var message = "";

        Set<ConstraintViolation<T>> violations = validator.validate(rq);
        if (!violations.isEmpty()) {
            logger.info(TransactionLog.Request.builder().body(rq).build(),
                    TransactionLog.Response.builder().build(),
                    MSG_VALID_REQUEST_DATA, transactionId, MESSAGE_SERVICE, NAME_CLASS);

            message = getMessageValidationErrors(violations);
            logger.error(message, transactionId, MESSAGE_SERVICE, NAME_CLASS);
        }

        return message.isEmpty() ? Mono.empty() : Mono.error(new TechnicalException(message, TECHNICAL_REQUEST_ERROR));
    }

    private static String getMessageValidationErrors(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(cv -> "[**" + cv.getPropertyPath() + "**: " + cv.getMessage() + "]")
                .collect(Collectors.joining(" - "));
    }

}
