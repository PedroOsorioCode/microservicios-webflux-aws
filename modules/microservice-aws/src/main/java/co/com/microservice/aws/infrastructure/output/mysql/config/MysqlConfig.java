package co.com.microservice.aws.infrastructure.output.mysql.config;

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
public class MysqlConfig {

    @Bean(name = "mysqlConnectionFactory")
    public ConnectionFactory MysqlConfig(@Value("${adapters.mysql.url}") String url,
                                         @Value("${adapters.mysql.usr}") String usr,
                                         @Value("${adapters.mysql.psw}") String psw) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url)
                .mutate()
                .option(USER, usr)
                .option(PASSWORD, psw)
                .build();

        return ConnectionFactories.get(options);
    }

    @Bean(name = "mysqlEntityTemplate")
    public R2dbcEntityTemplate mysqlEntityTemplate(@Qualifier("mysqlConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}