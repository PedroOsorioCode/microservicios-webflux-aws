package co.com.microservice.aws.application.helpers.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Getter
@Component
public class LoggerBuilder {
    private final String appName;
    private final ObjectMapper objectMapper;

    public LoggerBuilder(@Value("${spring.application.name}") String appName, ObjectMapper objectMapper) {
        this.appName = appName;
        this.objectMapper = objectMapper;
    }

    public void info(TransactionLog.Request rq, String message, String messageId, String service, String method) {
        log.info(new ObjectMessage(buildObjectReq(rq, buildDataLog(message, messageId, service, method))));
    }

    public void info(TransactionLog.Response rs, String message, String messageId, String service, String method) {
        log.info(new ObjectMessage(buildObjectRes(rs, buildDataLog(message, messageId, service, method))));
    }

    public void info(String message, String messageId, String service, String method) {
        log.info(new ObjectMessage(buildObjectApp(buildDataLog(message, messageId, service, method))));
    }

    public void error(Throwable throwable) {
        log.error("Unexpected error occurred:", throwable);
    }

    public void info(String message) {
        log.info(message);
    }

    private TransactionLog.Application buildDataLog(String message, String messageId, String service, String method){
        return new TransactionLog.Application(message, messageId, service, method, appName);
    }

    private String buildObjectReq(TransactionLog.Request rq, TransactionLog.Application data) {
        return buildObject(TransactionLog.builder().app(data).request(rq).build());
    }

    private String buildObjectRes(TransactionLog.Response rs, TransactionLog.Application data) {
        return buildObject(TransactionLog.builder().app(data).response(rs).build());
    }

    private String buildObjectApp(TransactionLog.Application data) {
        return buildObject(TransactionLog.builder().app(data).build());
    }

    private String buildObject(TransactionLog logObject) {
        try {
            return objectMapper.writeValueAsString(logObject);
        } catch (JsonProcessingException e) {
            return logObject.toString();
        }
    }
}