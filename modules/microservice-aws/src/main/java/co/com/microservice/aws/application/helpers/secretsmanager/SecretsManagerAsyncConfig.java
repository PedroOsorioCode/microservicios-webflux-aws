package co.com.microservice.aws.application.helpers.secretsmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;

@Configuration
public class SecretsManagerAsyncConfig {
    public static final String AWS_SECRET_MANAGER_ASYNC = "awsSecretManagerSyncConnector";

    @Profile("!local")
    @Bean(name = AWS_SECRET_MANAGER_ASYNC)
    public SecretsManagerClient secretsManagerClient(final SecretsConnectionProperties properties) {
        return SecretsManagerClient.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Profile("local")
    @Bean(name = AWS_SECRET_MANAGER_ASYNC)
    public SecretsManagerClient localManagerAsync(final SecretsConnectionProperties properties) {
        return SecretsManagerClient.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}