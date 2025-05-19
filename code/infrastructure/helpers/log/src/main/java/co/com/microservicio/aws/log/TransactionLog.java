package co.com.microservicio.aws.log;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {
    @Serial
    private static final long serialVersionUID = 1L;

    private DataLog dataLog;
    private Request request;
    private Response response;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataLog implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String message;
        private String messageId;
        private String service;
        private String method;
        private String appName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Request implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Map<String, String> headers;
        private transient Object body;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Map<String, String> headers;
        private transient Object body;
    }
}
