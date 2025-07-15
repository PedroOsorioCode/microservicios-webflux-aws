package co.com.microservice.aws.infrastructure.output.postgresql.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
@RequiredArgsConstructor
public class PostgresConfig {

    @Bean(name = "postgresConnectionFactory")
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

    @Bean(name = "postgresEntityTemplate")
    public R2dbcEntityTemplate postgresEntityTemplate(@Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}