package co.com.microservice.aws.domain.model.commons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseMessageConstant {
    public static final String MSG_LIST_SUCCESS = "Listed successfull!";
    public static final String MSG_SAVED_SUCCESS = "Saved successfull!";
    public static final String MSG_UPDATED_SUCCESS = "Updated successfull!";
    public static final String MSG_DELETED_SUCCESS = "Deleted successfull!";
}