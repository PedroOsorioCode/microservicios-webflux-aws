package co.com.microservice.aws.domain.model.commons.error;

import co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage;
import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import lombok.experimental.UtilityClass;

import java.util.List;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_SERVER_ERROR;

@UtilityClass
public class ErrorFactory {

    public Error buildError(TechnicalExceptionMessage technicalExceptionEnum, String reason, String domain) {
        return Error.builder()
                .errors(List.of(Error.Data.builder().reason(reason).domain(domain)
                        .code(technicalExceptionEnum.getCode()).message(technicalExceptionEnum.getMessage()).build()))
                .build();
    }

    public Error buildErrorBusiness(BusinessExceptionMessage businessErrorMessage, String reason, String domain) {
        return Error
                .builder().errors(List.of(Error.Data.builder().reason(reason).domain(domain)
                        .code(businessErrorMessage.getCode()).message(businessErrorMessage.getTypeMessage()).build()))
                .build();
    }

    public Error fromTechnical(TechnicalException technicalException, String domain) {
        if (technicalException.getReason() == null || technicalException.getReason().isEmpty()) {
            return buildError(technicalException.getTypeTechnicalException(), technicalException.getMessage(), domain);
        } else {
            return buildErrorWithReason(technicalException, domain);
        }
    }

    public Error buildErrorWithReason(TechnicalException technicalException, String domain) {
        return buildError(technicalException.getTypeTechnicalException(), technicalException.getReason(), domain);
    }

    public Error fromBusiness(BusinessException businessException, String domain) {
        if (businessException.getTypeBusinessException() == BusinessExceptionMessage.BUSINESS_ERROR) {
            return businessException.getError();
        }
        return buildErrorBusiness(businessException.getTypeBusinessException(),
                businessException.getTypeBusinessException().getMessage(), domain);
    }

    public Error fromDefaultTechnical(String reason, String domain) {
        return buildError(TECHNICAL_SERVER_ERROR, reason, domain);
    }
}