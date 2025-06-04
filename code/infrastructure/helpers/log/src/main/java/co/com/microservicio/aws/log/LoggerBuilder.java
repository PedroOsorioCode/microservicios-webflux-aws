package co.com.microservicio.aws.log;

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
public class LoggerBuilder  {
    private String appName;
    private ObjectMapper objectMapper;

    public LoggerBuilder(@Value("${spring.application.name}") String appName, ObjectMapper objectMapper) {
        this.appName = appName;
        this.objectMapper = objectMapper;
    }

    public void info(TransactionLog.Request rq, TransactionLog.Response rs,
                 String message, String messageId, String service, String method) {
        log.info(new ObjectMessage(buildObject(rq, rs, buildDataLog(message, messageId, service, method))));
    }

    public void info(String message, String messageId, String service, String method) {
        log.info(new ObjectMessage(buildObject(new TransactionLog.Request(), new TransactionLog.Response(),
            buildDataLog(message, messageId, service, method))));
    }

    public void error(String message, String messageId, String service, String method) {
        log.error(new ObjectMessage(buildObject(new TransactionLog.Request(), new TransactionLog.Response(),
                buildDataLog(message, messageId, service, method))));
    }

    private TransactionLog.DataLog buildDataLog(String message, String messageId, String service, String method){
        return new TransactionLog.DataLog(message, messageId, service, method, appName);
    }

    private String buildObject(TransactionLog.Request rq, TransactionLog.Response rs,
                   TransactionLog.DataLog dataLog) {
        var logObject = new TransactionLog(dataLog, rq, rs);
        try {
            return objectMapper.writeValueAsString(logObject);
        } catch (JsonProcessingException e) {
            return logObject.toString();
        }
    }
}
