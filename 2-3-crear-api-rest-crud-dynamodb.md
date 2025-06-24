# Creación microservicio APIREST Java Webflux con DynamoDB
> A continuación se indica el paso a paso que se debe realizar para continuar con el proyecto de creación de microservicios basados en la nube de AWS, esta guía comprende la creación de API REST con metodos HTTP a una tabla en dynamo db con un caso practico real

### Requisitos: 

⚠️ Debes haber realizado el instructivo de ambiente local para comprender los comandos que usaremos<br>
[Ver documentación ambiente local](./1-1-podman-localstack-aws.md)

⚠️ Debes haber comprendido el funcionamiento de creación de tablas en DynamoDB <br>
[Ver documentación dynamoDB](./1-2-1-dynamodb.md)

⚠️ Debes haber realizado el proyecto base para continuar con este instructivo <br>
[Crear proyecto base](./2-1-crear-proyecto-base.md)

⚠️ Debes haber realizado el proyecto api rest para continuar con este instructivo <br>
[Crear proyecto base](./2-2-crear-api-rest.md)

⚠️ Debes haber comprendido algunos de los flujos en programación reactiva<br>
[Spring webflux](./1-3-spring-webflux.md)

## Caso de uso:
Permitir crear, actualizar, borrar y consultar la ubicación geográfica partiendo del nivel más general (país) hasta el más específico (unidad o conjunto residencial).

## Criterios de aceptación:
- Listar todos los paises
- Listar departamentos por pais
- Listar ciudad por departamento
- Listar barrio por ciudad
- Listar unidad por barrio
- Registrar item en la tabla
- Borrar item
- Actualizar dirección a un item

## Creación de la tabla dynamoDB en ambiente local

1. Comandos ambiente local (si usas docker cambias *podman* por *docker*)
    ```
    podman machine start
    podman start localstack
    ```

    ![](./img/webflux-iniciar-ambiente-local.png)

