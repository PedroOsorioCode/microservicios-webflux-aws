package co.com.microservice.aws.domain.model.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum TechnicalExceptionMessage {

    TECHNICAL_SERVER_ERROR("WRT01", "Internal server error"),
    TECHNICAL_REST_CLIENT_ERROR("WRT02", "An error has occurred in the Rest Client"),
    TECHNICAL_HEADER_MISSING("WRT03", "Missing parameters per header"),
    TECHNICAL_REQUEST_ERROR("WRT04", "There is an error in the request body"),
    TECHNICAL_EXCEPTION_REPOSITORY("WRT05", "An error has occurred in the repository");

    private final String code;
    private final String message;

    public String getDescription() {
        return String.join(" - ", this.getCode(), this.getMessage());
    }
}