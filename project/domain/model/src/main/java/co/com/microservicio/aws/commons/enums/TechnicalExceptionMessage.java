package co.com.microservicio.aws.commons.enums;

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
    TECHNICAL_EVENT_EXCEPTION("WRT04", "An error has occurred sending event"),
    TECHNICAL_SECRET_EXCEPTION("WRT05", "An error occurred while trying to get AWS secrets"),
    TECHNICAL_REQUEST_ERROR("WRT06", "There is an error in the request body"),
    TECHNICAL_GENERATE_FILE_ERROR("WRT08", "An error occurred transforming the messaging to generate the file"),
    TECHNICAL_S3_EXCEPTION("WRT07", "An error occurred while trying to get S3 object"),
    TECHNICAL_S3_PUT_OBJECT_FAIL("WRT09", "An error has occurred upload an object in S3"),
    TECHNICAL_EXCEPTION_REPOSITORY("WRT10", "An error has occurred in the repository");

    private final String code;
    private final String message;

    public String getDescription() {
        return String.join(" - ", this.getCode(), this.getMessage());
    }

}
