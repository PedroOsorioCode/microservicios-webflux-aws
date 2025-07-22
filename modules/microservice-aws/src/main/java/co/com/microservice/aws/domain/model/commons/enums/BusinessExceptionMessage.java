package co.com.microservice.aws.domain.model.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum BusinessExceptionMessage {
    BUSINESS_ERROR("WRB01", "Error in a service", "Server error"),
    BUSINESS_USERNAME_REQUIRED("WRB02", "The attribute 'user-name' is required", "There is an error in the request body"),
    BUSINESS_RECORD_NOT_FOUND("WRB03", "The record not found", "Bussiness error"),
    BUSINESS_COUNTRY_NOT_EXIST("WRB04", "The country not exist", "Bussiness error"),
    BUSINESS_OTRO_MENSAJE("WRB03", "Others message business", "Other");

    private final String code;
    private final String message;
    private final String typeMessage;

    public String getDescription() {
        return String.join(" - ", this.getCode(), this.getMessage());
    }
}