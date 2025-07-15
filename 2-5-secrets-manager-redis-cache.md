# Creación microservicio APIREST Java Webflux con DynamoDB - Estrategia de parámetros en Redis cache

> A continuación se indica el paso a paso que se debe realizar para continuar con el proyecto de creación de microservicios basados en la nube de AWS, esta guía comprende el uso de usar secrets-manager de aws para guardar conexiones en este caso al servicio de Redis e implementación de guardado de parámetro en redis cache.

### Requisitos: 

⚠️ Debes haber realizado el instructivo de ambiente local para comprender los comandos que usaremos<br>
[Ver documentación ambiente local](./1-1-podman-localstack-aws.md)

⚠️ Debes haber comprendido el funcionamiento de creación de secretos <br>
[Ver documentación Secret Manager](./1-2-2-secret-manager.md)

⚠️ Debes haber comprendido el funcionamiento de trabajo con Redis <br>
[Ver documentación Redis cache](./1-2-3-redis-cache.md)

⚠️ Debes haber realizado el instructivo Api REST Crud DynamoDB<br>
[Realizar instructivo](./2-3-1-crear-api-rest-informar-errores.md)

## Caso de uso:
- Uso de secretos
- Almacenamiento de parámetro en Redis cache
- Auditar cada Metodo en el API Rest

## Criterios de aceptación:
- Configurar secrets-manager en la aplicación
- Almacenar parámetro en redis cache y consulta del parámetro
- Imprimir logs auditoría por cada metodo update

## A continuación se proponen diferentes situaciones a modo de estudio

> Vamos a suponer que necesitamos auditar que usuario realiza alguna de las acciones en las api que tenemos (listar, crear, obtener uno, actualizar y borrar), para lograr esto vamos a usar un parámetro de tal forma de que si está activo se guarda auditoría, sino, entonces no se guarda; para ver alternativas de parametrización vamos a hacerlo de forma diferente para cada metodo (Listar, Crear, Borrar).

## Recursos base:

1. Agregar nuevas variables en el application-local.yaml

- Ubicarse en el proyecto application > app-service en la carpeta de resources modificamos el archivo application-local.yaml para agregar
```
adapters:
  secrets-manager:
    region: "${AWS_REGION:us-east-1}"
    endpoint: ${PARAM_URL:http://localhost:4566}
    cacheSeconds: ${AWS_CACHE_SECONDS:3600}
    cacheSize: ${AWS_CACHE_SIZE:200}
  cache:
    redis:
      expireTime: 3600
    secret:
      redis: ${SECRET_NAME_REDIS:local-redis}
  events:
    secret:
      rabbitmq: ${SECRET_NAME_RABBITMQ:local-rabbitmq}
 ```

 - Iniciar localstack y crear el secreto de conexión redis
    ```
    podman machine start
    podman start localstack
    podman start redis-container

    aws secretsmanager create-secret --name local-redis --description "Connection to Redis" --secret-string "{\"username\":\"admin\",\"password\":\"password123\",\"host\":\"localhost\",\"port\":\"6379\",\"hostReplicas\":\"localhost:6380\"}" --endpoint-url=http://localhost:4566
    ```

