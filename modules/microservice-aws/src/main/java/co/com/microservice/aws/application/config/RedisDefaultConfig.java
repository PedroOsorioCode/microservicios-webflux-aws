package co.com.microservice.aws.application.config;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
import co.com.microservice.aws.domain.usecase.out.RedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDefaultConfig {
    private final RedisPort redisPort;
    private final LoggerBuilder logger;

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void initialDefaultRedis() {
        logger.info("Executed key default");
        redisPort.save(CacheKey.KEY_DEFAULT.getKey(), "Value by started application")
                .subscribe();
    }
}