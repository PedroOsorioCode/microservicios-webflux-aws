package co.com.microservicio.aws.secretsmanager.async;

import co.com.microservicio.aws.secretsmanager.properties.SecretsConnectionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import software.amazon.awssdk.regions.Region;

@Configuration
public class SecretsManagerAsyncConfig {
    public static final String AWS_SECRET_MANAGER_ASYNC = "awsSecretManagerAsyncConnector";

    @Profile("!local")
    @Bean(name = AWS_SECRET_MANAGER_ASYNC)
    public AWSSecretManagerConnectorAsync managerAsync(final SecretsConnectionProperties properties) {
        return new AWSSecretManagerConnectorAsync(getBuilder(properties).build());
    }

    @Profile("local")
    @Bean(name = AWS_SECRET_MANAGER_ASYNC)
    public AWSSecretManagerConnectorAsync localManagerAsync(final SecretsConnectionProperties properties) {
        return new AWSSecretManagerConnectorAsync(getBuilder(properties).endpoint(properties.getEndpoint()).build());
    }

    private AWSSecretsManagerConfig.AWSSecretsManagerConfigBuilder getBuilder(SecretsConnectionProperties properties) {
        return AWSSecretsManagerConfig.builder().region(Region.of(properties.getRegion()))
                .cacheSeconds(properties.getCacheSeconds()).cacheSize(properties.getCacheSize());
    }
}