2. Creación de la estructura de la tabla
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name local_worldregions --attribute-definitions AttributeName=PrimaryKey,AttributeType=S AttributeName=SortKey,AttributeType=S AttributeName=EntityTypeKey,AttributeType=S AttributeName=EntityName,AttributeType=S --key-schema AttributeName=PrimaryKey,KeyType=HASH AttributeName=SortKey,KeyType=RANGE --global-secondary-indexes "[{\"IndexName\": \"EntityTypeIndex\",\"KeySchema\": [{\"AttributeName\": \"EntityTypeKey\",\"KeyType\": \"HASH\"},{\"AttributeName\": \"EntityName\",\"KeyType\": \"RANGE\"}],\"Projection\": {\"ProjectionType\": \"ALL\"},\"ProvisionedThroughput\": {\"ReadCapacityUnits\": 5,\"WriteCapacityUnits\": 5}}]" --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
    ```
### 🧾 Aclaración de atributos en el comando `aws dynamodb create-table`

| Parámetro                    | Valor usado              | ¿Es palabra reservada de AWS? | Descripción                                                                                                                                 |
|-----------------------------|--------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `AttributeName`             | `PrimaryKey`             | ❌ No                         | Nombre personalizado del atributo que usas como clave de partición (HASH).                                                                 |
| `AttributeName`             | `SortKey`                | ❌ No                         | Nombre personalizado del atributo que usas como clave de ordenamiento (RANGE).                                                              |
| `AttributeName`             | `EntityTypeKey`          | ❌ No                         | Atributo personalizado para clasificar el tipo de entidad (por ejemplo: país, ciudad, unidad).                                              |
| `AttributeName`             | `EntityName`             | ❌ No                         | Atributo personalizado que representa el nombre de la entidad (ejemplo: "Colombia", "Bogotá", "Chapinero").                                |
| `KeyType`                   | `HASH` / `RANGE`         | ✅ Sí                        | Palabras reservadas de AWS que indican si el atributo es una clave de partición (HASH) o de ordenamiento (RANGE).                          |
| `ProjectionType`            | `ALL`                    | ✅ Sí                        | Palabra reservada que indica que todos los atributos del ítem estarán disponibles en el índice secundario global.                         |

3. Así queda creada la tabla
    ```
    {
        "TableDescription": {
            "AttributeDefinitions": [
                {
                    "AttributeName": "PrimaryKey",
                    "AttributeType": "S"
                },
                {
                    "AttributeName": "SortKey",
                    "AttributeType": "S"
                },
                {
                    "AttributeName": "EntityTypeKey",
                    "AttributeType": "S"
                },
                {
                    "AttributeName": "EntityName",
                    "AttributeType": "S"
                }
            ],
            "TableName": "local_worldregions",
            "KeySchema": [
                {
                    "AttributeName": "PrimaryKey",
                    "KeyType": "HASH"
                },
                {
                    "AttributeName": "SortKey",
                    "KeyType": "RANGE"
                }
            ],
            "TableStatus": "ACTIVE",
            "CreationDateTime": "2025-06-22T10:08:20.727000-05:00",
            "ProvisionedThroughput": {
                "LastIncreaseDateTime": "1969-12-31T19:00:00-05:00",
                "LastDecreaseDateTime": "1969-12-31T19:00:00-05:00",
                "NumberOfDecreasesToday": 0,
                "ReadCapacityUnits": 5,
                "WriteCapacityUnits": 5
            },
            "TableSizeBytes": 0,
            "ItemCount": 0,
            "TableArn": "arn:aws:dynamodb:us-east-1:000000000000:table/local_worldregions",
            "GlobalSecondaryIndexes": [
                {
                    "IndexName": "EntityTypeIndex",
                    "KeySchema": [
                        {
                            "AttributeName": "EntityTypeKey",
                            "KeyType": "HASH"
                        },
                        {
                            "AttributeName": "EntityName",
                            "KeyType": "RANGE"
                        }
                    ],
                    "Projection": {
                        "ProjectionType": "ALL"
                    },
                    "IndexStatus": "ACTIVE",
                    "ProvisionedThroughput": {
                        "ReadCapacityUnits": 5,
                        "WriteCapacityUnits": 5
                    },
                    "IndexSizeBytes": 0,
                    "ItemCount": 0,
                    "IndexArn": "arn:aws:dynamodb:ddblocal:000000000000:table/local_worldregions/index/EntityTypeIndex"
                }
            ]
        }
    }
    ```

4. Ingreso items a la tabla (*Los comandos estan organizados para ejecutar en command line de windows*)
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"CITY#MED\"},\"SortKey\":{\"S\":\"NEIGHBORHOOD#ROB\"},\"EntityType\":{\"S\":\"NEIGHBORHOOD\"},\"Code\":{\"S\":\"ROB\"},\"Name\":{\"S\":\"Robledo\"},\"ParentCode\":{\"S\":\"MED\"},\"EntityTypeKey\":{\"S\":\"NEIGHBORHOOD\"},\"EntityName\":{\"S\":\"Robledo\"},\"Address\":{\"S\":\"Calle 65 #97-50\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"CITY#MED\"},\"SortKey\":{\"S\":\"NEIGHBORHOOD#LAU\"},\"EntityType\":{\"S\":\"NEIGHBORHOOD\"},\"Code\":{\"S\":\"LAU\"},\"Name\":{\"S\":\"Laureles\"},\"ParentCode\":{\"S\":\"MED\"},\"EntityTypeKey\":{\"S\":\"NEIGHBORHOOD\"},\"EntityName\":{\"S\":\"Laureles\"},\"Address\":{\"S\":\"Av Nutibara #33\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"CITY#MED\"},\"SortKey\":{\"S\":\"NEIGHBORHOOD#POB\"},\"EntityType\":{\"S\":\"NEIGHBORHOOD\"},\"Code\":{\"S\":\"POB\"},\"Name\":{\"S\":\"El Poblado\"},\"ParentCode\":{\"S\":\"MED\"},\"EntityTypeKey\":{\"S\":\"NEIGHBORHOOD\"},\"EntityName\":{\"S\":\"El Poblado\"},\"Address\":{\"S\":\"Cra 43A #6 Sur - 26\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"DEPARTMENT#ANT\"},\"SortKey\":{\"S\":\"CITY#MED\"},\"EntityType\":{\"S\":\"CITY\"},\"Code\":{\"S\":\"MED\"},\"Name\":{\"S\":\"Medellín\"},\"ParentCode\":{\"S\":\"ANT\"},\"EntityTypeKey\":{\"S\":\"CITY\"},\"EntityName\":{\"S\":\"Medellín\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"COUNTRY#CO\"},\"SortKey\":{\"S\":\"DEPARTMENT#ANT\"},\"EntityType\":{\"S\":\"DEPARTMENT\"},\"Code\":{\"S\":\"ANT\"},\"Name\":{\"S\":\"Antioquia\"},\"ParentCode\":{\"S\":\"CO\"},\"EntityTypeKey\":{\"S\":\"DEPARTMENT\"},\"EntityName\":{\"S\":\"Antioquia\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"COUNTRY#CO\"},\"SortKey\":{\"S\":\"COUNTRY#CO\"},\"EntityType\":{\"S\":\"COUNTRY\"},\"Code\":{\"S\":\"CO\"},\"Name\":{\"S\":\"Colombia\"},\"EntityTypeKey\":{\"S\":\"COUNTRY\"},\"EntityName\":{\"S\":\"Colombia\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"PrimaryKey\":{\"S\":\"COUNTRY#CO\"},\"SortKey\":{\"S\":\"COUNTRY#CO\"},\"EntityType\":{\"S\":\"COUNTRY\"},\"Code\":{\"S\":\"CO\"},\"Name\":{\"S\":\"Colombia\"},\"EntityTypeKey\":{\"S\":\"COUNTRY\"},\"EntityName\":{\"S\":\"Colombia\"}}"
    ```

