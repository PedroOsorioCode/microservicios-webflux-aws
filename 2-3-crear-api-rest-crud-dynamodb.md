# Creación microservicio APIREST con Webflux
> A continuación se indica el paso a paso que se debe realizar para continuar con el proyecto de creación de microservicios basados en la nube de AWS, esta guía comprende la creación de API REST con metodos HTTP a una tabla en dynamo db con un caso practico real

### Requisitos: 

⚠️ Debes haber comprendido el funcionamiento de creación de tablas en DynamoDB <br>
[Ver documentación dynamoDB](./1-2-1-dynamodb.md)

⚠️ Debes haber realizado el proyecto base para continuar con este instructivo <br>
[Crear proyecto base](./2-1-crear-proyecto-base.md)

⚠️ Debes haber realizado el proyecto api rest para continuar con este instructivo <br>
[Crear proyecto base](./2-2-crear-api-rest.md)

## Creación de la tabla dynamoDB en ambiente local


10. Creamos la clase POJO Flight.java en el paquete co.com.microservicio.aws.model.flight para mapear todos los datos de la tabla de dynamoDB
        
    ```
    package co.com.microservicio.aws.model.flight;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.io.Serial;
    import java.io.Serializable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class FlightTicket implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String documentNumber;
        private String ticket;
        private String status;
        private String flightNumber;
        private String origin;
        private String destination;
        private Double price;
        private String date;
    }
    ```

11. Creamos la clase FlightRepository en el paquete co.com.microservicio.aws.model.flight.gateway
    ```
    package co.com.microservicio.aws.model.flight.gateway;

    import co.com.microservicio.aws.model.flight.FlightTicket;
    import reactor.core.publisher.Mono;

    import java.util.Map;

    public interface FlightRepository {
        Mono<FlightTicket> getAllRows(Map<String, String> param);
    }
    ```

12. Creamos la clase FlightTicketUseCase.java en el paquete co.com.microservicio.aws.usecase.flight para obtener todos los datos de la tabla de dynamoDB, hacemos una pequeña validación del message-id para aplicar metodos webflux
    ```
    package co.com.microservicio.aws.usecase.flight;

    import co.com.microservicio.aws.model.flight.FlightTicket;
    import co.com.microservicio.aws.model.flight.gateway.FlightRepository;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Map;

    @RequiredArgsConstructor
    public class FlightTicketUseCase {
        private static final String KEY_SIZE = "size";
        private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";
        private final FlightRepository flightRepository;

        public Mono<FlightTicket> getAllRows(Map<String, String> param){
            return Mono.just(param).filter(this::isEmpty)
                .flatMap(flightRepository::getAllRows)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format(ATTRIBUTE_IS_REQUIRED, KEY_SIZE))));
        }

        private Boolean isEmpty(Map<String, String> param){
            return !param.get(KEY_SIZE).isEmpty();
        }
    }
    ```

13. Modificamos la clase Handler.java para invocar el caso de uso
    ```
    package co.com.microservicio.aws.api;

    import co.com.microservicio.aws.log.LoggerBuilder;
    import co.com.microservicio.aws.log.TransactionLog;
    import co.com.microservicio.aws.model.flight.FlightTicket;
    import co.com.microservicio.aws.usecase.flight.FlightTicketUseCase;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    import java.util.Collection;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;

    @Component
    @RequiredArgsConstructor
    public class Handler {
        private static final String NAME_CLASS = Handler.class.getName();
        private static final String MESSAGE_SERVICE = "Service Api Rest get alls rows by size";
        private final LoggerBuilder logger;
        private final FlightTicketUseCase flightTicketUseCase;

        public Mono<ServerResponse> getAllRows(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var messageId = headers.get("message-id");
            logger.info(TransactionLog.Request.builder().body(headers).build(), null,
                "My first api rest", messageId, MESSAGE_SERVICE, NAME_CLASS);

            return ServerResponse.ok().body(flightTicketUseCase.getAllRows(headers)
                .onErrorResume(e -> this.printFailed(e, messageId)), FlightTicket.class
            );
        }

        private Mono<FlightTicket> printFailed(Throwable throwable, String messageId) {
            logger.error(throwable.getMessage(), messageId, MESSAGE_SERVICE, NAME_CLASS);
            return Mono.empty();
        }
    }
    ```

