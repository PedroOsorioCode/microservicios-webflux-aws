package co.com.microservice.aws.infrastructure.output.postgresql.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@RequiredArgsConstructor
public class PostgresConfig {

    @Bean
    public ConnectionFactory postgresConnectionFactory(@Value("${adapters.postgresql.url}") String url,
                                                       @Value("${adapters.postgresql.usr}") String usr,
                                                       @Value("${adapters.postgresql.psw}") String psw) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url)
                .mutate()
                .option(USER, usr)
                .option(PASSWORD, psw)
                .build();

        return ConnectionFactories.get(options);
    }
}