package co.com.microservicio.aws.redis;

import co.com.microservicio.aws.cache.CacheKey;
import co.com.microservicio.aws.cache.gateway.CacheGateway;
import co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage;
import co.com.microservicio.aws.commons.exceptions.TechnicalException;
import co.com.microservicio.aws.redis.properties.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.function.Function;
import static org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;

@Configuration
public class CacheManagementAdapter<T> implements CacheGateway<T> {
    private final ReactiveRedisTemplate<String, T> cacheOperationsForHash;
    private final ReactiveRedisTemplate<String, T> cacheOperationsForValue;
    private final Class<T> type;
    private final CacheProperties cacheProperties;

    public CacheManagementAdapter(Class<T> persistentClass, ReactiveRedisConnectionFactory factory,
                                  CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        RedisSerializationContextBuilder<String, T> builder = RedisSerializationContext.
                newSerializationContext(new StringRedisSerializer());
        cacheOperationsForValue = new ReactiveRedisTemplate<>(factory, builder.
                value(new Jackson2JsonRedisSerializer<>(persistentClass)).build());
        cacheOperationsForHash = new ReactiveRedisTemplate<>(factory, builder.
                hashValue(new Jackson2JsonRedisSerializer<>(persistentClass)).build());
        type = persistentClass;
    }

    @Override
    public Mono<Boolean> saveDataInCacheFromEvent(String key, T entity) {
        return Mono.just(entity)
                .flatMap(value -> cacheOperationsForValue.opsForValue().set(key, value,
                        Duration.ofMinutes(cacheProperties.getExpireTime())))
                .defaultIfEmpty(Boolean.FALSE)
                .onErrorMap(Exception.class, exception -> new TechnicalException(exception,
                        TechnicalExceptionMessage.SAVE_FOR_VALUE_IN_REDIS));
    }

    @Override
    public Flux<T> save(CacheKey key, Flux<T> list, Function<T, String> retrieveKey) {
        return list
                .switchIfEmpty(Mono.error(new TechnicalException(TechnicalExceptionMessage.CONFIG_REDIS_NOT_FOUND)))
                .collectList()
                .flatMapMany(conf ->
                        Flux.fromIterable(conf)
                                .collectMap(retrieveKey)
                                .flatMap(x -> cacheOperationsForHash.opsForHash().putAll(key.getKey(), x))
                                .thenMany(Flux.fromIterable(conf))
                )
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.SAVE_IN_REDIS));
    }

    @Override
    public Mono<T> saveFromEventByHash(CacheKey key, Object hasKey, T entity) {
        return cacheOperationsForHash.opsForHash().put(key.getKey(), hasKey, entity)
                .thenReturn(entity)
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.SAVE_IN_REDIS));
    }

    @Override
    public Flux<T> getValuesByKey(CacheKey key) {
        return cacheOperationsForHash.opsForHash().values(key.getKey())
                .cast(type)
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.GET_VALUES_FROM_REDIS));
    }

    @Override
    public Mono<T> getByKey(CacheKey key, String name) {
        return cacheOperationsForHash.opsForHash().get(key.getKey(), name)
                .cast(type)
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.GET_KEY_FROM_REDIS));
    }

    @Override
    public Mono<T> findForValue(String key) {
        return cacheOperationsForValue.opsForValue().get(key)
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.GET_KEY_FOR_VALUE_FROM_REDIS));
    }

    @Override
    public Mono<Boolean> deleteTable(CacheKey key) {
        return cacheOperationsForHash.opsForHash()
                .delete(key.getKey())
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.DELETE_COLLECTION_FROM_REDIS));
    }

    @Override
    public Mono<Long> deleteRegisterByPrimaryKey(CacheKey key, Object... hashKeys) {
        return cacheOperationsForHash.opsForHash()
                .remove(key.getKey(), hashKeys)
                .onErrorMap(Exception.class, exception ->
                        new TechnicalException(exception,
                                TechnicalExceptionMessage.DELETE_KEY_FROM_REDIS));
    }

    @Bean
    public Class<?> getType() {
        return Boolean.class;
    }
}
