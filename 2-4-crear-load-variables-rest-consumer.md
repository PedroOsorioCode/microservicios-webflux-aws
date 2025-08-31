# Creación microservicio APIREST Java Webflux con DynamoDB - Estrategia de parámetros

> A continuación se indica el paso a paso que se debe realizar para continuar con el proyecto de creación de microservicios basados en la nube de AWS, esta guía comprende el uso de variables configurados en el proyecto, consumo servicio api rest y  mocks de api rest

### Requisitos: 

⚠️ Debes haber realizado el instructivo de ambiente local para comprender los comandos que usaremos<br>
[Ver documentación ambiente local](./1-1-podman-localstack-aws.md)

⚠️ Debes haber comprendido el funcionamiento de Mocks de servicios API <br>
[Ver documentación Wiremock](./1-2-4-wiremock-apirest.md)

⚠️ Debes haber realizado el instructivo Api REST Crud DynamoDB<br>
[Realizar instructivo](./2-3-1-crear-api-rest-informar-errores.md)

## Caso de uso:
- Parametrización de reglas local, consumo apirest
- Auditar cada Metodo en el API Rest

## Criterios de aceptación:
- Configurar parámetros en la aplicación
- Leer parámetros de un sistema externo
- Imprimir logs auditoría por cada metodo creado

## A continuación se proponen diferentes situaciones a modo de estudio

> Vamos a suponer que necesitamos auditar que usuario realiza alguna de las acciones en las api que tenemos (listar, crear, obtener uno, actualizar y borrar), para lograr esto vamos a usar un parámetro de tal forma de que si está activo se guarda auditoría, sino, entonces no se guarda; para ver alternativas de parametrización vamos a hacerlo de forma diferente para cada metodo (Listar, Crear, Borrar).

## Recursos base:

1. Agregar nuevas variables en el application-local.yaml

- Ubicarse en el proyecto application > app-service en la carpeta de resources modificamos el archivo application-local.yaml para agregar
```
audit:
  world-region:
    audit-on-list: ${APPLY_AUDIT_LIST_WR:true}

adapters:
  dynamodb:
    endpoint: "http://localhost:4566"
  repositories:
    tables:
      namesmap:
        world_region: local_worldregions
  rest-audit:
    timeout: ${TIMEOUT:5000}
    url: ${PARAM_URL:http://localhost:3000/api/v3/microservice-param}
    parameter:
      nameAuditOnSave: "${PARAM_NAME_ONSAVE:/auditOnSave}"
      nameAuditOnUpdate: "${PARAM_NAME_ONUPDATE:/auditOnUpdate}"
    retry:
      retries: ${REST_AUDIT_RETRIES:3}
      retryDelay: ${REST_AUDIT_RETRY_DELAY:2}
 ```

2. Crear proyecto de carga de variables para llevar estas hasta los casos de uso a través de puertos y adaptadores

    - Ubicarse en la raíz del proyecto, abrir la consola de comandos y ejecutar
        ```
        gradle generateEntryPoint --type=generic --name load-variables
        ```

    - Ubicarse en el proyecto infrastructure > entry-points > load-variables y modificar el archivo build.gradle
        ```
        dependencies {
            implementation project(':model')
            implementation "org.springframework.boot:spring-boot-starter-webflux:${springBootVersion}"
            implementation "org.springframework.boot:spring-boot-starter-validation:${springBootVersion}"
        }
        ```
        Refrescar dependencias

    - Ubicarse en el proyecto infrastructure > entry-points > load-variables en el paquete co.com.microservicio.aws.loadvariables.properties y crear la clase ApplyAuditListProperties.java
        ```
        package co.com.microservicio.aws.loadvariables.properties;

        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Configuration
        @ConfigurationProperties(prefix = "audit.world-region")
        public class AuditConfigProperties {
            private boolean auditOnList;
        }
        ```

