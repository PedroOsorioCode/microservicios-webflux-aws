package co.com.microservice.aws.infrastructure.output.postgresql.config;

import co.com.microservice.aws.application.helpers.utils.SecretUtil;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
public class PostgresConfig {
    private final SecretsManagerClient secretsClient;
    private final String secretNameBd;

    public PostgresConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                          @Value("${adapters.secrets-manager.namePostgresql}") String secretNameBd){
        this.secretsClient = secretsClient;
        this.secretNameBd = secretNameBd;
    }

    @Bean(name = "postgresConnectionFactory")
    public ConnectionFactory postgresConnectionFactory() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretNameBd)
                .build();

        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        String secretJson = response.secretString();

        Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(secrets.get("url"))
                .mutate()
                .option(USER, secrets.get("usr"))
                .option(PASSWORD, secrets.get("psw"))
                .build();

        return ConnectionFactories.get(options);
    }

    @Bean(name = "postgresEntityTemplate")
    public R2dbcEntityTemplate postgresEntityTemplate(
            @Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}