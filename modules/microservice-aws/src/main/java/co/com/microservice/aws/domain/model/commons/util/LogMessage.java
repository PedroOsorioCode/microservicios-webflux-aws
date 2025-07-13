package co.com.microservice.aws.domain.model.commons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMessage {
    public static final String MESSAGE_SERVICE = "Service Api Rest world regions";
    public static final String METHOD_LISTCOUNTRIES = "List all records";
    public static final String METHOD_FINDONE = "Find one record";
    public static final String METHOD_SAVE = "Save one record";
    public static final String METHOD_UPDATE = "Update one record";
    public static final String METHOD_DELETE = "Delete one record";
}