3. Crear proyecto de eventos rabbit para conectar con el servicio AWS y aprovechar la comunicación asincrona por mensajería: Para este caso y d emomento planearemos una solución de impresión de logs en consola

    - Ubicarse en la raíz del proyecto, abrir la consola de comandos y ejecutar
        ```
        gradle generateDrivenAdapter --type=generic --name async-event-bus
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > secrets y modificar el archivo build.gradle
        ```
        dependencies {
            implementation project(':model')
            implementation project(':log')
            implementation "org.springframework.boot:spring-boot-starter-webflux:${springBootVersion}"
            implementation "org.springframework.boot:spring-boot-starter-validation:${springBootVersion}"
        }
        ```
        Refrescar dependencias

4. Crear proyecto de consumo de servicio REST para obtener el parámetro de aplicar auditoría al guardar, en este caso haremos uso de un proveedor de respuestas de servicios REST (wiremock)

    - Ubicarse en la raíz del proyecto, abrir la consola de comandos y ejecutar
        ```
        gradle generateDrivenAdapter --type=generic --name rest-consumer
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > secrets y modificar el archivo build.gradle
        ```
        dependencies {
            implementation project(':model')
            implementation project(':log')
            implementation "org.springframework.boot:spring-boot-starter-webflux:${springBootVersion}"
            implementation "org.springframework.boot:spring-boot-starter-validation:${springBootVersion}"
        }
        ```
        Refrescar dependencias

## Implementación casos de uso

