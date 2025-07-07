package co.com.microservicio.aws.cache.gateway;

import co.com.microservicio.aws.cache.CacheKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.function.Function;

public interface CacheGateway<T> {
    Flux<T> save(CacheKey key, Flux<T> list, Function<T, String> retrieveKey);

    Mono<Boolean> saveDataInCacheFromEvent(String key, T entity);

    Mono<T> getByKey(CacheKey key, String name);

    Mono<Boolean> deleteTable(CacheKey key);

    Mono<Long> deleteRegisterByPrimaryKey(CacheKey key, Object... hashKeys);

    Mono<T> findForValue(String key);

    Mono<T> saveFromEventByHash(CacheKey key, Object hasKey, T entity);

    Flux<T> getValuesByKey(CacheKey key);
}