## Creación de la capa de dominio y servicios REST

1. Creamos la clase POJO WolrdRegion.java en el paquete co.com.microservicio.aws.model.worldregion para mapear todos los datos de la tabla de dynamoDB
        
    ```
    package co.com.microservicio.aws.model.worldregion;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.io.Serial;
    import java.io.Serializable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class WorldRegion implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String primaryKey;
        private String sortKey;
        private String entityType;
        private String code;
        private String name;
        private String parentCode;
        private String entityTypeKey;
        private String entityName;
        private String address;
    }
    ```

2. Primero definimos nuestros modelos de transporte, enfocándonos en crear las clases necesarias para comunicar la capa REST con el caso de uso. Estas clases contienen la información relevante para el funcionamiento interno del microservicio, así como los datos que podrían ser útiles para integrar con otros microservicios o para ser publicados como eventos en colas o buses de mensajería.

    - Creamos la clase Device.java en el paquete co.com.microservicio.aws.model.worldregion.rq 
    ```
    package co.com.microservicio.aws.model.worldregion.rq;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class Device {
        private String userAgent;
        private String platformType;
    }
    ```

    - Creamos la clase Customer.java en el paquete co.com.microservicio.aws.model.worldregion.rq 
    ```
    package co.com.microservicio.aws.model.worldregion.rq;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class Customer {
        private String ip;
        private String username;
        private Device device;
    }
    ```

    - Creamos la clase Context.java en el paquete co.com.microservicio.aws.model.worldregion.rq 
    ```
    package co.com.microservicio.aws.model.worldregion.rq;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class Context {
        private String id;
        private Customer customer;
    }
    ```

    - Creamos la clase TransactionRequest.java en el paquete co.com.microservicio.aws.model.worldregion.rq 
    ```
    package co.com.microservicio.aws.model.worldregion.rq;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class TransactionRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient Context context;
        private transient String request;
    }
    ```

    **Nota:** Utilizamos la palabra clave transient para excluir ciertos campos del proceso de serialización. Esto significa que, al convertir un objeto en un flujo de bytes (por ejemplo, para enviarlo por red, almacenarlo en un archivo o en caché), los campos marcados como transient no se incluirán. Aunque estos datos pueden ser útiles para enviar a una cola o integrarse con otros sistemas, en esos casos se deberá construir un objeto específico para tal propósito. En el contexto de este requerimiento, no es necesario serializar dichos campos, ya que contienen información sensible o estrictamente técnica que no debe persistirse ni exponerse

