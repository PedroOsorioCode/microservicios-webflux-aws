package co.com.microservicio.aws.secretsmanager.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "adapters.secrets-manager")
public class SecretsConnectionProperties {
    private String region;
    private Integer cacheSeconds;
    private Integer cacheSize;
    private String endpoint;
}
