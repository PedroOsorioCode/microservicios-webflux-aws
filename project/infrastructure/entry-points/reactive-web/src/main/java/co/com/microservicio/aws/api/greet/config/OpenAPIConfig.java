package co.com.microservicio.aws.api.greet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    private static final String TITLE = "API Rest DynamoDB Microservice";
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "Services for greet";
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title(TITLE).version(VERSION).description(DESCRIPTION));
    }
}