3. Ahora creamos nuestra clase de respuesta la cual contiene información del proceso y resultado esperado.

    - Creamos la clase WorldRegionResponse.java en el paquete co.com.microservicio.aws.model.worldregion.rs
    ```
    package co.com.microservicio.aws.model.worldregion.rs;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class WorldRegionResponse implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        
        private String code;
        private String name;
    }
    ```

    - Creamos la clase TransactionResponse.java en el paquete co.com.microservicio.aws.model.worldregion.rs
    ```
    package co.com.microservicio.aws.model.worldregion.rs;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;
    import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class TransactionResponse implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String message;
        private String size;
        private List<WorldRegionResponse> response;
    }
    ```

    - Creamos la clase WorldRegionType.java en el paquete co.com.microservicio.aws.model.worldregion.util
    ```
    package co.com.microservicio.aws.model.worldregion.util;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class WorldRegionType {
        public static final String COUNTRY = "COUNTRY";
        public static final String DEPARTMENT = "DEPARTMENT";
        public static final String CITY = "CITY";
        public static final String NEIGHBORHOOD = "NEIGHBORHOOD";
        public static final String UNIT = "UNIT";
    }
    ```

4. Creamos la clase WorldRegionRepository.java en el paquete co.com.microservicio.aws.model.worldregion.gateway
    ```
    package co.com.microservicio.aws.model.worldregion.gateway;

    import co.com.microservicio.aws.model.worldregion.WorldRegion;
    import reactor.core.publisher.Flux;

    public interface WorldRegionRepository {

        Flux<WorldRegion> findByEntityType(String entityType);

        Flux<WorldRegion> findByParentCodeAndEntityType(String parentCode, String entityType);
    }
    ```

5. Creamos la clase WorldRegionUseCase.java en el paquete co.com.microservicio.aws.usecase.worldregion para cumplir con los criterios de aceptación; hacemos una validación del user-name para aplicar metodos webflux y lanzar errores
    ```
    package co.com.microservicio.aws.usecase.worldregion;

    import co.com.microservicio.aws.model.worldregion.WorldRegion;
    import co.com.microservicio.aws.model.worldregion.gateway.WorldRegionRepository;
    import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
    import co.com.microservicio.aws.model.worldregion.rq.Context;
    import co.com.microservicio.aws.model.worldregion.rq.Customer;
    import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
    import co.com.microservicio.aws.model.worldregion.rs.WorldRegionResponse;
    import co.com.microservicio.aws.model.worldregion.util.WorldRegionType;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.List;
    import java.util.Optional;

    @RequiredArgsConstructor
    public class WorldRegionUseCase {
        private static final String KEY_USER_NAME = "user-name";
        private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

        private final WorldRegionRepository regionRepository;

        public Mono<TransactionResponse> listAllCountries(TransactionRequest request){
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> regionRepository.findByEntityType(WorldRegionType.COUNTRY)
                        .collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.error(new IllegalStateException(
                        String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                    .map(TransactionRequest::getContext)
                    .map(Context::getCustomer)
                    .map(Customer::getUsername)
                    .filter(username -> !username.isEmpty())
                    .isPresent();
        }

        private Mono<TransactionResponse> buildResponse(List<WorldRegion> worldRegions){
            var simplifiedList = worldRegions.stream()
                .map(wr -> WorldRegionResponse.builder()
                    .code(wr.getCode())
                    .name(wr.getName())
                    .build())
                .toList();

            TransactionResponse response = TransactionResponse.builder()
                    .message("countries listed successfull")
                    .size(worldRegions.size())
                    .response(simplifiedList)
                    .build();

            return Mono.just(response);
        }
    }
    ```

6. Instalar helper básico
    
    - Commons: Contiene información sobre: Estructura para extracción de información de los headers, manipulación de fechas, entre otros.

    Ejecutar en el directorio que contiene el build.gradle general
    ```
    gradle generateHelper --name=commons
    ```

7. Creamos la clase HeadersUtil.java en el paquete co.com.microservicio.aws.commons
    ```
    package co.com.microservicio.aws.commons;

    import java.util.LinkedHashMap;
    import java.util.Map;
    import java.util.regex.Pattern;

    import lombok.experimental.UtilityClass;

    @UtilityClass
    public class HeadersUtil {

        private static final String CHARS_TO_CLEAR = "<>(;|'";
        private static final String REGEXP_CHARS_TO_CLEAR = "[" + CHARS_TO_CLEAR + "]";
        private static final Pattern PATTERN_CHARS_TO_CLEAR = Pattern.compile(REGEXP_CHARS_TO_CLEAR);

        public static Map<String, String> clearChars(Map<String, String> headers) {
            var localHeaders = new LinkedHashMap<String, String>();
            if (null != headers && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    localHeaders.put(entry.getKey(), PATTERN_CHARS_TO_CLEAR.matcher(entry.getValue()).replaceAll(" "));
                }
            }
            return localHeaders;
        }
    }
    ```

