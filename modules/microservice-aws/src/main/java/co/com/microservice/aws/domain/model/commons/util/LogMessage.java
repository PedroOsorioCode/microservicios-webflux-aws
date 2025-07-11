package co.com.microservice.aws.domain.model.commons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMessage {
    public static final String MESSAGE_SERVICE = "Service Api Rest world regions";
    public static final String METHOD_LISTCOUNTRIES = "List all by region";
    public static final String METHOD_FINDONE = "Find one world region";
    public static final String METHOD_SAVE = "Save one world region";
    public static final String METHOD_UPDATE = "Update one world region";
    public static final String METHOD_DELETE = "Delete one world region";
}
