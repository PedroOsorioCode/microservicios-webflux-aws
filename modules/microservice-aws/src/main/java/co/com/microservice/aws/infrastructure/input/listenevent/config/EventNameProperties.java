package co.com.microservice.aws.infrastructure.input.listenevent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "listen.event.names")
public class EventNameProperties {
    private String saveCountry;
    private String saveCacheCountCountry;
}