8. Creamos la clase ContextUtil.java en el paquete co.com.microservicio.aws.commons
    ```
    package co.com.microservicio.aws.commons;

    import co.com.microservicio.aws.model.worldregion.rq.Context;
    import co.com.microservicio.aws.model.worldregion.rq.Customer;
    import co.com.microservicio.aws.model.worldregion.rq.Device;
    import lombok.experimental.UtilityClass;

    import java.util.Map;
    import java.util.Optional;

    @UtilityClass
    public class ContextUtil {
        private static final String EMPTY_VALUE = "";

        public static Context buildContext(Map<String, String> headers) {
            var localHeaders = HeadersUtil.clearChars(headers);
            return Context.builder().id(Optional.ofNullable(localHeaders.get("message-id")).orElse(EMPTY_VALUE))
                    .customer(buildCustomer(localHeaders)).build();
        }

        private static Customer buildCustomer(Map<String, String> headers) {
            return Customer.builder().ip(Optional.ofNullable(headers.get("ip")).orElse(EMPTY_VALUE))
                    .username(Optional.ofNullable(headers.get("username")).orElse(EMPTY_VALUE))
                    .device(buildDevice(headers)).build();
        }

        private static Device buildDevice(Map<String, String> headers) {
            return Device.builder().userAgent(Optional.ofNullable(headers.get("user-agent")).orElse(EMPTY_VALUE))
                    .platformType(Optional.ofNullable(headers.get("platform-type")).orElse(EMPTY_VALUE)).build();
        }
    }
    ```

9. Modificamos el build.gradle de la aplicación infrastructure > entry-points para agregar la dependencia de helpers > commons
    ```
    implementation project(':commons')
    ``` 

10. Creamos la clase WorldRegionHandler.java en el paquete co.com.microservicio.aws.api.worldregion
    ```
    package co.com.microservicio.aws.api.worldregion;

    import co.com.microservicio.aws.commons.ContextUtil;
    import co.com.microservicio.aws.log.LoggerBuilder;
    import co.com.microservicio.aws.log.TransactionLog;
    import co.com.microservicio.aws.model.worldregion.rq.Context;
    import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
    import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
    import co.com.microservicio.aws.usecase.worldregion.WorldRegionUseCase;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class WorldRegionHandler {
        private static final String NAME_CLASS = WorldRegionHandler.class.getName();
        private static final String MESSAGE_SERVICE = "Service Api Rest world regions";

        private final LoggerBuilder logger;
        private final WorldRegionUseCase worldRegionUseCase;

        public Mono<ServerResponse> listAllCountries(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, "List all countries");

            var request = TransactionRequest.builder().context(context).build();
            return ServerResponse.ok().body(worldRegionUseCase.listAllCountries(request)
                    .onErrorResume(e -> this.printFailed(e, context.getId())), TransactionResponse.class
            );
        }

        private Mono<TransactionResponse> printFailed(Throwable throwable, String messageId) {
            logger.error(throwable.getMessage(), messageId, MESSAGE_SERVICE, NAME_CLASS);
            return Mono.empty();
        }

        private void printOnProcess(Context context, String messageInfo){
            logger.info(TransactionLog.Request.builder().body(context).build(), null,
                    messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
        }
    }
    ``` 

11. Modificamos el archivo application.yaml para agregar las nuevas rutas, debe quedar así:

```
entries:
  reactive-web:
    path-base: "${PATH_BASE:/api/v1/microservice-aws}"
    greet: "/greet"
    greetReactive: "/greetReactive"
  world-region-web:
    path-base: "${PATH_BASE:/api/v1/microservice-aws}"
    listCountries: "/list-countries"
    listDepartaments: "/list-departaments"
    listCities: "/list-cities"
    listNeighborhoods: "/list-neighborhoods"
``` 

