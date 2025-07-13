package co.com.microservice.aws.infrastructure.output.postgresql.repository;

import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends R2dbcRepository<CountryEntity, Long> {
    Mono<CountryEntity> findByShortCode(String shortCode);
}