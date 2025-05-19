package co.com.microservicio.aws.log;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Getter
@Component
public class LoggerBuilder  {
    private String appName;

    public LoggerBuilder(@Value("${spring.application.name}") String appName) {
        this.appName = appName;
    }

    public void info(TransactionLog.Request rq, TransactionLog.Response rs,
                 String message, String messageId, String service, String method) {
        log.info(buildObject(rq, rs, buildDataLog(message, messageId, service, method)));
    }

    public void info(String message, String messageId, String service, String method) {
        log.info(buildObject(new TransactionLog.Request(), new TransactionLog.Response(),
            buildDataLog(message, messageId, service, method)));
    }

    public void error(String message, String messageId, String service, String method) {
        log.error(buildObject(new TransactionLog.Request(), new TransactionLog.Response(),
                buildDataLog(message, messageId, service, method)));
    }

    private TransactionLog.DataLog buildDataLog(String message, String messageId, String service, String method){
        return new TransactionLog.DataLog(message, messageId, service, method, appName);
    }

    private TransactionLog buildObject(TransactionLog.Request rq, TransactionLog.Response rs,
                   TransactionLog.DataLog dataLog) {
        return new TransactionLog(dataLog, rq, rs);
    }
}