12. Creamos la clase ApiWorldRegionProperties.java en el paquete co.com.microservicio.aws.api.worldregion.config
    ```
    package co.com.microservicio.aws.api.worldregion.config;

    import lombok.Data;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;

    @Data
    @Component
    @ConfigurationProperties(prefix = "entries.world-region-web")
    public class ApiWorldRegionProperties {
        private String pathBase;
        private String listCountries;
        private String listDepartaments;
        private String listCities;
        private String listNeighborhoods;
    }
    ``` 

13. Creamos la clase WorldRegionOpenAPI en el paquete co.com.microservicio.aws.api.worldregion.doc
    ```
    package co.com.microservicio.aws.api.worldregion.doc;

    import lombok.experimental.UtilityClass;
    import org.springdoc.core.fn.builders.operation.Builder;

    import java.util.function.Consumer;

    import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
    import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
    import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
    import static org.springframework.http.HttpStatus.*;
    import static org.springframework.http.MediaType.APPLICATION_JSON;

    @UtilityClass
    public class WorldRegionOpenAPI {

        private static final String OPERATION_ID = "Greet";
        private static final String DESCRIPTION = "Retrieve information of a world regions";
        private static final String DESCRIPTION_OK = "When the response has status 200";
        private static final String DESCRIPTION_CONFLICT = "When the request fails";
        private static final String DESCRIPTION_ERROR = "Internal server error";
        private static final String TAG = "Payments";

        public static Consumer<Builder> greetRoute() {
            return ops -> ops
                    .operationId(OPERATION_ID)
                    .description(DESCRIPTION)
                    .tag(TAG)
                    .summary(OPERATION_ID)
                    .response(responseOk())
                    .response(responseBusiness())
                    .response(responseError())
                    .response(responseNotFound())
                    .response(responseBadRequest());
        }

        public static org.springdoc.core.fn.builders.apiresponse.Builder responseOk(){
            return responseBuilder().
                    responseCode(String.valueOf(OK.value()))
                    .description(DESCRIPTION_OK)
                    .content(contentBuilder()
                            .mediaType(APPLICATION_JSON.toString())
                            .schema(schemaBuilder()
                                    .implementation(String.class)));
        }

        public static org.springdoc.core.fn.builders.apiresponse.Builder responseBusiness(){
            return responseBuilder()
                    .responseCode(String.valueOf(CONFLICT.value()))
                    .description(DESCRIPTION_CONFLICT)
                    .implementation(Error.class);
        }

        public static org.springdoc.core.fn.builders.apiresponse.Builder responseError(){
            return responseBuilder()
                    .responseCode(String.valueOf(INTERNAL_SERVER_ERROR.value()))
                    .description(DESCRIPTION_ERROR)
                    .implementation(Error.class);
        }

        public static org.springdoc.core.fn.builders.apiresponse.Builder responseNotFound(){
            return responseBuilder()
                    .responseCode(String.valueOf(NOT_FOUND.value()))
                    .description(NOT_FOUND.getReasonPhrase())
                    .implementation(Error.class);
        }

        public static org.springdoc.core.fn.builders.apiresponse.Builder responseBadRequest() {
            return responseBuilder()
                    .responseCode(String.valueOf(BAD_REQUEST.value()))
                    .description(BAD_REQUEST.getReasonPhrase())
                    .implementation(Error.class);
        }

    }
    ```

14. Creamos la clase WorldRegionRouterRest.java en el paquete co.com.microservicio.aws.api.worldregion
    ```
    package co.com.microservicio.aws.api.worldregion;

    import co.com.microservicio.aws.api.greet.doc.GreetOpenAPI;
    import co.com.microservicio.aws.api.worldregion.config.ApiProperties;
    import lombok.RequiredArgsConstructor;
    import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class WorldRegionRouterRest {
        private final ApiWorldRegionProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerWorldRegionFunction(WorldRegionHandler worldRegionHandler) {
            return SpringdocRouteBuilder.route()
                    .GET(properties.getPathBase().concat(properties.getListCountries()),
                            worldRegionHandler::listAllCountries, GreetOpenAPI.greetRoute())
                    .build();
        }
    }
    ```

## Creación del driver de conexión con dynamoDB

