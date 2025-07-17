package co.com.microservice.aws.infrastructure.input.backgroudtask;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
import co.com.microservice.aws.domain.usecase.out.RedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
@EnableAutoConfiguration
@RequiredArgsConstructor
public class BackgroundTasks {
    private static final String FLAG_PROCESS_YES = "Y";

    private final RedisPort redisPort;
    private final LoggerBuilder logger;

    @Value("${entries.properties.process-on-schedule}")
    private String processOnSchedule;

    @Scheduled(cron = "${entries.properties.expression-timer}")
    public void updateRedisKeyDefault() {
        if (processOnSchedule.equals(FLAG_PROCESS_YES)) {
            logger.info("Executed cron");
            redisPort.save(CacheKey.KEY_DEFAULT.getKey(), "Value modified by cron after five minutes")
                    .subscribe();
        }
    }
}