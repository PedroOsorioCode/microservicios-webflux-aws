package co.com.microservice.aws.infrastructure.input.rest.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "entries.web")
public class RouterProperties {
    private String pathBase;
    private String pathCountries;
    private String listAll;
    private String findByShortCode;
    private String save;
    private String update;
    private String delete;
}