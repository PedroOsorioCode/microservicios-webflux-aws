package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.WorldRegion;
import co.com.microservice.aws.domain.model.rq.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorldRegionPort {
    Flux<WorldRegion> findByRegion(Context context, String region);
    Mono<WorldRegion> findOne(Context context, String region, String code);
    Mono<WorldRegion> save(Context context, WorldRegion worldRegion);
    Mono<WorldRegion> update(Context context, WorldRegion worldRegion);
    Mono<WorldRegion> delete(Context context, String region, String code);
}