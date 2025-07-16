package co.com.microservice.aws.infrastructure.output.redis.config;

import co.com.microservice.aws.application.helpers.utils.SecretUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

@Configuration
public class RedisConfig {
    private final SecretsManagerClient secretsClient;
    private final String secretNameBd;

    public RedisConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                          @Value("${adapters.secrets-manager.nameRedis}") String secretNameBd){
        this.secretsClient = secretsClient;
        this.secretNameBd = secretNameBd;
    }

    @Bean(name = "customRedisConnectionFactory")
    public ReactiveRedisConnectionFactory redisConnectionFactory() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretNameBd)
                .build();

        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        String secretJson = response.secretString();

        Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(secrets.get("host"));
        config.setPort(Integer.parseInt(secrets.get("port")));
        config.setPassword(RedisPassword.of(secrets.get("password")));

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveRedisTemplate(
            @Qualifier("customRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }
}