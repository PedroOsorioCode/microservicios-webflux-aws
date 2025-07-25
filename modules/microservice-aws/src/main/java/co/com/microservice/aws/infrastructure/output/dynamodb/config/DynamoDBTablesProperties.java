package co.com.microservice.aws.infrastructure.output.dynamodb.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "adapters.repositories.tables")
public class DynamoDBTablesProperties {
    private Map<String, String> namesmap;
}