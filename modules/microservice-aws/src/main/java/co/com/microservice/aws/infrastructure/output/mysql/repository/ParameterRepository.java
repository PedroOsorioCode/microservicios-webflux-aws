package co.com.microservice.aws.infrastructure.output.mysql.repository;

import co.com.microservice.aws.infrastructure.output.mysql.entity.ParameterEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ParameterRepository extends R2dbcRepository<ParameterEntity, Long> {
    Mono<ParameterEntity> findByName(String name);
}
