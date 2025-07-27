package co.com.microservice.jwt.infrastructure.input.rest.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "entries.web")
public class RouterProperties {
    private String pathBase;
    private String pathPublic;
    private String pathPrivate;
    private String pathUser;
    private String pathInfo;
    private String validateSecurity;
    private String token;
}