package co.com.microservicio.aws.model.worldregion.gateway;

import co.com.microservicio.aws.model.worldregion.WorldRegion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorldRegionRepository {

    Flux<WorldRegion> findByRegion(String region);

    Mono<WorldRegion> findOne(String region, String code);

    Mono<WorldRegion> save(WorldRegion worldRegion);

    Mono<WorldRegion> update(WorldRegion worldRegion);

    Mono<WorldRegion> delete(String region, String code);
}