1. Creamos la conexión con DynamoDB para implementar la interfaz de conexión entre el caso de uso y la infrastructura de conexión con DynamoDB
    
    - Ubicarse en la raiz del proyecto, abrir la consola de comandos y ejecutar el comando de creación del driven-adapter con DynamoDB
   ```
   gradle generateDrivenAdapter --type=dynamodb
   ``` 

   ![](./img/apirest-crear-driven-adapter-dynamodb.png)

2. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase

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

3. En el paquete 'co.com.microservicio.aws.dynamodb.config' creamos la siguiente clase
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class SourceName {
        public static final String FLIGHT_TICKETS = "flight_tickets";
    }
    ```

4. En el paquete 'co.com.microservicio.aws.dynamodb.model' creamos la siguiente clase de acuerdo a los datos a almacenar en la tabla
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
    @DynamoDbTableAdapter(tableName = SourceName.WORLD_REGIONS) // asegúrate que exista esta constante
    public class ModelEntityWorldRegion {

        @Getter(onMethod_ = @DynamoDbPartitionKey)
        private String primaryKey;

        @Getter(onMethod_ = @DynamoDbSortKey)
        private String sortKey;

        @Getter(onMethod_ = @DynamoDbSecondaryPartitionKey(indexNames = "EntityTypeIndex"))
        private String entityTypeKey;

        private String code;
        private String name;
        private String parentCode;
        private String entityName;
        private String address;
    }
    ```

5. Cambiamos la clase DynamoDBConfig en el paquete 'co.com.microservicio.aws.dynamodb.config' por el siguiente código
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

6. Cambiamos la clase DynamoDBConfigTest en el paquete 'co.com.microservicio.aws.dynamodb.config' por el siguiente código
    ```
    package co.com.microservicio.aws.dynamodb.config;

    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
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

7. En el archivo build.gradle del proyecto dynamo-db colocamos las siguientes dependencias
    ```
    dependencies {
        implementation project(':model')
        implementation 'org.springframework:spring-context'
        implementation 'software.amazon.awssdk:dynamodb-enhanced'
        implementation 'org.reactivecommons.utils:object-mapper-api:0.1.0'
        implementation 'org.springframework.boot:spring-boot-starter-validation'
        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        implementation 'org.mapstruct:mapstruct:1.3.1.Final'
        implementation "com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}"

        annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
        annotationProcessor 'org.mapstruct:mapstruct-processor:1.3.1.Final'

        testImplementation 'org.reactivecommons.utils:object-mapper:0.1.0'
    }
    ```

8. En el paquete 'co.com.microservicio.aws.dynamodb.config' creamos la siguiente clase
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


9. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase
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

10. En el paquete 'co.com.microservicio.aws.dynamodb.mapper' creamos la siguiente clase a cargo de mapear los datos de la clase DTO a la clase Entity o viceversa
    ```
    package co.com.microservicio.aws.dynamodb.mapper;

    import co.com.microservicio.aws.dynamodb.model.ModelEntityWorldRegion;
    import co.com.microservicio.aws.model.worldregion.WorldRegion;
    import org.mapstruct.Mapper;
    import org.mapstruct.ReportingPolicy;

    @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
    public interface WorldRegionDataMapper {
        ModelEntityWorldRegion toData(WorldRegion worldRegion);
        WorldRegion toEntity(ModelEntityWorldRegion modelEntityWorldRegion);
    }
    ```

11. Eliminamos las clases autogeneradas: TemplateAdapterOperationsTest, DynamoDBTemplateAdapter

12. Creamos la clase WorldRegionRepositoryAdapter.java quien implementará la interfaz Gateway en el paquete 'co.com.microservicio.aws.dynamodb.mapper'
    ```

    ```



13. En el paquete 'co.com.microservicio.aws.dynamodb' creamos la siguiente clase correspondiente al adaptador que implementa la consulta en la bd
    ```

    ```



## API Rest con anotaciones



[< Volver](README-PROYECTO-JAVA-WEBFLUX.md)

---

**Author**: Pedro Luis Osorio Pavas [Linkedin](www.linkedin.com/in/pedro-luis-osorio-pavas-68b3a7106)  
**Start Date**: 01-06-2025  
**Update Date**: 01-06-2025.