package co.com.microservice.aws.infrastructure.output.rabbiteventbus.config;

import co.com.microservice.aws.application.helpers.utils.SecretUtil;
import org.reactivecommons.async.rabbit.config.RabbitProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

@Configuration
public class RabbitMqConfig {
    private final SecretsManagerClient secretsClient;
    private final String secretNameRabbit;

    public RabbitMqConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                          @Value("${adapters.secrets-manager.nameRabbitMq}") String secretNameRabbit){
        this.secretsClient = secretsClient;
        this.secretNameRabbit = secretNameRabbit;
    }

    @Primary
    @Bean
    public RabbitProperties customRabbitProperties() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretNameRabbit)
                .build();

        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        String secretJson = response.secretString();

        Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

        RabbitProperties properties = new RabbitProperties();
        properties.setHost(secrets.get("hostname"));
        properties.setPort(Integer.parseInt(secrets.get("port")));
        properties.setVirtualHost(secrets.get("virtualhost"));
        properties.setUsername(secrets.get("username"));
        properties.setPassword(secrets.get("password"));
        return properties;
    }
}