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
    TECHNICAL_EXCEPTION_REPOSITORY("WRT05", "An error has occurred in the repository"),
    TECHNICAL_GETTING_S3_OBJECT_FAILED("WRT06", "Error obteniendo objeto de S3"),
    ZIP_FILE_IS_WRONG("WRT07", "Error, the file ZIP is incorrect"),
    ZIP_FILE_HASNT_ONLY_ONE_FILE("WRT08", "Error, the file ZIP has more one files"),
    FILE_ISNT_TXT("WRT09", "Error, the file is not flat text"),
    TXT_FILE_HAS_INVALID_CHARS("PAT0017", "Error, the file TXT has invalid characters");

    private final String code;
    private final String message;

    public String getDescription() {
        return String.join(" - ", this.getCode(), this.getMessage());
    }
}