2. Configurar secrets-manager

    - Ubicarse en la raíz del proyecto, abrir la consola de comandos y ejecutar
        ```
        gradle generateHelper --name secrets-manager-commons
        ```

    - Ubicarse en el proyecto infrastructure > helpers > secrets-manager-commons y modificar el archivo build.gradle
        ```
        dependencies {
            implementation project(':model')
            implementation project(':log')
            implementation "org.springframework.boot:spring-boot-starter-webflux:${springBootVersion}"
            implementation "org.springframework.boot:spring-boot-starter-validation:${springBootVersion}"
            implementation 'software.amazon.awssdk:regions'
            implementation 'software.amazon.awssdk:secretsmanager'
            implementation "com.github.bancolombia:aws-secrets-manager-sync:${awsSecretManagerSyncVersion}"
            implementation "com.github.bancolombia:aws-secrets-manager-async:${awsSecretManagerAsyncVersion}"
        }
        ```
        Refrescar dependencias

    - Ubicarse en el proyecto infrastructure > helpers > secrets-manager-commons en el paquete co.com.microservicio.aws.secretsmanager.properties y crear la clase SecretsConnectionProperties.java
        ```
        package co.com.microservicio.aws.secretsmanager.properties;

        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;

        import lombok.Getter;
        import lombok.Setter;

        @Setter
        @Getter
        @Configuration
        @ConfigurationProperties(prefix = "adapters.secrets-manager")
        public class SecretsConnectionProperties {
            private String region;
            private Integer cacheSeconds;
            private Integer cacheSize;
            private String endpoint;
        }
        ```

    - Ubicarse en el proyecto infrastructure > helpers > secrets-manager-commons en el paquete co.com.microservicio.aws.secretsmanager.async y crear la clase SecretsManagerAsyncConfig.java
        ```
        package co.com.microservicio.aws.secretsmanager.async;

        import co.com.microservicio.aws.secretsmanager.properties.SecretsConnectionProperties;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.context.annotation.Profile;

        import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
        import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
        import software.amazon.awssdk.regions.Region;

        @Configuration
        public class SecretsManagerAsyncConfig {
            public static final String AWS_SECRET_MANAGER_ASYNC = "awsSecretManagerAsyncConnector";

            @Profile("!local")
            @Bean(name = AWS_SECRET_MANAGER_ASYNC)
            public AWSSecretManagerConnectorAsync managerAsync(final SecretsConnectionProperties properties) {
                return new AWSSecretManagerConnectorAsync(getBuilder(properties).build());
            }

            @Profile("local")
            @Bean(name = AWS_SECRET_MANAGER_ASYNC)
            public AWSSecretManagerConnectorAsync localManagerAsync(final SecretsConnectionProperties properties) {
                return new AWSSecretManagerConnectorAsync(getBuilder(properties).endpoint(properties.getEndpoint()).build());
            }

            private AWSSecretsManagerConfig.AWSSecretsManagerConfigBuilder getBuilder(SecretsConnectionProperties properties) {
                return AWSSecretsManagerConfig.builder().region(Region.of(properties.getRegion()))
                        .cacheSeconds(properties.getCacheSeconds()).cacheSize(properties.getCacheSize());
            }
        }
        ```

    - Ubicarse en el proyecto infrastructure > helpers > secrets-manager-commons en el paquete co.com.microservicio.aws.secretsmanager.helper y crear la clase SecretsHelper.java
        ```
        package co.com.microservicio.aws.secretsmanager.helper;

        import java.util.function.Function;

        import co.com.bancolombia.secretsmanager.api.GenericManager;
        import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
        import lombok.AccessLevel;
        import lombok.NoArgsConstructor;

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public class SecretsHelper<T, R> {

            protected String secretName;
            protected Class<T> clazz;

            protected SecretsHelper(Class<T> clazz, String secretName) {
                this.clazz = clazz;
                this.secretName = secretName;
            }

            protected R createConfigFromSecret(GenericManager manager, Function<T, R> configMaker) throws SecretException {
                try {
                    return configMaker.apply(manager.getSecret(secretName, clazz));
                } catch (SecretException exception) {
                    throw new SecretException(exception.getMessage());
                }
            }
        }
        ```

