package co.com.microservice.aws.infrastructure.output.s3repository.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "adapters.s3")
public class S3ConnectionProperties {
    private String region;
    private String endpoint;
    private String accessKey;
    private String secretKey;
}