14. Creamos la conexión con DynamoDB para implementar la interfaz de conexión entre el caso de uso y la infrastructura de conexión con DynamoDB
    
    - Ubicarse en la raiz del proyecto, abrir la consola de comandos y ejecutar el comando de creación del driven-adapter con DynamoDB
   ```
   gradle generateDrivenAdapter --type=dynamodb
   ``` 

   ![](./img/apirest-crear-driven-adapter-dynamodb.png)

15. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase

   ```
    package co.com.microservicio.aws.dynamodb;

    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DynamoDbTableAdapter {
        String tableName() default "";
    }
   ```

16. En el paquete 'co.com.microservicio.aws.dynamodb.config' creamos la siguiente clase
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class SourceName {
        public static final String FLIGHT_TICKETS = "flight_tickets";
    }
    ```

17. En el paquete 'co.com.microservicio.aws.dynamodb.flight.model' creamos la siguiente clase de acuerdo a los datos a almacenar en la tabla
    ```
    package co.com.microservicio.aws.dynamodb.model;

    import co.com.microservicio.aws.dynamodb.DynamoDbTableAdapter;
    import co.com.microservicio.aws.dynamodb.config.SourceName;
    import lombok.Data;
    import lombok.Getter;
    import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
    import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
    import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
    import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

    @Data
    @DynamoDbBean
    @DynamoDbTableAdapter(tableName = SourceName.FLIGHT_TICKETS)
    public class ModelEntityFlight {
        @Getter(onMethod_ = @DynamoDbPartitionKey)
        private String documentNumber;
        @Getter(onMethod_ = @DynamoDbSortKey)
        private String ticket;
        @Getter(onMethod_ = @DynamoDbSecondaryPartitionKey(indexNames = "statusIndex"))
        private String status;
        private String flightNumber;
        private String origin;
        private String destination;
        private Double price;
        private String date;
    }
    ```

18. Cambiamos la clase DynamoDBConfig en el paquete 'co.com.microservicio.aws.dynamodb.config' por el siguiente código
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
    import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.regions.Region;
    import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

    import java.net.URI;

    @Configuration
    public class DynamoDBConfig {

        @Bean
        @Profile({ "local" })
        DynamoDbAsyncClient amazonDynamoDB(@Value("${aws.dynamodb.endpoint}") String endpoint,
                                        @Value("${aws.region}") String region) {
            return DynamoDbAsyncClient.builder().credentialsProvider(ProfileCredentialsProvider.create("default"))
                    .endpointOverride(URI.create(endpoint)).region(Region.of(region)).build();
        }

        @Bean
        @Profile({ "!local" })
        DynamoDbAsyncClient amazonDynamoDBAsync(@Value("${aws.region}") String region) {
            return DynamoDbAsyncClient.builder().credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .region(Region.of(region)).build();
        }

        @Bean
        DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
            return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDbAsyncClient).build();
        }

    }
    ```

19. En el archivo build.gradle del proyecto dynamo-db colocamos las siguientes dependencias
    ```
    dependencies {
        implementation project(':model')
        implementation 'org.springframework:spring-context'
        implementation 'software.amazon.awssdk:dynamodb-enhanced'
        implementation 'org.reactivecommons.utils:object-mapper-api:0.1.0'
        implementation 'org.springframework.boot:spring-boot-starter-validation'
        testImplementation 'org.reactivecommons.utils:object-mapper:0.1.0'
    }
    ```

20. En el paquete 'co.com.microservicio.aws.dynamodb.config' creamos la siguiente clase
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import java.util.Map;

    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.annotation.Configuration;

    import lombok.Data;

    @Data
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "adapters.repositories.tables")
    public class DynamoDBTablesProperties {
        private Map<String, String> namesmap;
    }
    ```

21. Cambiamos la clase DynamoDBConfigTest en el paquete 'co.com.microservicio.aws.dynamodb.config' por el siguiente código
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.metrics.MetricPublisher;
    import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

    import static org.junit.jupiter.api.Assertions.assertNotNull;

    @ExtendWith(MockitoExtension.class)
    class DynamoDBConfigTest {

        @Mock
        private DynamoDbAsyncClient dynamoDbAsyncClient;
        private final DynamoDBConfig dynamoDBConfig = new DynamoDBConfig();

        @Test
        void testAmazonDynamoDB() {
            DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDB("http://aws.dynamo.test", "region");
            assertNotNull(result);
        }

        @Test
        void testAmazonDynamoDBAsync() {
            DynamoDbAsyncClient result = dynamoDBConfig.amazonDynamoDBAsync("region");
            assertNotNull(result);
        }

        @Test
        void testGetDynamoDbEnhancedAsyncClient() {
            DynamoDbEnhancedAsyncClient result = dynamoDBConfig.getDynamoDbEnhancedAsyncClient(dynamoDbAsyncClient);
            assertNotNull(result);
        }
    }
    ```
22. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase
    ```
    package co.com.microservicio.aws.dynamodb;

    import java.util.function.Function;

    import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
    import reactor.core.publisher.Mono;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.enhanced.dynamodb.Key;
    import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

    public class AdapterOperations<E, D> {
        protected DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient;
        protected Function<E, D> fnToData;
        protected Function<D, E> fnToEntity;
        protected DynamoDbAsyncTable<D> dataTable;

        public AdapterOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                                DynamoDBTablesProperties tablesProperties, Function<E, D> fnToData, Function<D, E> fnToEntity,
                                Class<D> dataClass) {
            this.dbEnhancedAsyncClient = dbEnhancedAsyncClient;
            this.fnToData = fnToData;
            this.fnToEntity = fnToEntity;
            var dynamoDbTableAdapter = dataClass.getAnnotation(DynamoDbTableAdapter.class);
            var tableName = tablesProperties.getNamesmap().get(dynamoDbTableAdapter.tableName());
            dataTable = dbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
        }

        protected D toData(E entity) {
            return fnToData.apply(entity);
        }

        protected E toEntity(D data) {
            return fnToEntity.apply(data);
        }

        protected Mono<E> findOne(Key id) {
            return Mono.fromFuture(dataTable.getItem(id)).map(this::toEntity);
        }

        protected Mono<E> update(E entity) {
            return Mono.fromFuture(dataTable.updateItem(toData(entity))).map(this::toEntity);
        }
    }
    ```

23. En el paquete 'co.com.microservicio.aws.model.flight' creamos la siguiente clase
    ```
    package co.com.microservicio.aws.model.flight;

    import java.io.Serializable;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class ValidationResponse implements Serializable {
        private static final long serialVersionUID = 1L;
        private boolean valid;
        private String reason;
        private String validationCode;
    }
    ```

23. En el paquete 'co.com.microservicio.aws.dynamodb.flight.mapper' creamos la siguiente clase a cargo de mapear los datos de la clase DTO a la clase Entity
    ```
    package co.com.microservicio.aws.dynamodb.flight.mapper;

    import co.com.microservicio.aws.dynamodb.flight.model.ModelEntityFlight;
    import co.com.microservicio.aws.model.flight.FlightTicket;

    import java.util.List;

    import co.com.microservicio.aws.model.flight.ValidationResponse;
    import org.apache.logging.log4j.util.Strings;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.Named;
    import org.mapstruct.ReportingPolicy;

    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import lombok.SneakyThrows;

    @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
    public interface FlightTicketDataMapper {
        ModelEntityFlight toData(FlightTicket flightTicket);

        @Mapping(target = "errors", source = "errors", qualifiedByName = "mapErrors")
        FlightTicket toEntity(ModelEntityFlight modelEntityFlight);

        @SneakyThrows
        default String getErrors(List<ValidationResponse> errors) {
            var mapper = new ObjectMapper();
            return errors != null ? mapper.writeValueAsString(errors) : null;
        }

        @SneakyThrows
        @Named("mapErrors")
        default List<ValidationResponse> getErrors(String errors) {
            var mapper = new ObjectMapper();
            return Strings.isBlank(errors) ? List.of() : mapper.readValue(errors, new TypeReference<>() {
            });
        }
    }
    ```

23. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase correspondiente al adaptador que implementa la consulta en la bd
    ```

    ```



## API Rest con anotaciones



[< Volver](README-PROYECTO-JAVA-WEBFLUX.md)

---

**Author**: Pedro Luis Osorio Pavas [Linkedin](www.linkedin.com/in/pedro-luis-osorio-pavas-68b3a7106)  
**Start Date**: 01-06-2025  
**Update Date**: 01-06-2025.