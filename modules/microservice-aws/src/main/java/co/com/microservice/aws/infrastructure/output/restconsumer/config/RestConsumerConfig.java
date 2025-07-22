package co.com.microservice.aws.infrastructure.output.restconsumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static co.com.microservice.aws.infrastructure.output.restconsumer.config.RestConsumerUtils.getClientHttpConnector;

@Configuration
@RequiredArgsConstructor
public class RestConsumerConfig {
    private final RestConsumerProperties properties;

    @Bean(name = "webClientConfig")
    public WebClient webClientConfig() {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(getClientHttpConnector((long) properties.getTimeout()))
                .build();
    }
}