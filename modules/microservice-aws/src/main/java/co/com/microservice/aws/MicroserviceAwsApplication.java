package co.com.microservice.aws;

import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableEventListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDomainEventBus
@EnableEventListeners
@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
		org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration.class
})
public class MicroserviceAwsApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceAwsApplication.class, args);
	}
}