3. Configurar redis cache

    - Ubicarse en la raíz del proyecto, abrir la consola de comandos y ejecutar
        ```
        gradle generateDrivenAdapter --type=generic --name reactive-redis
        ```

    - Ubicarse en el proyecto infrastructure > helpers > secrets-manager-commons y modificar el archivo build.gradle
        ```
        dependencies {
            implementation project(':model')
            implementation project(':log')
            implementation project(':secrets-manager-commons')
            implementation "org.springframework.boot:spring-boot-starter-webflux:${springBootVersion}"
            implementation "org.springframework.boot:spring-boot-starter-validation:${springBootVersion}"
            implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'
            implementation "com.github.bancolombia:aws-secrets-manager-async:${awsSecretManagerAsyncVersion}"
        }
        ```
        Refrescar dependencias

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.properties y crear la clase CacheProperties.java
        ```
        package co.com.microservicio.aws.redis.properties;

        import lombok.Getter;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.stereotype.Component;

        @Getter
        @Setter
        @Component
        @ConfigurationProperties(prefix = "adapters.cache.redis")
        public class CacheProperties {
            private Integer expireTime;
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.properties y crear la clase RedisSecretProperties.java
        ```
        package co.com.microservicio.aws.redis.properties;

        import lombok.Getter;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.stereotype.Component;

        @Getter
        @Setter
        @Component
        @ConfigurationProperties(prefix = "adapters.cache.secret")
        public class RedisSecretProperties {
            private String redis;
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.config.model y crear la clase RedisSecret.java
        ```
        package co.com.microservicio.aws.redis.config.model;

        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Getter;
        import lombok.Setter;
        import lombok.NoArgsConstructor;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public class RedisSecret {
            private String username;
            private String password;
            private String host;
            private String port;
            private String hostReplicas;
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.config y crear la clase RedisConfigHelper.java
        ```
        package co.com.microservicio.aws.redis.config;

        import co.com.microservicio.aws.redis.config.model.RedisSecret;
        import co.com.microservicio.aws.redis.properties.RedisSecretProperties;
        import co.com.microservicio.aws.secretsmanager.helper.SecretsHelper;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.context.annotation.Primary;
        import org.springframework.context.annotation.Profile;
        import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
        import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
        import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
        import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

        import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

        @Configuration
        public class RedisConfigHelper extends SecretsHelper<RedisSecret, ReactiveRedisConnectionFactory> {

            protected RedisConfigHelper(RedisSecretProperties redisSecretProperties) {
                super(RedisSecret.class, redisSecretProperties.getRedis());
            }

            @Primary
            @Bean
            @Profile({"!local"})
            public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisSecret properties){
                RedisStaticMasterReplicaConfiguration configuration;
                configuration = new RedisStaticMasterReplicaConfiguration(
                        properties.getHost(), Integer.parseInt(properties.getPort()));
                configuration.addNode(properties.getHostReplicas(), Integer.parseInt(properties.getPort()));
                configuration.setUsername(properties.getUsername());
                configuration.setPassword(properties.getPassword());

                LettuceClientConfiguration clientConfig =
                        LettuceClientConfiguration.builder()
                                .readFrom(REPLICA_PREFERRED)
                                .useSsl()
                                .and()
                                .build();

                return new LettuceConnectionFactory(configuration, clientConfig);
            }

            @Primary
            @Bean
            @Profile({"local"})
            public ReactiveRedisConnectionFactory reactiveRedisConnectionFactoryLocal(RedisSecret properties) {
                return new LettuceConnectionFactory(properties.getHost(), Integer.parseInt(properties.getPort()));
            }

        }
        ```

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.cachey crear la clase CacheKey.java
        ```
        package co.com.microservicio.aws.cache;

        import lombok.Getter;
        import lombok.RequiredArgsConstructor;

        @Getter
        @RequiredArgsConstructor
        public enum CacheKey {
            AUDIT_ON_UPDATE("AUDIT_ON_UPDATE");

            private final String key;
        }
        ```

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.cache.gateway y crear la clase CacheRepository.java
        ```
        package co.com.microservicio.aws.cache.gateway;

        import co.com.microservicio.aws.cache.CacheKey;
        import reactor.core.publisher.Flux;
        import reactor.core.publisher.Mono;
        import java.util.function.Function;

        public interface CacheRepository<T> {
            Flux<T> save(CacheKey key, Flux<T> list, Function<T, String> retrieveKey);
            Mono<Boolean> saveDataInCacheFromEvent(String key, T entity);
            Mono<T> getByKey(CacheKey key, String name);
            Mono<Boolean> deleteTable(CacheKey key);
            Mono<Long> deleteRegisterByPrimaryKey(CacheKey key, Object... hashKeys);
            Mono<T> findForValue(String key);
            Mono<T> saveFromEventByHash(CacheKey key, Object hasKey, T entity);
            Flux<T> getValuesByKey(CacheKey key);
        }
        ```

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.commons.enums y modificar la clase TechnicalExceptionMessage.java
        ```
        package co.com.microservicio.aws.commons.enums;

        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.ToString;

        @Getter
        @AllArgsConstructor
        @ToString
        public enum TechnicalExceptionMessage {

            TECHNICAL_SERVER_ERROR("WRT01", "Internal server error"),
            TECHNICAL_REST_CLIENT_ERROR("WRT02", "An error has occurred in the Rest Client"),
            TECHNICAL_HEADER_MISSING("WRT03", "Missing parameters per header"),
            TECHNICAL_EVENT_EXCEPTION("WRT04", "An error has occurred sending event"),
            TECHNICAL_SECRET_EXCEPTION("WRT05", "An error occurred while trying to get AWS secrets"),
            TECHNICAL_REQUEST_ERROR("WRT06", "There is an error in the request body"),
            TECHNICAL_GENERATE_FILE_ERROR("WRT08", "An error occurred transforming the messaging to generate the file"),
            TECHNICAL_S3_EXCEPTION("WRT07", "An error occurred while trying to get S3 object"),
            TECHNICAL_S3_PUT_OBJECT_FAIL("WRT09", "An error has occurred upload an object in S3"),
            TECHNICAL_EXCEPTION_REPOSITORY("WRT10", "An error has occurred in the repository"),
            GET_KEY_FROM_REDIS("WRT10", "An error occurred getting one key from redis"),
            SAVE_IN_REDIS("WRT11", "An error occurred saving collection from redis"),
            DELETE_KEY_FROM_REDIS("WRT12", "An error occurred deleting from key from redis"),
            GET_KEY_FOR_VALUE_FROM_REDIS("WRT13", "An error occurred getting one key from redis"),
            SAVE_FOR_VALUE_IN_REDIS("WRT14", "An error occurred saving collection in redis"),
            DELETE_COLLECTION_FROM_REDIS("WRT15", "An error occurred deleting collection from redis"),
            GET_VALUES_FROM_REDIS("WRT16", "Error retrieving routes in redis caching"),
            CONFIG_REDIS_NOT_FOUND("WRT17", "Redis configuration not found");

            private final String code;
            private final String message;

            public String getDescription() {
                return String.join(" - ", this.getCode(), this.getMessage());
            }

        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis y crear la clase CacheManagementAdapter.java
        ```
        package co.com.microservicio.aws.redis;

        import co.com.microservicio.aws.cache.CacheKey;
        import co.com.microservicio.aws.cache.gateway.CacheRepository;
        import co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage;
        import co.com.microservicio.aws.commons.exceptions.TechnicalException;
        import co.com.microservicio.aws.redis.properties.CacheProperties;
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
        @Service
        public class CacheManagementAdapter<T> implements CacheRepository<T> {
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
        }
        ```
4. Modificar caso de uso update para validar aplicación de auditoría

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.redis.config.model y crear la clase RedisSecret.java
        ```

        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.config.model y crear la clase RedisSecret.java
        ```

        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > reactive-redis en el paquete co.com.microservicio.aws.redis.config.model y crear la clase RedisSecret.java
        ```

        ```

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)