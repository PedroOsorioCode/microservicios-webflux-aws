package co.com.microservice.aws.infrastructure.output.postgresql.repository;

import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface CountryRepository extends R2dbcRepository<CountryEntity, Long> {
    Mono<CountryEntity> findByShortCode(String shortCode);

    @Query("SELECT COUNT(1) FROM worldregion.countries WHERE status = :status")
    Mono<Integer> countByStatus(@Param("status") boolean status);
}