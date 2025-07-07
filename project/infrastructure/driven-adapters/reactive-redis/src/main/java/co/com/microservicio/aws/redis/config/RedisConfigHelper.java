package co.com.microservicio.aws.redis.config;

import co.com.microservicio.aws.redis.config.model.RedisSecret;
import co.com.microservicio.aws.redis.properties.RedisSecretProperties;
import co.com.microservicio.aws.secretsmanager.helper.SecretsHelper;
import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

@Configuration
public class RedisConfigHelper extends SecretsHelper<RedisSecret, ReactiveRedisConnectionFactory> {

    protected RedisConfigHelper(@Value("${adapters.cache.secret.redis}") String  redisSecretProperties) {
        super(RedisSecret.class, redisSecretProperties);
    }

    @Primary
    @Bean
    @Profile("local")
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactoryLocal() {
        RedisSecret localSecret = new RedisSecret();
        localSecret.setHost("localhost");
        localSecret.setPort("6379");
        localSecret.setUsername("admin");
        localSecret.setPassword("password123");
        localSecret.setHostReplicas("localhost");

        return buildConnectionFactory(localSecret);
    }

    private ReactiveRedisConnectionFactory buildConnectionFactory(RedisSecret secret) {
        RedisStaticMasterReplicaConfiguration configuration =
                new RedisStaticMasterReplicaConfiguration(
                        secret.getHost(), Integer.parseInt(secret.getPort()));
        configuration.addNode(secret.getHostReplicas(), Integer.parseInt(secret.getPort()));
        configuration.setUsername(secret.getUsername());
        configuration.setPassword(secret.getPassword());

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .useSsl()
                .and()
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

}
