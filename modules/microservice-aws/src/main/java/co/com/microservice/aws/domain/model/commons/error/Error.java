package co.com.microservice.aws.domain.model.commons.error;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Error implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Data> errors;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    public static class Data implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String reason;
        private String domain;
        private String code;
        private String message;
    }
}