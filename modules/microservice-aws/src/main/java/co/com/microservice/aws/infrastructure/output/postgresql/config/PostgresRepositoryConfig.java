package co.com.microservice.aws.infrastructure.output.postgresql.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(
        basePackages = "co.com.microservice.aws.infrastructure.output.postgresql.repository",
        entityOperationsRef = "postgresEntityTemplate"
)
public class PostgresRepositoryConfig {
}