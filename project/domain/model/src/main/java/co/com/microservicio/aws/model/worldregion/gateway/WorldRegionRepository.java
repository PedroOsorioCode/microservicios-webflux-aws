package co.com.microservicio.aws.model.worldregion.gateway;

import co.com.microservicio.aws.model.worldregion.WorldRegion;
import reactor.core.publisher.Flux;

public interface WorldRegionRepository {

    Flux<WorldRegion> findByEntityType(String entityType);

    Flux<WorldRegion> findByParentCodeAndEntityType(String parentCode, String entityType);
}