1. Parametrización por aplicación (Metodo Listar): 

    - ¿Por qué elegir esta opción?

        > Si bien parametrizar en un sistema externo podría ofrecer una solución más flexible a largo plazo, en algunos casos esto implica mayor esfuerzo de desarrollo o depender de servicios que aún no están disponibles. Por ello, y en línea con el principio de agilidad que buscamos en los microservicios, optamos por parametrizar directamente en el microservicio, lo cual permite avanzar rápidamente sin comprometer la estabilidad ni la configuración por entorno.

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.variables.gateway y crear la clase LoadVariablesGateway.java
        ```
        public interface LoadVariablesGateway {
            boolean isAuditOnList();
        }
        ```

    - Ubicarse en el proyecto infrastructure > entry-points > load-variables en el paquete co.com.microservicio.aws.loadvariables y crear la clase LoadVariablesAdapter.java
        ```
        package co.com.microservicio.aws.loadvariables;

        import co.com.microservicio.aws.loadvariables.properties.AuditConfigProperties;
        import co.com.microservicio.aws.variables.gateway.LoadVariablesGateway;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Component;

        @Component
        @RequiredArgsConstructor
        public class LoadVariablesAdapter implements LoadVariablesGateway {
            private final AuditConfigProperties auditConfigProperties;

            @Override
            public boolean isAuditOnList() {
                return auditConfigProperties.isAuditOnList();
            }
        }
        ```

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.event.gateway y crear la clase EventGateway.java 
        ```
        package co.com.microservicio.aws.event.gateway;

        import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
        import reactor.core.publisher.Mono;

        public interface EventGateway {
            Mono<Void> emitEvent(TransactionRequest request);
        }
        ```

    - Ubicarse en el proyecto infrastructura > driven-adapters > asyn-event-bus en el paquete co.com.microservicio.aws.asynceventbus y crear la clase ReactiveEventsAdapter.java 
        ```
        package co.com.microservicio.aws.asynceventbus;

        import co.com.microservicio.aws.event.gateway.EventGateway;
        import co.com.microservicio.aws.log.LoggerBuilder;
        import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Component;
        import reactor.core.publisher.Mono;

        @Component
        @RequiredArgsConstructor
        public class ReactiveEventsAdapter implements EventGateway {
            private final LoggerBuilder logger;

            @Override
            public Mono<Void> emitEvent(TransactionRequest request) {
                logger.info(request.getContext().getCustomer().getUsername(),
                        request.getContext().getId(), "audit on list", "emitEvent");
                return Mono.empty();
            }
        }
        ```

    - Ubicarse en el proyecto domain > usecase en el paquete co.com.microservicio.aws.usecase.sentevent y crear la clase SentEventUseCase.java 
        ```
        package co.com.microservicio.aws.usecase.sentevent;

        import co.com.microservicio.aws.event.gateway.EventGateway;
        import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
        import co.com.microservicio.aws.variables.gateway.LoadVariablesGateway;
        import lombok.RequiredArgsConstructor;

        @RequiredArgsConstructor
        public class SentEventUseCase {
            private final LoadVariablesGateway loadVariablesGateway;
            private final EventGateway eventGateway;

            public void sendAudit(TransactionRequest request){
                if (loadVariablesGateway.isAuditOnList()){
                    eventGateway.emitEvent(request);
                }
            }
        }
        ```

    - Ubicarse en el proyecto domain > usecase en el paquete co.com.microservicio.aws.usecase.worldregion y modificar la clase WorldRegionUseCase.java en el metodo listByRegion para incluir un llamado a la auditoría de tal forma que si falla no dañe el flujo y continúe.
        ```
        private final SentEventUseCase sentEventUseCase;

        public Mono<TransactionResponse> listByRegion(TransactionRequest request){
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> regionRepository.findByRegion(buildKeyRegion(req))
                    .collectList().flatMap(this::buildResponse))
                .doOnNext(res -> sentEventUseCase.sendAudit(request))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }
        ```
    
    - Realizar prueba

        ![](./img/wr-list-all-countries-with-audit.png)

        Logs: efectivamente se imprimió la auditoría del usuario que ejecutó la acción de listar

        ```
        {
            "instant": {
                "epochSecond": 1751424690,
                "nanoOfSecond": 448444600
            },
            "thread": "reactor-http-nio-3",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"List all by region\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservicio.aws.api.worldregion.WorldRegionHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"\",\"username\":\"peter name\",\"device\":{\"userAgent\":\"\",\"platformType\":\"\"}}}},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 58,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751424690,
                "nanoOfSecond": 749513000
            },
            "thread": "sdk-async-response-1-0",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"peter name\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"audit on list\",\"method\":\"emitEvent\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 67,
            "threadPriority": 5
        }
        ```

    2. Parametrización consumiendo API Rest externo (Metodo Guardar): 

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer.config y crear la clase RestConsumerUtils.java
        ```
        package co.com.microservicio.aws.restconsumer.config;

        import io.netty.handler.timeout.ReadTimeoutHandler;
        import io.netty.handler.timeout.WriteTimeoutHandler;
        import lombok.experimental.UtilityClass;
        import org.springframework.http.client.reactive.ClientHttpConnector;
        import org.springframework.http.client.reactive.ReactorClientHttpConnector;
        import reactor.netty.http.client.HttpClient;

        import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
        import static java.util.concurrent.TimeUnit.MILLISECONDS;

        @UtilityClass
        public class RestConsumerUtils {
            public static ClientHttpConnector getClientHttpConnector(Long timeout) {
                return new ReactorClientHttpConnector(HttpClient.create()
                    .compress(true)
                    .keepAlive(true)
                    .option(CONNECT_TIMEOUT_MILLIS, timeout.intValue())
                    .doOnConnected(connection -> {
                        connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                        connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                    }));
            }
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer.config y crear la clase RestConsumerConfig.java
        ```
        package co.com.microservicio.aws.restconsumer.config;

        import co.com.microservicio.aws.restconsumer.properties.RestConsumerProperties;
        import lombok.RequiredArgsConstructor;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.MediaType;
        import org.springframework.web.reactive.function.client.WebClient;

        import static co.com.microservicio.aws.restconsumer.config.RestConsumerUtils.getClientHttpConnector;

        @Configuration
        @RequiredArgsConstructor
        public class RestConsumerConfig {
            private final RestConsumerProperties properties;

            @Bean(name = "webClientConfig")
            public WebClient webClientConfig() {
                return WebClient.builder()
                        .baseUrl(properties.getUrl())
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .clientConnector(getClientHttpConnector((long) properties.getTimeout()))
                        .build();
            }
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer.properties y crear la clase ParamProperties.java
        ```
        package co.com.microservicio.aws.restconsumer.properties;

        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Configuration
        @ConfigurationProperties(prefix = "adapters.rest-audit.parameter")
        public class ParamProperties {
            private String nameAuditOnSave;
            private String nameAuditOnUpdate;
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer.properties y crear la clase RestConsumerProperties.java
        ```
        package co.com.microservicio.aws.restconsumer.properties;

        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Configuration
        @ConfigurationProperties(prefix = "adapters.rest-audit")
        public class RestConsumerProperties {
            private String url;
            private int timeout;
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer.properties y crear la clase RetryProperties.java
        ```
        package co.com.microservicio.aws.restconsumer.properties;

        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.springframework.boot.context.properties.ConfigurationProperties;
        import org.springframework.context.annotation.Configuration;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Configuration
        @ConfigurationProperties(prefix = "adapters.rest-audit.retry")
        public class RetryProperties {
            private int retries;
            private int retryDelay;
        }
        ```

    - Ubicarse en el proyecto domain > model en el paquete co.com.microservicio.aws.restconsumer.gateway y crear la clase ParameterGateway.java
        ```
        package co.com.microservicio.aws.restconsumer.gateway;

        import co.com.microservicio.aws.model.worldregion.rq.Context;
        import reactor.core.publisher.Mono;

        public interface ParameterGateway {
            Mono<Boolean> isAuditOnSave(Context context);
        }
        ```

    - Ubicarse en el proyecto helpers > log en el paquete co.com.microservicio.aws.log y modificar la clase LoggerBuilder.java agregando el metodo
        ```
        public void error(Throwable throwable) {
            log.error("throwable: " + throwable);
        }
        ```

    - Ubicarse en el proyecto infrastructure > driven-adapters > rest-consumer en el paquete co.com.microservicio.aws.restconsumer y crear la clase ParameterService.java
        ```
        package co.com.microservicio.aws.restconsumer;

        import co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage;
        import co.com.microservicio.aws.commons.exceptions.TechnicalException;
        import co.com.microservicio.aws.log.LoggerBuilder;
        import co.com.microservicio.aws.log.TransactionLog;
        import co.com.microservicio.aws.restconsumer.gateway.ParameterGateway;
        import co.com.microservicio.aws.restconsumer.properties.ParamProperties;
        import co.com.microservicio.aws.restconsumer.properties.RetryProperties;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.http.MediaType;
        import org.springframework.stereotype.Service;
        import org.springframework.web.reactive.function.client.ClientResponse;
        import org.springframework.web.reactive.function.client.WebClient;
        import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
        import reactor.core.publisher.Mono;
        import co.com.microservicio.aws.model.worldregion.rq.Context;
        import org.springframework.http.HttpStatusCode;
        import reactor.util.retry.Retry;

        import java.time.Duration;
        import java.util.Objects;

        import static co.com.microservicio.aws.model.worldregion.util.LogMessage.MESSAGE_SERVICE;

        @Service
        public class ParameterService implements ParameterGateway {
            private static final String NAME_CLASS = ParameterService.class.getName();
            private static final String AUDIT_ON_LIST_TRUE = "1";
            private final WebClient webClientConfig;
            private final ParamProperties paramProperties;
            private final RetryProperties retryProperties;
            private final LoggerBuilder logger;

            public ParameterService(@Qualifier(value = "webClientConfig") WebClient webClientConfig,
                                    ParamProperties paramProperties, LoggerBuilder loggerBuilder,
                                    RetryProperties retryProperties){
                this.webClientConfig = webClientConfig.mutate().build();
                this.paramProperties = paramProperties;
                this.logger = loggerBuilder;
                this.retryProperties = retryProperties;
            }

            @Override
            public Mono<Boolean> isAuditOnSave(Context context) {
                logger.info("rest get parameter", context.getId(), NAME_CLASS, "isAuditOnList");
                return this.getParameter(context, paramProperties.getNameAuditOnSave());
            }

            private Mono<Boolean> getParameter(Context context, String urlPath) {
                return this.buildGetRequestWithHeaders(context, urlPath)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> this.errorStatusFunction(res, context))
                    .bodyToMono(Parameter.class)
                    .retryWhen(Retry
                        .fixedDelay(retryProperties.getRetries(), Duration.ofSeconds(retryProperties.getRetryDelay()))
                        .doBeforeRetry(signal -> this.printErrorRetry(signal, context))
                        .doAfterRetry(retrySignal -> logger.info("Retry: " + (retrySignal.totalRetries() + 1), context.getId(), NAME_CLASS, "isAuditOnList")))
                    .doOnNext(paramres -> this.printOnProcess(context, paramres))
                    .doOnError(logger::error)
                    .flatMap(this::createResponse);
            }

            private Mono<Boolean> createResponse(Parameter result) {
                return !Objects.isNull(result) && !result.getValue().isEmpty()
                        && result.getValue().equalsIgnoreCase(AUDIT_ON_LIST_TRUE) ?
                        Mono.just(Boolean.TRUE): Mono.just(Boolean.FALSE);
            }

            private RequestHeadersSpec<?> buildGetRequestWithHeaders(Context context, String urlPath) {
                return webClientConfig.get().uri(urlPath)
                        .header("message-id", context.getId())
                        .header("ip", context.getCustomer().getIp())
                        .header("user-name", context.getCustomer().getUsername());
            }

            private void printErrorRetry(Retry.RetrySignal retrySignal, Context context){
                var messageInfo = String.format("%s, wating retry: '%s'",
                        TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR.getDescription(),
                        (retrySignal.totalRetries() + 1));
                logger.error(messageInfo, context.getId(), NAME_CLASS, "printErrorRetry");
                logger.error(retrySignal.failure());
            }

            private Mono<Throwable> errorStatusFunction(ClientResponse response, Context context) {
                var messageInfo = String.format("rest get parameter %s", response.statusCode());
                logger.error(messageInfo, context.getId(), NAME_CLASS, "errorStatusFunction");
                return response.bodyToMono(String.class).switchIfEmpty(Mono.just(response.statusCode().toString()))
                        .map(msg -> new TechnicalException(new RuntimeException(msg),
                                TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR));
            }

            private void printOnProcess(Context context, Parameter parameter){
                logger.info(TransactionLog.Request.builder().body(context).build(),
                        TransactionLog.Response.builder().body(parameter).build(),
                        context.getId(), context.getId(), MESSAGE_SERVICE, NAME_CLASS);
            }
        }
        ```

        | Línea de código | Explicación breve |
        |-----------------|------------------|
        | `defineHeaders(context)` | Construye el `WebClient.RequestHeadersSpec` con headers personalizados para el contexto |
        | `.accept(MediaType.APPLICATION_JSON)` | Agrega header `Accept: application/json` |
        | `.retrieve()` | Inicia el procesamiento de la respuesta del `WebClient` |
        | `.onStatus(HttpStatusCode::isError, ...)` | Si la respuesta HTTP tiene un código 4xx o 5xx, se lanza un error controlado |
        | `.bodyToMono(Parameter.class)` | Convierte el cuerpo de la respuesta a un `Mono<Parameter>` |
        | `.retryWhen(...)` | Reintenta la llamada si falla (por excepción), según una política definida |
        | `Retry.fixedDelay(...)` | Número de reintentos y tiempo de espera fijo entre intentos fallidos (por ejemplo, 3 reintentos cada 2 segundos) |
        | `.doBeforeRetry(...)` | Acción a ejecutar antes de cada reintento (ej: loguear el error del intento anterior) |
        | `.doAfterRetry(...)` | Acción a ejecutar después de que se ha planificado el reintento (ej: loguear intento actual) |
        | `.doOnNext(...)` | Acción adicional cuando llega una respuesta exitosa (ej: imprimir logs del parámetro recibido) |
        | `.doOnError(...)` | Loguea cualquier error que no haya sido capturado anteriormente |
        | `.flatMap(this::createResponse)` | Transforma el resultado a un `Mono<Boolean>` final según la lógica de negocio |
        | `.option(CONNECT_TIMEOUT_MILLIS, timeout.intValue())` | Define cuánto tiempo máximo se esperará por la respuesta del servidor antes de lanzar TimeoutException |

    - Ubicarse en el proyecto domain > usecase en el paquete co.com.microservicio.aws.usecase.restconsumer y crear la clase RestParameterUseCase.java
        ```
        package co.com.microservicio.aws.usecase.restconsumer;

        import co.com.microservicio.aws.model.worldregion.rq.Context;
        import co.com.microservicio.aws.restconsumer.gateway.ParameterGateway;
        import lombok.RequiredArgsConstructor;
        import reactor.core.publisher.Mono;

        @RequiredArgsConstructor
        public class RestParameterUseCase {
            private final ParameterGateway parameterGateway;

            public Mono<Boolean> getParameterAuditOnSave(Context context){
                return parameterGateway.isAuditOnSave(context)
                    .onErrorResume(ex -> Mono.just(Boolean.FALSE));
            }
        }
        ```

    - Ubicarse en el proyecto domain > usecase en el paquete co.com.microservicio.aws.usecase.worldregion y modificar la clase WorldRegionUseCase.java en el metodo save
        ```
        public Mono<String> save(TransactionRequest request){
                return Mono.just(request)
                        .filter(this::userIsRequired)
                        .map(TransactionRequest::getItem)
                        .map(wr -> WorldRegion
                                .builder()
                                .region(wr.getRegion().toUpperCase())
                                .name(wr.getName())
                                .code(UUID.randomUUID().toString())
                                .codeRegion(wr.getCodeRegion().toUpperCase())
                                .creationDate(new Date().toString()).build())
                        .flatMap(regionRepository::save)
                        .flatMap(res -> restParameterUseCase.getParameterAuditOnSave(request.getContext()))
                        .doOnNext(res -> sentEventUseCase.sendAuditSave(request, res))
                        .thenReturn(WorldRegionConstant.MSG_SAVED_SUCCESS);
        }
        ```

    - Realizar prueba

        Logs: se imprimé que guardó el registro y que se reintentó la petición hasta 3 veces cada 2 segundos, pero no se interrumpió el guardar el registro debido a que se controla el error y se emite un valor de FALSE para indicar que no debe guardar auditoría

        ```
        {
            "instant": {
                "epochSecond": 1751762127,
                "nanoOfSecond": 510193500
            },
            "thread": "reactor-http-nio-3",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"Save one world region\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservicio.aws.api.worldregion.WorldRegionHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"\",\"username\":\"user dummy\",\"device\":{\"userAgent\":\"\",\"platformType\":\"\"}}}},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 58,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762127,
                "nanoOfSecond": 873951700
            },
            "thread": "sdk-async-response-0-0",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"rest get parameter\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"isAuditOnList\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 67,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762127,
                "nanoOfSecond": 987288100
            },
            "thread": "reactor-http-nio-5",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '1'\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 69,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762127,
                "nanoOfSecond": 987288100
            },
            "thread": "reactor-http-nio-5",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "throwable: org.springframework.web.reactive.function.client.WebClientRequestException: Connection refused: getsockopt: localhost/127.0.0.1:3000",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 69,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762132,
                "nanoOfSecond": 991383900
            },
            "thread": "parallel-1",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"Retry: 1\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"isAuditOnList\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 82,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762132,
                "nanoOfSecond": 995731000
            },
            "thread": "reactor-http-nio-6",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '2'\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 70,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762132,
                "nanoOfSecond": 995731000
            },
            "thread": "reactor-http-nio-6",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "throwable: org.springframework.web.reactive.function.client.WebClientRequestException: Connection refused: getsockopt: localhost/127.0.0.1:3000",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 70,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762138,
                "nanoOfSecond": 10353600
            },
            "thread": "parallel-2",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"Retry: 2\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"isAuditOnList\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 83,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762138,
                "nanoOfSecond": 18488300
            },
            "thread": "reactor-http-nio-7",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '3'\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 71,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762138,
                "nanoOfSecond": 19478200
            },
            "thread": "reactor-http-nio-7",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "throwable: org.springframework.web.reactive.function.client.WebClientRequestException: Connection refused: getsockopt: localhost/127.0.0.1:3000",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 71,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762143,
                "nanoOfSecond": 31735700
            },
            "thread": "parallel-3",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"Retry: 3\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.gateway.ParameterGateway\",\"method\":\"isAuditOnList\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 84,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751762143,
                "nanoOfSecond": 37327900
            },
            "thread": "reactor-http-nio-8",
            "level": "ERROR",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "throwable: reactor.core.Exceptions$RetryExhaustedException: Retries exhausted: 3/3",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 72,
            "threadPriority": 5
        }
        ```

    - Debido a que de momento no tenemos un microservicio que entregue el parámetro entonces vamos a simular la respuesta con la estructura que esperamos, utilizando wiremock

    Ejecutar en la consola para este caso windows
    ```
    podman run -d --name mock-server -p 3000:8080 docker.io/wiremock/wiremock
    ```

    Si ya existe se inicia el contenedor
    ```
    podman start mock-server
    ```

    - Curls para simular respuesta correcta
    ```
    curl --location 'http://localhost:8089/__admin/mappings' \
    --header 'Content-Type: application/json' \
    --data '{
        "request": {
            "method": "GET",
            "url": "/api/v3/microservice-param/auditOnSave"
        },
        "response": {
            "status": 200,
            "body": "{ \"name\": \"auditOnSave\", \"value\": \"1\", \"status\": \"active\" }",
            "headers": {
                "Content-Type": "application/json"
            }
        }
    }'
    ```

    - Realizar prueba

        Logs: se imprimé que guardó el registro y que se reintentó la petición hasta 3 veces cada 2 segundos, pero no se interrumpió el guardar el registro debido a que se controla el error y se emite un valor de FALSE para indicar que no debe guardar auditoría

        curl guardar
        ```
        curl --location 'localhost:8080/api/v2/microservice-aws/save-region' \
        --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
        --header 'Content-Type: application/json' \
        --header 'user-name: user dummy' \
        --data '{
            "region": "DEPARTAMENT-ANT",
            "name": "Rionegro",
            "codeRegion": "CITY-RIO"
        }'
        ```

        logs
        ```
        {
            "instant": {
                "epochSecond": 1751767827,
                "nanoOfSecond": 78101200
            },
            "thread": "reactor-http-nio-3",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"Save one world region\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservicio.aws.api.worldregion.WorldRegionHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"\",\"username\":\"user dummy\",\"device\":{\"userAgent\":\"\",\"platformType\":\"\"}}}},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 58,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751767827,
                "nanoOfSecond": 458332200
            },
            "thread": "sdk-async-response-0-0",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"rest get parameter\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"co.com.microservicio.aws.restconsumer.ParameterService\",\"method\":\"isAuditOnList\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 67,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751767827,
                "nanoOfSecond": 615260800
            },
            "thread": "reactor-http-nio-5",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservicio.aws.restconsumer.ParameterService\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"\",\"username\":\"user dummy\",\"device\":{\"userAgent\":\"\",\"platformType\":\"\"}}}},\"response\":{\"headers\":null,\"body\":{\"name\":\"auditOnSave\",\"value\":\"1\",\"status\":\"active\"}}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 69,
            "threadPriority": 5
        }
        {
            "instant": {
                "epochSecond": 1751767827,
                "nanoOfSecond": 615260800
            },
            "thread": "reactor-http-nio-5",
            "level": "INFO",
            "loggerName": "co.com.microservicio.aws.log.LoggerBuilder",
            "message": "{\"dataLog\":{\"message\":\"user dummy\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"audit on save\",\"method\":\"emitEvent\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":null},\"response\":{\"headers\":null,\"body\":null}}",
            "endOfBatch": false,
            "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
            "threadId": 69,
            "threadPriority": 5
        }
        ```

        1. Se imprime la respuesta de invocar el servicio REST
        2. Se imprime el usuario que ejecutó la auditoría

[< Volver al índice](README.md)

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)