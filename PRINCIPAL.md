# Instructivo paso a paso Postgresql, Mysql database; Secrets, Redis, Cron, RabbitMQ (Publicador - Consumidor), S3, DynamoDB

> A continuación se describe la configuración base de un proyecto construido bajo principios de microservicios y arquitectura limpia, integrando un sólido stack tecnológico.

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

### Requisitos

- ⚠️ Instalar Java 17 o superior
- ⚠️ Gradle 8.8 o posterior
- ⚠️ Instalar Docker o Podman: para simular servicios aws y otros
- ⚠️ Instalar Postman: para consumir servicios REST
- ⚠️ Instalar Intellij: IDE de desarrollo
- ⚠️ Instalar Plugin lombok en intellij

### Indice

* [1. Crear proyecto](#id1)
* [2. Crear la aplicación](#id2)
* [3. Postgresql: Crear la conexión](#id3)
* [4. Postgresql: Crear la instancia de base de datos en Podman](#id4)
* [5. Postgresql: Realizar pruebas (listall - save)](#id5)
* [6. Postgresql: Completar la aplicación con otros metodos](#id6)
* [7. Postgresql: Realizar pruebas (update - delete - findby)](#id7)
* [8. Mysql: Crear la conexión](#id8)
* [9. Mysql: Crear la instancia de base de datos en Podman](#id9)
* [10. Secrets-manager: Crear conexión ](#id10)
* [11. Secrets-manager: Crear secretos en podman](#id11)
* [12. Redis-cache: Crear conexión](#id12)
* [13. Redis-cache: Crear la instancia en podman](#id13)
* [14. Cron: Crear tarea en background](#id14)
* [15. Cron: Logs y validación en Redis](#id15)
* [16. Rabbit-MQ: Crear conexión](#id16)
* [17. Rabbit-MQ: Publicador](#id17)
* [18. Rabbit-MQ: Publicador pruebas](#id18)
* [19. Rabbit-MQ: Publicador logs](#id19)
* [20. Rabbit-MQ: Consumidor](#id20)
* [21. Rabbit-MQ: Consumidor pruebas](#id21)
* [22. Rabbit-MQ: Consumidor logs](#id22)
* [23. Webclient: Configurar consumo de APIs Externas](#id23)
* [24. Webclient: pruebas](#id24)
* [25. Webclient: logs](#id25)
* [26. S3: Configurar](#id26)
* [27. S3: Pruebas Txt](#id27)
* [28. S3: Logs Txt](#id28)
* [29. S3: Pruebas Zip](#id29)
* [30. S3: Logs Zip](#id30)
* [31. Dynamodb: Configuración](#id31)
* [32. Dynamodb: pruebas](#id32)
* [31. Dynamodb: logs](#id33)

# <div id='id1'/>
# 1. Crear y configurar el proyecto:

- ⚠️ Por favor lea detenidamente cada instrucción, es fácil perderse si no se lee cuidadosamente ya que pueden crear clases donde no corresponde o no leer bien la instrución, en algunas dice **crear** la clase y otras **modificar** la clase

1. Datos del proyecto
    - Visitar el sitio [Spring initializr](https://start.spring.io/)
    - Información:
      - group: co.com.microservice.aws
      - artifact: microservice-aws
      - name: MicroserviceAWS
      - description: A microservice based on Clean Architecture, built using Spring WebFlux for reactive, non-blocking operations, and performing CRUD operations on a PostgreSQL database.
      - package-name: co.com.microservice.aws
      - packaging: jar 

    - Dependencias base:
      - Spring Reactive Web (spring-boot-starter-webflux)
      - Spring Data R2DBC (spring-boot-starter-data-r2dbc)
      - R2DBC PostgreSQL Driver (io.r2dbc:r2dbc-postgresql)
      - Lombok (org.projectlombok:lombok)
      - Spring Boot DevTools

    ![](./img/modules/1-spring-initializr.png)

    - Generar el proyecto y descomprimir el proyecto

2. Cargar proyecto en intellij y crear paquetes de arquitectura limpia, en el paso a paso se especificarán los paquetes donde debe crearse la clase, si el paquete no existe, este debe crearse de acuerdo a la estructura propuesta

3. Configurar el proyecto

    - Ubicarse en la raiz del proyecto y modificar el archivo settings.gradle por
        ```
        pluginManagement {
            repositories {
                gradlePluginPortal()
            }
        }

        rootProject.name = 'microservice-aws'
        ```
        ⚠️ Actualizar dependencias

    - Ubicarse en la raiz del proyecto y modificar el archivo build.gradle por
        ```
        plugins {
            id 'java'
            id 'org.springframework.boot' version '3.5.3'
            id 'io.spring.dependency-management' version '1.1.7'
        }

        group = 'co.com.microservice.aws'
        version = '0.0.1-SNAPSHOT'

        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }

        configurations {
            compileOnly {
                extendsFrom annotationProcessor
            }
        }

        repositories {
            mavenCentral()
        }

        ext {
            awsSdkVersion = '2.25.17'
            reactiveCommonsVersion = '4.1.4'
            tikaVersion = '3.1.0'
            commonsCompressVersion = '1.27.1'
        }

        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation "org.apache.logging.log4j:log4j-core:2.24.3"
            implementation "org.apache.logging.log4j:log4j-api:2.24.3"
            implementation "org.apache.logging.log4j:log4j-to-slf4j:2.24.3"
            implementation 'com.fasterxml.jackson.core:jackson-databind'
            implementation 'io.r2dbc:r2dbc-h2'
            implementation 'org.mapstruct:mapstruct:1.5.2.Final'

            implementation 'com.github.jasync-sql:jasync-r2dbc-mysql:2.1.16'

            implementation platform('software.amazon.awssdk:bom:2.25.2')
            implementation 'software.amazon.awssdk:auth'

            implementation "software.amazon.awssdk:secretsmanager:$awsSdkVersion"
            implementation "software.amazon.awssdk:regions:$awsSdkVersion"
            implementation "software.amazon.awssdk:core:$awsSdkVersion"

            implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'
            implementation 'io.lettuce:lettuce-core'

            implementation "org.reactivecommons:async-commons-rabbit-starter:${reactiveCommonsVersion}"

            implementation 'software.amazon.awssdk:s3'
            implementation "org.apache.tika:tika-core:${tikaVersion}"
            implementation "org.apache.commons:commons-compress:${commonsCompressVersion}"

            implementation 'software.amazon.awssdk:dynamodb-enhanced'

            annotationProcessor 'org.mapstruct:mapstruct-processor:1.3.1.Final'

            testImplementation 'org.reactivecommons.utils:object-mapper:0.1.0'

            //developmentOnly 'org.springframework.boot:spring-boot-devtools'
            compileOnly 'org.projectlombok:lombok'
            runtimeOnly 'org.postgresql:postgresql'
            runtimeOnly 'org.postgresql:r2dbc-postgresql'
            runtimeOnly 'com.mysql:mysql-connector-j'

            annotationProcessor 'org.projectlombok:lombok'
            annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.2.Final'

            testImplementation 'org.springframework.boot:spring-boot-starter-test'
            testImplementation 'io.projectreactor:reactor-test'
            testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
        }

        // Para evitar conflicto con Logback
        configurations {
            configureEach {
                exclude group: 'org.apache.logging.log4j', module: 'log4j-to-slf4j'
                exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
            }
        }

        tasks.named('test') {
            useJUnitPlatform()
        }

        //options.incremental = false recompila siempre todos los archivos del proyecto
        tasks.withType(JavaCompile).configureEach {
            options.compilerArgs += ["-parameters"]
            options.incremental = false
        }
        ```
        ⚠️ Actualizar dependencias: se decargan todas las dependencias que se usarán en esta guía.

    - Ubicarse en src > main > resources y crear el archivo application.yaml y de igual forma application-local.yaml con lo siguiente
```
server:
  port: ${APP_PORT:8080}

spring:
  application:
    name: "${APP_NAME:MicroserviceAws}"
  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:

management:
  health:
    probes:
      enabled: true
  endpoint:
    health:
      show-details: ${SHOW_DETAILS:never}
      enabled: true
      cache:
        time-to-live: "10s"
  endpoints:
    web:
      base-path: "${PATH_BASE:/api/v1/microservice-aws/}"
      path-mapping:
        health: "health"
        liveness: "liveness"
        readiness: "readiness"
      exposure:
        include: "health, liveness, readiness, metrics"

logging:
  level:
    root: ${LOG4J_LEVEL:INFO}

entries:
  web:
    path-base: "${PATH_BASE:/api/v1/microservice-aws}"
    path-countries: "${PATH_COUNTRY:/country}"
    listAll: "/list-all"
    findByShortCode: "/findByShortCode/{shortCode}"
    save: "/save"
    update: "/update"
    delete: "/delete/{id}"
  regex-body-wr:
    name: "${REGEX_COUNTRY_NAME:^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$}"
    codeShort: "${REGEX_COUNTRY_CODE_SHORT:^[a-zA-Z]{3,4}$}"
  properties:
    expression-timer: "${EXPRESSION_TIMER:0 */15 * * * ?}"
    process-on-schedule: "${PROCESS_ON_SCHEDULE:Y}"

adapters:
  secrets-manager:
    region: "${AWS_REGION:us-east-1}"
    endpoint: ${PARAM_URL:http://localhost:4566}
    namePostgresql: "${SECRET_NAME_POSTGRE:local-postgresql}"
    nameMysql: "${SECRET_NAME_MYSQL:local-mysql}"
    nameRedis: "${SECRET_NAME_REDIS:local-redis}"
    nameRabbitMq: "${SECRET_NAME_RABBIT_MQ:local-rabbitmq}"
  redis:
    expireTime: ${CACHE_EXPIRE_SECONDS:10}
  rest-country:
    timeout: ${TIMEOUT:5000}
    url: ${URL_COUNTRIES:http://localhost:3000/api/v3/microservice-countries}
    info:
      exist: "${COUNTRY_EXIST:/country/exist/}"
    retry:
      retries: ${REST_COUNTRY_RETRIES:3}
      retryDelay: ${REST_COUNTRY_RETRY_DELAY:2}
  s3:
    region: "${AWS_REGION:us-east-1}"
    endpoint: http://localhost:4566
    accessKey: "test"
    secretKey: "test"

listen:
  event:
    names:
      saveCountry: "${EVENT_NAME_SAVE_COUNTRY:business.myapp.save.country}"
      saveCacheCountCountry: "${EVENT_NAME_COUNT_IN_CACHE_COUNTRY:business.myapp.save-cache-count.country}"
      saveWorldRegion: "${EVENT_NAME_SAVE_ALL_WORLD_REGION:business.myapp.save-all.world-region}"
```
⚠️ Algunas configuraciones serán de importancia en desarrollos mas adelante y se crea de esta forma ${variable:valor} para que al momento de un despliegue a través de pipeline, estos valores puedan ser tomados desde la configuración del pipeline

- Abrir el archivo MicroserviceAwsApplication.java y click derecho y ejecutar la aplicación
    
    ![](./img/modules/2-ejecutar-aplicacion.png)

- Configurar la aplicación para ejecutar de forma local
    ```
    SPRING_PROFILES_ACTIVE=local
    ```

    ![](./img/proyecto-base-config-perfil-local.png)

# <div id='id2'/>
# 2. Crear la aplicación

- Ubicarse en src > main > resources y crear el archivo log4j2.properties
    ```
    status = error
    name = MicroserviceAWS
    appender.console.type = Console
    appender.console.name = STDOUT
    appender.console.layout.type = JsonLayout
    appender.console.layout.compact = true
    appender.console.layout.eventEol = true
    appender.console.layout.includeStacktrace = true
    appender.console.layout.includeThreadContext = false
    appender.console.layout.properties = false

    appender.console.layout.includeTimeMillis = false

    rootLogger.level = info
    rootLogger.appenderRefs = stdout
    rootLogger.appenderRef.stdout.ref = STDOUT
    ```
    Esta estructura corresponde al modo de visualización de los logs en la consola del IDE, en la que estos se ven en formato JSON para una mejor visualización.

- 💡 **Tip importante**, si en algun momento requieres mover una clase a un paquete, lo haces haciendo click derecho sobre la clase, elegir opción refactor, luego la opción move class y escribes el paquete o en los "..." puedes elegir en forma de arbol el paquete donde deseas ubicar la clase

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.logs y crear la clase TransactionLog.java
    ```
    package co.com.microservice.aws.application.helpers.logs;

    import java.io.Serial;
    import java.io.Serializable;
    import java.util.Map;

    import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder(toBuilder = true)
    public class TransactionLog {
        @Serial
        private static final long serialVersionUID = 1L;

        private Application app;
        private Request request;
        private Response response;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @Builder(toBuilder = true)
        public static class Application implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private String message;
            private String messageId;
            private String service;
            private String method;
            private String appName;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @Builder(toBuilder = true)
        public static class Request implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private Map<String, String> headers;
            private transient Object body;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @Builder(toBuilder = true)
        public static class Response implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private Map<String, String> headers;
            private transient Object body;
        }
    }
    ```
    Esta clase es la usada para imprimir en los logs los datos de entrada que llamamos request o los datos de respuesta que llamamos response

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.logs y crear la clase LoggerBuilder.java

    ```
    package co.com.microservice.aws.application.helpers.logs;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.Getter;
    import lombok.extern.log4j.Log4j2;
    import org.apache.logging.log4j.message.ObjectMessage;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;

    @Log4j2
    @Getter
    @Component
    public class LoggerBuilder {
        private final String appName;
        private final ObjectMapper objectMapper;

        public LoggerBuilder(@Value("${spring.application.name}") String appName, ObjectMapper objectMapper) {
            this.appName = appName;
            this.objectMapper = objectMapper;
        }

        public void info(TransactionLog.Request rq, String message, String messageId, String service, String method) {
            log.info(new ObjectMessage(buildObjectReq(rq, buildDataLog(message, messageId, service, method))));
        }

        public void info(TransactionLog.Response rs, String message, String messageId, String service, String method) {
            log.info(new ObjectMessage(buildObjectRes(rs, buildDataLog(message, messageId, service, method))));
        }

        public void info(String message, String messageId, String service, String method) {
            log.info(new ObjectMessage(buildObjectApp(buildDataLog(message, messageId, service, method))));
        }

        public void error(Throwable throwable) {
            log.error("Unexpected error occurred:", throwable);
        }

        public void info(String message) {
            log.info(message);
        }

        private TransactionLog.Application buildDataLog(String message, String messageId, String service, String method){
            return new TransactionLog.Application(message, messageId, service, method, appName);
        }

        private String buildObjectReq(TransactionLog.Request rq, TransactionLog.Application data) {
            return buildObject(TransactionLog.builder().app(data).request(rq).build());
        }

        private String buildObjectRes(TransactionLog.Response rs, TransactionLog.Application data) {
            return buildObject(TransactionLog.builder().app(data).response(rs).build());
        }

        private String buildObjectApp(TransactionLog.Application data) {
            return buildObject(TransactionLog.builder().app(data).build());
        }

        private String buildObject(TransactionLog logObject) {
            try {
                return objectMapper.writeValueAsString(logObject);
            } catch (JsonProcessingException e) {
                return logObject.toString();
            }
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y crear la clase BusinessExceptionMessage.java
    - WRB: Es World Region Business

    ```
    package co.com.microservice.aws.domain.model.commons.enums;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.ToString;

    @Getter
    @AllArgsConstructor
    @ToString
    public enum BusinessExceptionMessage {
        BUSINESS_ERROR("WRB01", "Error in a service", "Server error"),
        BUSINESS_USERNAME_REQUIRED("WRB02", "The attribute 'user-name' is required", "There is an error in the request body"),
        BUSINESS_RECORD_NOT_FOUND("WRB03", "The record not found", "Bussiness error"),
        BUSINESS_COUNTRY_NOT_EXIST("WRB04", "The country not exist", "Bussiness error"),
        BUSINESS_OTRO_MENSAJE("WRB03", "Others message business", "Other");

        private final String code;
        private final String message;
        private final String typeMessage;

        public String getDescription() {
            return String.join(" - ", this.getCode(), this.getMessage());
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y crear la clase TechnicalExceptionMessage.java
    - WRT: Es World Region Technical
    ```
    package co.com.microservice.aws.domain.model.commons.enums;

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
        TECHNICAL_REQUEST_ERROR("WRT04", "There is an error in the request body"),
        TECHNICAL_EXCEPTION_REPOSITORY("WRT05", "An error has occurred in the repository");

        private final String code;
        private final String message;

        public String getDescription() {
            return String.join(" - ", this.getCode(), this.getMessage());
        }
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.error y crear la clase Error.java
    ```
    package co.com.microservice.aws.domain.model.commons.error;

    import java.io.Serial;
    import java.io.Serializable;
    import java.util.List;

    import lombok.*;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public class Error implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private List<Data> errors;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Builder(toBuilder = true)
        public static class Data implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private String reason;
            private String domain;
            private String code;
            private String message;
        }
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.exception y crear la clase TechnicalException.java
    ```
    package co.com.microservice.aws.domain.model.commons.exception;

    import java.io.Serial;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import lombok.Getter;

    @Getter
    public class TechnicalException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;

        private final TechnicalExceptionMessage typeTechnicalException;
        private final String reason;

        public TechnicalException(Throwable error, TechnicalExceptionMessage technicalExceptionMessage) {
            super(technicalExceptionMessage.getDescription(), error);
            this.typeTechnicalException = technicalExceptionMessage;
            this.reason = technicalExceptionMessage.getDescription();
        }

        public TechnicalException(TechnicalExceptionMessage technicalExceptionEnum) {
            super(technicalExceptionEnum.getMessage());
            this.typeTechnicalException = technicalExceptionEnum;
            this.reason = technicalExceptionEnum.getDescription();
        }

        public TechnicalException(String reason, TechnicalExceptionMessage errorMessage) {
            super(errorMessage.getMessage());
            this.reason = reason;
            this.typeTechnicalException = errorMessage;
        }
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.exception y crear la clase BusinessException.java
    ```
    package co.com.microservice.aws.domain.model.commons.exception;

    import co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.error.Error;
    import lombok.Getter;

    import java.io.Serial;

    @Getter
    public class BusinessException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;

        private final Error error;
        private final BusinessExceptionMessage typeBusinessException;

        public BusinessException(Error error) {
            super(error.getErrors().get(0).getMessage());
            this.error = error;
            this.typeBusinessException = BusinessExceptionMessage.BUSINESS_ERROR;
        }

        public BusinessException(Throwable error, BusinessExceptionMessage typeBusinessException) {
            super(typeBusinessException.getDescription(), error);
            this.error = Error.builder().build();
            this.typeBusinessException = typeBusinessException;
        }

        public BusinessException(BusinessExceptionMessage typeBusinessException) {
            super(typeBusinessException.getDescription());
            this.error = Error.builder().build();
            this.typeBusinessException = typeBusinessException;
        }
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.error y crear la clase ErrorFactory.java
    ```
    package co.com.microservice.aws.domain.model.commons.error;

    import co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import lombok.experimental.UtilityClass;

    import java.util.List;

    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_SERVER_ERROR;

    @UtilityClass
    public class ErrorFactory {

        public Error buildError(TechnicalExceptionMessage technicalExceptionEnum, String reason, String domain) {
            return Error.builder()
                    .errors(List.of(Error.Data.builder().reason(reason).domain(domain)
                            .code(technicalExceptionEnum.getCode()).message(technicalExceptionEnum.getMessage()).build()))
                    .build();
        }

        public Error buildErrorBusiness(BusinessExceptionMessage businessErrorMessage, String reason, String domain) {
            return Error
                    .builder().errors(List.of(Error.Data.builder().reason(reason).domain(domain)
                            .code(businessErrorMessage.getCode()).message(businessErrorMessage.getTypeMessage()).build()))
                    .build();
        }

        public Error fromTechnical(TechnicalException technicalException, String domain) {
            if (technicalException.getReason() == null || technicalException.getReason().isEmpty()) {
                return buildError(technicalException.getTypeTechnicalException(), technicalException.getMessage(), domain);
            } else {
                return buildErrorWithReason(technicalException, domain);
            }
        }

        public Error buildErrorWithReason(TechnicalException technicalException, String domain) {
            return buildError(technicalException.getTypeTechnicalException(), technicalException.getReason(), domain);
        }

        public Error fromBusiness(BusinessException businessException, String domain) {
            if (businessException.getTypeBusinessException() == BusinessExceptionMessage.BUSINESS_ERROR) {
                return businessException.getError();
            }
            return buildErrorBusiness(businessException.getTypeBusinessException(),
                    businessException.getTypeBusinessException().getMessage(), domain);
        }

        public Error fromDefaultTechnical(String reason, String domain) {
            return buildError(TECHNICAL_SERVER_ERROR, reason, domain);
        }
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.exception;

    import co.com.microservice.aws.domain.model.commons.error.ErrorFactory;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import org.springframework.boot.autoconfigure.web.WebProperties;
    import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
    import org.springframework.boot.web.reactive.error.ErrorAttributes;
    import org.springframework.context.ApplicationContext;
    import org.springframework.core.annotation.Order;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.codec.ServerCodecConfigurer;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.*;
    import reactor.core.publisher.Mono;

    import java.util.function.Function;

    import static org.springframework.http.HttpStatus.BAD_REQUEST;
    import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

    @Order(-2)
    @Component
    public class ExceptionHandler extends AbstractErrorWebExceptionHandler {
        public static final String FORMAT_ERROR = "%s:%s";

        public ExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                ServerCodecConfigurer serverCodecConfigurer) {
            super(errorAttributes, new WebProperties.Resources(), applicationContext);
            this.setMessageWriters(serverCodecConfigurer.getWriters());
        }

        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
        }

        public Mono<ServerResponse> buildErrorResponse(final ServerRequest request) {
            return Mono.just(request).map(this::getError).flatMap(Mono::error)
                    .onErrorResume(TechnicalException.class, responseTechnicalError(request))
                    .onErrorResume(BusinessException.class, responseBusinessError(request))
                    .onErrorResume(responseDefaultError(request)).cast(ServerResponse.class);
        }

        private Function<BusinessException, Mono<ServerResponse>> responseBusinessError(ServerRequest request) {
            return e -> Mono
                    .just(ErrorFactory.fromBusiness(e,
                            String.format(FORMAT_ERROR, request.method().name(), request.path())))
                    .flatMap(this::responseFailBusiness);
        }

        private Function<TechnicalException, Mono<ServerResponse>> responseTechnicalError(ServerRequest request) {
            return e -> Mono
                    .just(ErrorFactory.fromTechnical(e,
                            String.format(FORMAT_ERROR, request.method().name(), request.path())))
                    .flatMap(this::responseFailBusiness);
        }

        private Function<Throwable, Mono<ServerResponse>> responseDefaultError(ServerRequest request) {
            return exception -> Mono
                    .just(ErrorFactory.fromDefaultTechnical(exception.getMessage(),
                            String.format(FORMAT_ERROR, request.method().name(), request.path())))
                    .flatMap(this::responseFail);
        }

        public <T> Mono<ServerResponse> buildResponse(T error, HttpStatus httpStatus) {
            return ServerResponse.status(httpStatus).contentType(MediaType.APPLICATION_JSON).bodyValue(error);
        }

        public <T> Mono<ServerResponse> responseFail(T body) {
            return buildResponse(body, INTERNAL_SERVER_ERROR);
        }

        public <T> Mono<ServerResponse> responseFailBusiness(T body) {
            return buildResponse(body, BAD_REQUEST);
        }
    }
    ```

    ¿Por qué usar @Order(-2)?

    Spring ya tiene un DefaultErrorWebExceptionHandler que también extiende AbstractErrorWebExceptionHandler.

    Si defines uno personalizado y no le das mayor prioridad (-2 o menor), el de Spring lo sobreescribirá o ejecutará antes.

    @Order(-2) asegura que tu handler personalizado tenga precedencia y maneje los errores globales de tu manera.

- Ubicarse en el paquete co.com.microservice.aws.domain.model y crear la clase Country.java
    ```
    package co.com.microservice.aws.domain.model;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.io.Serial;
    import java.io.Serializable;
    import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class Country implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Long id;
        private String shortCode;
        private String name;
        private String description;
        private boolean status;
        private LocalDateTime dateCreation;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.rq y crear la clase Context.java
    ```
    package co.com.microservice.aws.domain.model.rq;

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

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder(toBuilder = true)
        public static class Customer {
            private String ip;
            private String username;
            private Device device;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder(toBuilder = true)
        public static class Device {
            private String userAgent;
            private String platformType;
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.model.rq y crear la clase TransactionRequest.java
    ```
    package co.com.microservice.aws.domain.model.rq;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;
    import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class TransactionRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient Context context;
        private transient Object item;
        private transient List<Object> items;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.rs y crear la clase TransactionResponse.java
    ```
    package co.com.microservice.aws.domain.model.rs;

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
        private int size;
        private List<Object> response;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.commons y crear la clase HeadersUtil.java
    ```
    package co.com.microservice.aws.application.helpers.commons;

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

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.commons y crear la clase ContextUtil.java
    ```
    package co.com.microservice.aws.application.helpers.commons;

    import co.com.microservice.aws.domain.model.rq.Context;
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

        private static Context.Customer buildCustomer(Map<String, String> headers) {
            return Context.Customer.builder().ip(Optional.ofNullable(headers.get("ip")).orElse(EMPTY_VALUE))
                    .username(Optional.ofNullable(headers.get("user-name")).orElse(EMPTY_VALUE))
                    .device(buildDevice(headers)).build();
        }

        private static Context.Device buildDevice(Map<String, String> headers) {
            return Context.Device.builder().userAgent(Optional.ofNullable(headers.get("user-agent")).orElse(EMPTY_VALUE))
                    .platformType(Optional.ofNullable(headers.get("platform-type")).orElse(EMPTY_VALUE)).build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase ListAllUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import reactor.core.publisher.Mono;

    public interface ListAllUseCase {
        Mono<TransactionResponse> listAll(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase SaveUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface SaveUseCase {
        Mono<String> save(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase ListAllPort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import co.com.microservice.aws.domain.model.rq.Context;
    import reactor.core.publisher.Flux;

    public interface ListAllPort<T> {
        Flux<T> listAll(Context context);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase SavePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import co.com.microservice.aws.domain.model.rq.Context;
    import reactor.core.publisher.Mono;

    public interface SavePort<T> {
        Mono<T> save(T t, Context context);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.util y crear la clase ResponseMessageConstant.java
    ```
    package co.com.microservice.aws.domain.model.commons.util;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class ResponseMessageConstant {
        public static final String MSG_LIST_SUCCESS = "Listed successfull!";
        public static final String MSG_SAVED_SUCCESS = "Saved successfull!";
        public static final String MSG_UPDATED_SUCCESS = "Updated successfull!";
        public static final String MSG_DELETED_SUCCESS = "Deleted successfull!";
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.util y crear la clase LogMessage.java
    ```
    package co.com.microservice.aws.domain.model.commons.util;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class LogMessage {
        public static final String MESSAGE_SERVICE = "Service Api Rest world regions";
        public static final String METHOD_LISTCOUNTRIES = "List all by region";
        public static final String METHOD_FINDONE = "Find one world region";
        public static final String METHOD_SAVE = "Save one world region";
        public static final String METHOD_UPDATE = "Update one world region";
        public static final String METHOD_DELETE = "Delete one world region";
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.commons y crear la clase UseCase.java
    ```
    package co.com.microservice.aws.application.helpers.commons;

    import org.springframework.core.annotation.AliasFor;
    import org.springframework.stereotype.Component;

    import java.lang.annotation.*;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Component
    public @interface UseCase {
        @AliasFor(annotation = Component.class)
        String value() default "";
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase CountryUseCase.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.ListAllUseCase;
    import co.com.microservice.aws.domain.usecase.in.SaveUseCase;
    import co.com.microservice.aws.domain.usecase.out.ListAllPort;
    import co.com.microservice.aws.domain.usecase.out.SavePort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

    @UseCase("countryUseCase")
    @RequiredArgsConstructor
    public class CountryUseCase implements SaveUseCase, ListAllUseCase {
        private static final String KEY_USER_NAME = "user-name";
        private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

        private final SavePort<Country> countrySaver;
        private final ListAllPort<Country> countryLister;

        @Override
        public Mono<TransactionResponse> listAll(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> save(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(country -> countrySaver.save(country, request.getContext()))
                .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                        .shortCode(country.getShortCode()).status(country.isStatus())
                        .dateCreation(country.getDateCreation())
                        .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Country> countries){
            var simplifiedList = countries.stream()
                .map(country -> Country.builder().id(country.getId()).name(country.getName())
                    .shortCode(country.getShortCode()).status(country.isStatus())
                    .dateCreation(country.getDateCreation())
                    .build())
                .toList();

            TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(countries.size())
                .response(Collections.singletonList(simplifiedList))
                .build();

            return Mono.just(response);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.config y crear la clase RouterProperties.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.config;

    import lombok.Data;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;

    @Data
    @Component
    @ConfigurationProperties(prefix = "entries.web")
    public class RouterProperties {
        private String pathBase;
        private String pathCountries;
        private String listAll;
        private String findByShortCode;
        private String save;
        private String update;
        private String delete;
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.handler y crear la clase CountryHandler.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.handler;

    import co.com.microservice.aws.application.helpers.commons.ContextUtil;
    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.*;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    import java.util.Map;

    import static co.com.microservice.aws.domain.model.commons.util.LogMessage.*;

    @Component
    public class CountryHandler {
        private static final String NAME_CLASS = CountryHandler.class.getName();

        private final LoggerBuilder logger;
        private final ListAllUseCase useCaseLister;
        private final SaveUseCase useCaseSaver;

        public CountryHandler(
                LoggerBuilder logger,
                @Qualifier("countryUseCase") ListAllUseCase useCaseLister,
                @Qualifier("countryUseCase") SaveUseCase useCaseSaver
        ) {
            this.logger = logger;
            this.useCaseLister = useCaseLister;
            this.useCaseSaver = useCaseSaver;
        }

        public Mono<ServerResponse> listAll(ServerRequest serverRequest) {
            var request = this.buildRequestWithParams(serverRequest, METHOD_LISTCOUNTRIES, Map.of("none", "none"));
            return useCaseLister.listAll(request)
                    .doOnError(this::printFailed)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response));
        }

        public Mono<ServerResponse> save(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, METHOD_SAVE);

            return this.getRequest(serverRequest)
                    .flatMap(useCaseSaver::save)
                    .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
        }

        private Mono<TransactionRequest> getRequest(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            return serverRequest.bodyToMono(Country.class)
                    .flatMap(country -> Mono.just(TransactionRequest.builder()
                            .context(context).item(country).build()));
        }

        private TransactionRequest buildRequestWithParams(ServerRequest serverRequest,
                                                        String method, Map<String, String> param){
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, method);

            return TransactionRequest.builder()
                    .context(context)
                    .params(param)
                    .build();
        }

        private void printFailed(Throwable throwable) {
            logger.error(throwable);
        }

        private void printOnProcess(Context context, String messageInfo){
            logger.info(TransactionLog.Request.builder().body(context).build(),
                    messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.router y crear la clase CountryRouterRest.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.router;

    import co.com.microservice.aws.infrastructure.input.rest.api.config.RouterProperties;
    import co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.RouterFunctions;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class CountryRouterRest {
        private final RouterProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerCountryFunction(CountryHandler countryHandler) {
            return RouterFunctions.route()
                    .GET(createRoute(properties.getListAll()), countryHandler::listAll)
                    .POST(createRoute(properties.getSave()), countryHandler::save)
                    .build();
        }

        private String createRoute(String route){
            return properties.getPathBase().concat(properties.getPathCountries()).concat(route);
        }
    }
    ```

# <div id='id3'/>
# 3. Crear la conexión a Postgresql

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.entity y crear la clase CountryEntity.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.entity;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;

    import java.time.LocalDateTime;

    @Table(name = "countries", schema = "worldregion")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CountryEntity {
        @Id
        private Long id;
        private String shortCode;
        private String name;
        private String description;
        private boolean status;
        private LocalDateTime dateCreation;
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.mapper y crear la clase CountryEntityMapper.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.mapper;

    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
    import org.mapstruct.Mapper;
    import org.mapstruct.ReportingPolicy;

    @Mapper(componentModel = "spring")
    public interface CountryEntityMapper {
        CountryEntity toEntityFromModel(Country objectModel);
        Country toModelFromEntity(CountryEntity objectEntity);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.repository y crear la clase CountryRepository.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.repository;

    import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
    import org.springframework.data.r2dbc.repository.R2dbcRepository;

    public interface CountryRepository extends R2dbcRepository<CountryEntity, Long> {
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql y crear la clase CountryAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql;

    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.ListAllPort;
    import co.com.microservice.aws.domain.usecase.out.SavePort;
    import co.com.microservice.aws.infrastructure.output.postgresql.mapper.CountryEntityMapper;
    import co.com.microservice.aws.infrastructure.output.postgresql.repository.CountryRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class CountryAdapter implements SavePort<Country>, ListAllPort<Country> {
        private final CountryEntityMapper mapper;
        private final CountryRepository countryRepository;

        @Override
        public Mono<Country> save(Country country, Context context) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::save)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Flux<Country> listAll(Context context) {
            return countryRepository.findAll()
                    .map(mapper::toModelFromEntity);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.config y crear la clase PostgresConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.config;

    import io.r2dbc.spi.ConnectionFactory;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import io.r2dbc.spi.ConnectionFactories;
    import io.r2dbc.spi.ConnectionFactoryOptions;

    import static io.r2dbc.spi.ConnectionFactoryOptions.*;

    @Configuration
    @RequiredArgsConstructor
    public class PostgresConfig {

        @Bean
        public ConnectionFactory postgresConnectionFactory(@Value("${adapters.postgresql.url}") String url,
                                                        @Value("${adapters.postgresql.usr}") String usr,
                                                        @Value("${adapters.postgresql.psw}") String psw) {
            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url)
                    .mutate()
                    .option(USER, usr)
                    .option(PASSWORD, psw)
                    .build();

            return ConnectionFactories.get(options);
        }
    }
    ```

# <div id='id4'/>
# 4. Crear la instancia de base de datos en Podman

- Abrir la consola de comandos para descargar la imagen de Postgresql y subir el contenedor en Podman o Docker
    ```
    podman machine start
    podman pull docker.io/library/postgres:16
    ```
- Ejecutar el contenedor
    ```
    podman run --name postgres-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=my_postgres_db -p 5432:5432 -d postgres:16
    ```
- Conectar a la bd con DBeaver

    ![](./img/modules/3_connection_dbeaver_postgresql.png)

- Creamos la tabla
    ```
    DROP TABLE IF EXISTS worldregion.countries;

    CREATE SCHEMA IF NOT EXISTS worldregion;

    CREATE SEQUENCE worldregion.countries_id_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1;
    
    CREATE TABLE worldregion.countries (
        id BIGINT PRIMARY KEY DEFAULT nextval('worldregion.countries_id_seq'),
        short_code VARCHAR(10) NOT NULL,
        name VARCHAR(100) NOT NULL,
        description TEXT,
        status BOOLEAN NOT NULL,
        date_creation TIMESTAMP NOT NULL
    );

    CREATE SEQUENCE IF NOT EXISTS worldregion.countries_id_seq;

    ALTER TABLE worldregion.countries
        ALTER COLUMN id SET DEFAULT nextval('worldregion.countries_id_seq');
    ```

- Realizamos el insert de la información
    ```
    INSERT INTO worldregion.countries (short_code, name, description, status, date_creation) VALUES
    ('CO', 'Colombia', 'Colombia, ubicada en América del Sur, cuenta con una población estimada de 52 millones de habitantes.', 
    true, '2025-07-12 08:00:00')

    INSERT INTO worldregion.countries (short_code, name, description, status, date_creation) VALUES
    ('MX', 'México', 'México, nación norteamericana, tiene aproximadamente 129 millones de habitantes.', true, '2025-07-11 09:15:00'),
    ('US', 'Estados Unidos', 'Estados Unidos posee una población de alrededor de 334 millones de personas.', true, '2025-07-10 10:30:00'),
    ('FR', 'Francia', 'Francia, ubicada en Europa occidental, tiene una población cercana a los 68 millones de habitantes.', false, '2025-07-09 11:45:00'),
    ('JP', 'Japón', 'Japón, nación insular asiática, cuenta con aproximadamente 125 millones de habitantes.', true, '2025-07-08 14:00:00');
    ```

- Ejecutar la creación de la tabla

    ![](./img/modules/3_connection_dbeaver_postgresql_create_table.png)

# <div id='id5'/>
# 5. Realizar pruebas (listall - save)

- Ejecutar la aplicación
- Probamos "list-all" por postman: curls caso error
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/list-all'
    ```
    ![](./img/modules/4_postman-user-required.png)

- Probamos "list-all" por postman: curls caso exitoso
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/list-all' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'
    ```
    ![](./img/modules/4_postman-successful.png)

- Ver logs

    ![](./img/modules/4_logs-list-all.png)

- Probamos "save" por postman:
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/save' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ESP",
        "name": "España",
        "description": "Cuenta con una población estimada de 40 millones de habitantes.",
        "status": true,
        "dateCreation": "2025-07-12T08:00:00"
    }'
    ```

- Ver logs
    ```
    {
        "instant": {
            "epochSecond": 1752374275,
            "nanoOfSecond": 418281500
        },
        "thread": "reactor-http-nio-9",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Save one record\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 81,
        "threadPriority": 5
    }
    ```

# <div id='id6'/>
# 6. Completar la aplicación con otros metodos, refactorizando

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase UpdateUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface UpdateUseCase {
        Mono<String> update(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase DeleteUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface DeleteUseCase {
        Mono<String> delete(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase FindByShortCodeUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface FindByShortCodeUseCase {
        Mono<TransactionResponse> findByShortCode(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase CountryUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    public interface CountryUseCase extends SaveUseCase, UpdateUseCase, DeleteUseCase,
            ListAllUseCase, FindByShortCodeUseCase{
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.rq y modificar la clase TransactionRequest.java
    ```
    package co.com.microservice.aws.domain.model.rq;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;
    import java.util.List;
    import java.util.Map;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class TransactionRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient Context context;
        private transient Object item;
        private transient List<Object> items;
        private transient Map<String, String> params;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.handler y modificar la clase CountryHandler.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.handler;

    import co.com.microservice.aws.application.helpers.commons.ContextUtil;
    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.CountryUseCase;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    import java.util.Map;

    import static co.com.microservice.aws.domain.model.commons.util.LogMessage.*;

    @Component
    @RequiredArgsConstructor
    public class CountryHandler {
        private static final String NAME_CLASS = CountryHandler.class.getName();

        private final LoggerBuilder logger;
        private final CountryUseCase countryUseCase;

        public Mono<ServerResponse> listAll(ServerRequest serverRequest) {
            var request = this.buildRequestWithParams(serverRequest, METHOD_LISTCOUNTRIES, Map.of("none", "none"));
            return countryUseCase.listAll(request)
                    .doOnError(this::printFailed)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response));
        }

        public Mono<ServerResponse> save(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, METHOD_SAVE);

            return this.getRequest(serverRequest)
                    .flatMap(countryUseCase::save)
                    .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
        }

        public Mono<ServerResponse> findOne(ServerRequest serverRequest) {
            var request = this.buildRequestWithParamsFind(serverRequest, METHOD_FINDONE);
            return countryUseCase.findByShortCode(request)
                    .doOnError(this::printFailed)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response));
        }

        public Mono<ServerResponse> update(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, METHOD_UPDATE);

            return this.getRequest(serverRequest)
                    .flatMap(countryUseCase::update)
                    .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
        }

        public Mono<ServerResponse> delete(ServerRequest serverRequest) {
            var request = this.buildRequestWithParamsDelete(serverRequest, METHOD_DELETE);
            return countryUseCase.delete(request)
                    .doOnError(this::printFailed)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response));
        }

        private Mono<TransactionRequest> getRequest(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            return serverRequest.bodyToMono(Country.class)
                    .flatMap(country -> Mono.just(TransactionRequest.builder()
                            .context(context).item(country).build()));
        }

        private TransactionRequest buildRequestWithParamsFind(ServerRequest serverRequest, String method){
            var shortCode = serverRequest.pathVariable("shortCode");
            return this.buildRequestWithParams(serverRequest, method, Map.of("shortCode", shortCode));
        }

        private TransactionRequest buildRequestWithParamsDelete(ServerRequest serverRequest, String method){
            var shortCode = serverRequest.pathVariable("id");
            return this.buildRequestWithParams(serverRequest, method, Map.of("id", shortCode));
        }

        private TransactionRequest buildRequestWithParams(ServerRequest serverRequest,
                                                        String method, Map<String, String> param){
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            var context = ContextUtil.buildContext(headers);
            printOnProcess(context, method);

            return TransactionRequest.builder()
                    .context(context)
                    .params(param)
                    .build();
        }

        private void printFailed(Throwable throwable) {
            logger.error(throwable);
        }

        private void printOnProcess(Context context, String messageInfo){
            logger.info(TransactionLog.Request.builder().body(context).build(),
                    messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase UpdatePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface UpdatePort<T> {
        Mono<T> update(T t);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase DeletePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface DeletePort<T> {
        Mono<Void> delete(T t);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase FindByShortCodePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface FindByShortCodePort<T> {
        Mono<T> findByShortCode(T t);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y modificar la clase BusinessExceptionMessage.java
    ```
    package co.com.microservice.aws.domain.model.commons.enums;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.ToString;

    @Getter
    @AllArgsConstructor
    @ToString
    public enum BusinessExceptionMessage {
        BUSINESS_ERROR("WRB01", "Error in a service", "Server error"),
        BUSINESS_USERNAME_REQUIRED("WRB02", "The attribute 'user-name' is required", "There is an error in the request body"),
        BUSINESS_RECORD_NOT_FOUND("WRB03", "The record not found", "Bussiness error"),
        BUSINESS_OTRO_MENSAJE("WRB03", "Others message business", "Other");

        private final String code;
        private final String message;
        private final String typeMessage;

        public String getDescription() {
            return String.join(" - ", this.getCode(), this.getMessage());
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.application.usecase y modificar la clase CountryUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.*;
    import co.com.microservice.aws.domain.usecase.out.*;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_RECORD_NOT_FOUND;
    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;
    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

    @UseCase
    @RequiredArgsConstructor
    public class CountryUseCaseImpl implements CountryUseCase {
        private static final String KEY_USER_NAME = "user-name";
        private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

        private final SavePort<Country> countrySaver;
        private final ListAllPort<Country> countryLister;
        private final UpdatePort<Country> countryUpdater;
        private final DeletePort<Country> countryDeleter;
        private final FindByShortCodePort<Country> countryFinder;

        @Override
        public Mono<TransactionResponse> listAll(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> save(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(country -> countrySaver.save(country, request.getContext()))
                .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
        }

        @Override
        public Mono<String> delete(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().id(Long.valueOf(rq.getParams().get("id"))).build())
                    .flatMap(countryDeleter::delete)
                    .thenReturn(ResponseMessageConstant.MSG_DELETED_SUCCESS);
        }

        @Override
        public Mono<TransactionResponse> findByShortCode(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().shortCode(rq.getParams().get("shortCode")).build())
                    .flatMap(countryFinder::findByShortCode)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                    .flatMap(c -> this.buildResponse(List.of(c))
                    ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> update(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::executeUpdate)
                    .thenReturn(ResponseMessageConstant.MSG_UPDATED_SUCCESS);
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                    .shortCode(country.getShortCode()).status(country.isStatus())
                    .dateCreation(country.getDateCreation()).description(country.getDescription())
                    .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<Country> executeUpdate(Object object){
            if (object instanceof Country country) {
                return countryFinder.findByShortCode(Country.builder().shortCode(country.getShortCode()).build())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                        .map(ca -> Country.builder().id(ca.getId()).name(country.getName())
                                .shortCode(country.getShortCode()).status(country.isStatus())
                                .dateCreation(country.getDateCreation()).description(country.getDescription())
                                .build())
                        .flatMap(countryUpdater::update);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Country> countries){
            TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(countries.size())
                .response(new ArrayList<>(countries))
                .build();

            return Mono.just(response);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.repository y modificar la clase CountryRepository.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.repository;

    import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
    import org.springframework.data.r2dbc.repository.R2dbcRepository;
    import reactor.core.publisher.Mono;

    public interface CountryRepository extends R2dbcRepository<CountryEntity, Long> {
        Mono<CountryEntity> findByShortCode(String shortCode);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql y modificar la clase CountryAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql;

    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.*;
    import co.com.microservice.aws.infrastructure.output.postgresql.mapper.CountryEntityMapper;
    import co.com.microservice.aws.infrastructure.output.postgresql.repository.CountryRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class CountryAdapter implements SavePort<Country>, ListAllPort<Country>,
            UpdatePort<Country>, DeletePort<Country>, FindByShortCodePort<Country> {
        private final CountryEntityMapper mapper;
        private final CountryRepository countryRepository;

        @Override
        public Mono<Country> save(Country country, Context context) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::save)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Flux<Country> listAll(Context context) {
            return countryRepository.findAll().map(mapper::toModelFromEntity);
        }

        @Override
        public Mono<Void> delete(Country country) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::delete);
        }

        @Override
        public Mono<Country> findByShortCode(Country country) {
            return Mono.just(country)
                    .map(Country::getShortCode)
                    .flatMap(countryRepository::findByShortCode)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Mono<Country> update(Country country) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::save)
                    .map(mapper::toModelFromEntity);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.router y modificar la clase CountryRouterRest.java
    ```
    package co.com.microservice.aws.infrastructure.input.rest.api.router;

    import co.com.microservice.aws.infrastructure.input.rest.api.config.RouterProperties;
    import co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.RouterFunctions;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class CountryRouterRest {
        private final RouterProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerCountryFunction(CountryHandler countryHandler) {
            return RouterFunctions.route()
                    .GET(createRoute(properties.getListAll()), countryHandler::listAll)
                    .POST(createRoute(properties.getSave()), countryHandler::save)
                    .DELETE(createRoute(properties.getDelete()), countryHandler::delete)
                    .PUT(createRoute(properties.getUpdate()), countryHandler::update)
                    .GET(createRoute(properties.getFindByShortCode()), countryHandler::findOne)
                    .build();
        }

        private String createRoute(String route){
            return properties.getPathBase().concat(properties.getPathCountries()).concat(route);
        }
    }
    ```
# <div id='id7'/>
# 7. Realizar pruebas (update - delete - findby)

- Curls "findByShortCode"

    ```
    curl --location --request GET 'localhost:8080/api/v1/microservice-aws/country/findByShortCode/CO' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ESP",
        "name": "España",
        "description": "Cuenta con una población estimada de 40 millones de habitantes.",
        "status": true,
        "dateCreation": "2025-07-12T08:00:00"
    }'
    ```

- Curls "delete"

    ```
    curl --location --request DELETE 'localhost:8080/api/v1/microservice-aws/country/delete/7' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ESP",
        "name": "España",
        "description": "Cuenta con una población estimada de 40 millones de habitantes.",
        "status": true,
        "dateCreation": "2025-07-12T08:00:00"
    }'
    ```
    
- Curls "update"

    ```
    curl --location --request PUT 'localhost:8080/api/v1/microservice-aws/country/update' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ESP",
        "name": "España",
        "description": "población de 42 millones",
        "status": true,
        "dateCreation": "2025-07-12T08:00:00"
    }'
    ```

---

# <div id='id8'/>
# 8. Crear la conexión a Mysql

- Se debe reconfigurar la conexión de postgresql para mantener dos conexiones activas en el mismo proyecto

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.config y modificar la clase PostgresConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.config;

    import io.r2dbc.spi.ConnectionFactories;
    import io.r2dbc.spi.ConnectionFactory;
    import io.r2dbc.spi.ConnectionFactoryOptions;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

    import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
    import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

    @Configuration
    @RequiredArgsConstructor
    public class PostgresConfig {

        @Bean(name = "postgresConnectionFactory")
        public ConnectionFactory postgresConnectionFactory(@Value("${adapters.postgresql.url}") String url,
                                                        @Value("${adapters.postgresql.usr}") String usr,
                                                        @Value("${adapters.postgresql.psw}") String psw) {
            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url)
                    .mutate()
                    .option(USER, usr)
                    .option(PASSWORD, psw)
                    .build();

            return ConnectionFactories.get(options);
        }

        @Bean(name = "postgresEntityTemplate")
        public R2dbcEntityTemplate postgresEntityTemplate(@Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
            return new R2dbcEntityTemplate(connectionFactory);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.config y crear la clase PostgresRepositoryConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

    @Configuration
    @EnableR2dbcRepositories(
            basePackages = "co.com.microservice.aws.infrastructure.output.postgresql.repository",
            entityOperationsRef = "postgresEntityTemplate"
    )
    public class PostgresRepositoryConfig {
    }
    ```
    
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql.entity y crear la clase ParameterEntity.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.entity;
    import lombok.*;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;

    import java.time.LocalDateTime;

    @Table(name = "parameters")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ParameterEntity {
        @Id
        private Long id;
        private String name;
        private Boolean value;
        private LocalDateTime dateCreation;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model y crear la clase Parameter.java
    ```
    package co.com.microservice.aws.domain.model;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.io.Serial;
    import java.io.Serializable;
    import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class Parameter implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        
        private Long id;
        private String name;
        private Boolean value;
        private LocalDateTime dateCreation;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql.mapper y crear la clase ParameterEntityMapper.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.mapper;

    import co.com.microservice.aws.domain.model.Parameter;
    import co.com.microservice.aws.infrastructure.output.mysql.entity.ParameterEntity;
    import org.mapstruct.Mapper;

    @Mapper(componentModel = "spring")
    public interface ParameterEntityMapper {
        ParameterEntity toEntityFromModel(Parameter objectModel);
        Parameter toModelFromEntity(ParameterEntity objectEntity);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase ParemeterRepository.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.repository;

    import co.com.microservice.aws.infrastructure.output.mysql.entity.ParameterEntity;
    import org.springframework.data.r2dbc.repository.R2dbcRepository;
    import reactor.core.publisher.Mono;

    public interface ParemeterRepository extends R2dbcRepository<ParameterEntity, Long> {
        Mono<ParameterEntity> findByName(String name);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase FindByNamePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface FindByNamePort<T> {
        Mono<T> findByName(T t);
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql y crear la clase ParameterAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql;

    import co.com.microservice.aws.domain.model.Parameter;
    import co.com.microservice.aws.domain.usecase.out.FindByNamePort;
    import co.com.microservice.aws.infrastructure.output.mysql.mapper.ParameterEntityMapper;
    import co.com.microservice.aws.infrastructure.output.mysql.repository.ParameterRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class ParameterAdapter implements FindByNamePort<Parameter> {
        private final ParameterEntityMapper mapper;
        private final ParameterRepository parameterRepository;

        @Override
        public Mono<Parameter> findByName(Parameter parameter) {
            return Mono.just(parameter)
                    .map(Parameter::getName)
                    .flatMap(parameterRepository::findByName)
                    .map(mapper::toModelFromEntity);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql.config y crear la clase MysqlConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.config;

    import io.r2dbc.spi.ConnectionFactories;
    import io.r2dbc.spi.ConnectionFactory;
    import io.r2dbc.spi.ConnectionFactoryOptions;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

    import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
    import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

    @Configuration
    @RequiredArgsConstructor
    public class MysqlConfig {

        @Bean(name = "mysqlConnectionFactory")
        public ConnectionFactory MysqlConfig(@Value("${adapters.mysql.url}") String url,
                                            @Value("${adapters.mysql.usr}") String usr,
                                            @Value("${adapters.mysql.psw}") String psw) {
            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url)
                    .mutate()
                    .option(USER, usr)
                    .option(PASSWORD, psw)
                    .build();

            return ConnectionFactories.get(options);
        }

        @Bean(name = "mysqlEntityTemplate")
        public R2dbcEntityTemplate mysqlEntityTemplate(@Qualifier("mysqlConnectionFactory") ConnectionFactory connectionFactory) {
            return new R2dbcEntityTemplate(connectionFactory);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql.config y crear la clase MysqlRepositoryConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

    @Configuration
    @EnableR2dbcRepositories(
            basePackages = "co.com.microservice.aws.infrastructure.output.mysql.repository",
            entityOperationsRef = "mysqlEntityTemplate"
    )
    public class MysqlRepositoryConfig {
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase FindByNameUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import reactor.core.publisher.Mono;

    public interface FindByNameUseCase {
        Mono<TransactionResponse> findByName(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase FindByNameUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import reactor.core.publisher.Mono;

    public interface FindByNameUseCase {
        Mono<TransactionResponse> findByName(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase ParameterUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    public interface ParameterUseCase extends FindByNameUseCase{
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase ParameterUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Parameter;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.ParameterUseCase;
    import co.com.microservice.aws.domain.usecase.out.FindByNamePort;
    import co.com.microservice.aws.domain.usecase.out.RedisPort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Collections;
    import java.util.List;

    @UseCase
    @RequiredArgsConstructor
    public class ParameterUseCaseImpl implements ParameterUseCase {
        private final FindByNamePort<Parameter> parameterFinder;
        private final RedisPort redisPort;

        @Override
        public Mono<TransactionResponse> findByName(TransactionRequest request) {
            return Mono.just(request)
                    .map(rq -> Parameter.builder().name(rq.getParams().get("param1")).build())
                    .flatMap(parameterFinder::findByName)
                    .flatMap(pv -> redisPort.save(CacheKey.APPLY_AUDIT.getKey(), pv.toString()).thenReturn(pv))
                    .flatMap(c -> this.buildResponse(List.of(c)));
        }

        private Mono<TransactionResponse> buildResponse(List<Parameter> parameters){
            TransactionResponse response = TransactionResponse.builder()
                    .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                    .size(parameters.size())
                    .response(new ArrayList<>(parameters))
                    .build();

            return Mono.just(response);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.config y crear la clase ParameterLoaderConfig.java para cargar los parámetros desde el inicio de la aplicación, posteriormente estos se podrían cargar en una cache para acceder a su valor, para efectos del ejercicio se imprime en los logs
    ```
    package co.com.microservice.aws.application.config;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.ParameterUseCase;
    import lombok.RequiredArgsConstructor;
    import org.springframework.boot.context.event.ApplicationReadyEvent;
    import org.springframework.context.event.EventListener;
    import org.springframework.core.annotation.Order;
    import org.springframework.stereotype.Component;

    import java.util.Map;
    import java.util.UUID;

    @Component
    @RequiredArgsConstructor
    public class ParameterLoaderConfig {
        private final ParameterUseCase useCasefinder;
        private final LoggerBuilder logger;

        @Order(1)
        @EventListener(ApplicationReadyEvent.class)
        public void initialParamterStatus() {
            var parameters = Map.of("param1", "Aply_audit", "param2", "Message_in_spanish");
            TransactionRequest req = TransactionRequest.builder().params(parameters).build();

            useCasefinder.findByName(req)
                    .doOnNext(param -> logger.info(
                            TransactionLog.Response.builder().body(param).build(),
                            "List parameters", UUID.randomUUID().toString(),
                            "ParameterLoaderConfig", "initialParamterStatus"))
                    .doOnError(error -> logger.info("Error al cargar parámetros: " + error.getMessage(), "", "", ""))
                    .subscribe();
        }
    }
    ```

# <div id='id9'/>
# 9. Crear la instancia de base de datos en Podman

- Abrir la consola de comandos para descargar la imagen de Postgresql y subir el contenedor en Podman o Docker
    ```
    podman machine start
    podman pull docker.io/library/mysql:8.4
    ```
- Ejecutar el contenedor
    ```
    podman run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=my_mysql_db -e MYSQL_USER=myroot -e MYSQL_PASSWORD=myroot123 -p 3306:3306 mysql:8.4
    ```
- Conectar a la bd con DBeaver

    ![](./img/modules/3_connection_dbeaver_mysql.png)

- Creamos la tabla
    ```
    CREATE TABLE IF NOT EXISTS parameters (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        value BOOLEAN NOT NULL DEFAULT TRUE,
        date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    ```

- Realizamos el insert de la información
    ```
    INSERT INTO parameters (name) VALUES
    ('Aply_audit'),
    ('Message_in_spanish');
    ```

- Logs de cargar el parámetro: inicia la aplicación y posteriormente muestra la información del parámetro
    ```
    {
        "instant": {
            "epochSecond": 1752607560,
            "nanoOfSecond": 762734500
        },
        "thread": "restartedMain",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.MicroserviceAwsApplication",
        "message": "Started MicroserviceAwsApplication in 2.812 seconds (process running for 3.716)",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.commons.logging.LogAdapter$Log4jLog",
        "threadId": 42,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1752607560,
            "nanoOfSecond": 996090700
        },
        "thread": "ForkJoinPool.commonPool-worker-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"List parameters\",\"messageId\":\"df13976d-3e95-467d-bed1-12d797814435\",\"service\":\"ParameterLoaderConfig\",\"method\":\"initialParamterStatus\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"message\":\"Listed successfull!\",\"size\":1,\"response\":[[{\"id\":1,\"name\":\"Aply_audit\",\"value\":true,\"dateCreation\":\"2025-07-15T16:50:56\"}]]}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 59,
        "threadPriority": 5
    }
    ```
# <div id='id10'/>
# 10. Crear conexión secrets-manager

Si bien la configuración de las conexiones a la base de datos se pueden realizar desde un pipeline release, esta información se considera sensible en ambientes productivos, por lo tanto, lo mejor es usar el servicio de secrets-manager de AWS

- Ubicarse en el archivo application.yaml y cambiar driven adapters por lo siguiente: eliminamos las conexiones a bd ya que se guardarán en un secreto.
```
adapters:
  secrets-manager:
    region: "${AWS_REGION:us-east-1}"
    endpoint: ${PARAM_URL:http://localhost:4566}
    namePostgresql: "${SECRET_NAME_POSTGRE:local-postgresql}"
    nameMysql: "${SECRET_NAME_MYSQL:local-mysql}"
    nameRedis: "${SECRET_NAME_REDIS:local-redis}"
    nameRabbitMq: "${SECRET_NAME_RABBIT_MQ:local-rabbitmq}"
  redis:
    expireTime: ${CACHE_EXPIRE_SECONDS:10}
```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.secretsmanager y crear la clase SecretsConnectionProperties.java
    ```
    package co.com.microservice.aws.application.helpers.secretsmanager;

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
        private String endpoint;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.secretsmanager y crear la clase SecretsManagerAsyncConfig.java
    ```
    package co.com.microservice.aws.application.helpers.secretsmanager;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;

    import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
    import software.amazon.awssdk.regions.Region;
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

    import java.net.URI;

    @Configuration
    public class SecretsManagerAsyncConfig {
        public static final String AWS_SECRET_MANAGER_ASYNC = "awsSecretManagerSyncConnector";

        @Profile("!local")
        @Bean(name = AWS_SECRET_MANAGER_ASYNC)
        public SecretsManagerClient secretsManagerClient(final SecretsConnectionProperties properties) {
            return SecretsManagerClient.builder()
                    .region(Region.of(properties.getRegion()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }

        @Profile("local")
        @Bean(name = AWS_SECRET_MANAGER_ASYNC)
        public SecretsManagerClient localManagerAsync(final SecretsConnectionProperties properties) {
            return SecretsManagerClient.builder()
                    .endpointOverride(URI.create(properties.getEndpoint()))
                    .region(Region.of(properties.getRegion()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.utils y crear la clase SecretUtil.java
    ```
    package co.com.microservice.aws.application.helpers.utils;

    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.experimental.UtilityClass;

    import java.io.IOException;
    import java.util.Collections;
    import java.util.Map;

    @UtilityClass
    public class SecretUtil {

        public static Map<String, String> parseSecret(String json) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, new TypeReference<>() {});
            } catch (IOException e) {
                return Collections.emptyMap();
            }
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.config y modificar la clase PostgresConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.config;

    import co.com.microservice.aws.application.helpers.utils.SecretUtil;
    import io.r2dbc.spi.ConnectionFactories;
    import io.r2dbc.spi.ConnectionFactory;
    import io.r2dbc.spi.ConnectionFactoryOptions;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

    import java.util.Map;

    import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
    import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

    @Configuration
    public class PostgresConfig {
        private final SecretsManagerClient secretsClient;
        private final String secretNameBd;

        public PostgresConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                            @Value("${adapters.secrets-manager.namePostgresql}") String secretNameBd){
            this.secretsClient = secretsClient;
            this.secretNameBd = secretNameBd;
        }

        @Bean(name = "postgresConnectionFactory")
        public ConnectionFactory postgresConnectionFactory() {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretNameBd)
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            String secretJson = response.secretString();

            Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(secrets.get("url"))
                    .mutate()
                    .option(USER, secrets.get("usr"))
                    .option(PASSWORD, secrets.get("psw"))
                    .build();

            return ConnectionFactories.get(options);
        }

        @Bean(name = "postgresEntityTemplate")
        public R2dbcEntityTemplate postgresEntityTemplate(
                @Qualifier("postgresConnectionFactory") ConnectionFactory connectionFactory) {
            return new R2dbcEntityTemplate(connectionFactory);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.mysql.config y modificar la clase MysqlConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.mysql.config;

    import co.com.microservice.aws.application.helpers.utils.SecretUtil;
    import io.r2dbc.spi.ConnectionFactories;
    import io.r2dbc.spi.ConnectionFactory;
    import io.r2dbc.spi.ConnectionFactoryOptions;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

    import java.util.Map;

    import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
    import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

    @Configuration
    public class MysqlConfig {
        private final SecretsManagerClient secretsClient;
        private final String secretNameBd;

        public MysqlConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                            @Value("${adapters.secrets-manager.nameMysql}") String secretNameBd){
            this.secretsClient = secretsClient;
            this.secretNameBd = secretNameBd;
        }

        @Bean(name = "mysqlConnectionFactory")
        public ConnectionFactory mysqlConfig() {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretNameBd)
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            String secretJson = response.secretString();

            Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

            ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(secrets.get("url"))
                    .mutate()
                    .option(USER, secrets.get("usr"))
                    .option(PASSWORD, secrets.get("psw"))
                    .build();

            return ConnectionFactories.get(options);
        }

        @Bean(name = "mysqlEntityTemplate")
        public R2dbcEntityTemplate mysqlEntityTemplate(@Qualifier("mysqlConnectionFactory") ConnectionFactory connectionFactory) {
            return new R2dbcEntityTemplate(connectionFactory);
        }
    }
    ```

# <div id='id11'/>
# 11. Crear secretos en podman (Postgresql y mysql)

- Preparar el ambiente local creando los secretos, en la consola de comandos (windows en este caso) ejecutar los siguientes:
    ```
    podman start localstack

    aws secretsmanager create-secret --name local-postgresql --description "Connection to local PostgreSQL" --secret-string "{\"url\":\"r2dbc:postgresql://localhost:5432/my_postgres_db\",\"usr\":\"postgres\",\"psw\":\"123456\"}" --endpoint-url=http://localhost:4566

    aws secretsmanager create-secret --name local-mysql --description "Conexión local a MySQL para microservicio" --secret-string "{\"url\":\"r2dbc:mysql://localhost:3306/my_mysql_db\",\"usr\":\"myroot\",\"psw\":\"myroot123\"}" --endpoint-url http://localhost:4566

    ```

- Ejecutar la aplicación y todo debe continuar funcionando, los servicios de postman y el log de imprimir la información del parámetro

# <div id='id12'/>
# 12. Crear conexión redis-cache

- Agregar en el archivo build.gradle
    ```
    implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'
        implementation 'io.lettuce:lettuce-core'
    ```

- Ubicarse en el paquete co.com.microservice.aws y modificar la clase MicroserviceAwsApplication.java
    ```
    package co.com.microservice.aws;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication(exclude = {
            org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration.class
    })
    public class MicroserviceAwsApplication {
        public static void main(String[] args) {
            SpringApplication.run(MicroserviceAwsApplication.class, args);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.redis.config y crear la clase RedisConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.redis.config;

    import co.com.microservice.aws.application.helpers.utils.SecretUtil;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
    import org.springframework.data.redis.connection.RedisPassword;
    import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
    import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
    import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

    import java.util.Map;

    @Configuration
    public class RedisConfig {
        private final SecretsManagerClient secretsClient;
        private final String secretNameRedis;

        public RedisConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                            @Value("${adapters.secrets-manager.nameRedis}") String secretNameRedis){
            this.secretsClient = secretsClient;
            this.secretNameRedis = secretNameRedis;
        }

        @Bean(name = "customRedisConnectionFactory")
        public ReactiveRedisConnectionFactory redisConnectionFactory() {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretNameRedis)
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            String secretJson = response.secretString();

            Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(secrets.get("host"));
            config.setPort(Integer.parseInt(secrets.get("port")));
            config.setPassword(RedisPassword.of(secrets.get("password")));

            return new LettuceConnectionFactory(config);
        }

        @Bean
        public ReactiveStringRedisTemplate reactiveRedisTemplate(
                @Qualifier("customRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
            return new ReactiveStringRedisTemplate(factory);
        }
    }
    ```
- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.redis.repository y crear la clase RedisCacheRepository.java
    ```
    package co.com.microservice.aws.infrastructure.output.redis.repository;

    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
    import org.springframework.stereotype.Repository;
    import reactor.core.publisher.Mono;

    import java.time.Duration;

    @Repository
    @RequiredArgsConstructor
    public class RedisCacheRepository {
        private final ReactiveStringRedisTemplate redisTemplate;

        @Value("${adapters.redis.expireTime}")
        private int durationDefault;

        public Mono<Boolean> save(String key, String value) {
            return redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(durationDefault));
        }

        public Mono<String> find(String key) {
            return redisTemplate.opsForValue().get(key);
        }

        public Mono<Boolean> delete(String key) {
            return redisTemplate.opsForValue().delete(key);
        }

        public Mono<Boolean> exists(String key) {
            return redisTemplate.hasKey(key);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase RedisPort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface RedisPort {
        Mono<String> find(String key);
        Mono<Boolean> save(String key, String value);
        Mono<Boolean> delete(String key);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y crear la clase CacheKey.java
    ```
    package co.com.microservice.aws.domain.model.commons.enums;

    import lombok.Getter;
    import lombok.RequiredArgsConstructor;

    @Getter
    @RequiredArgsConstructor
    public enum CacheKey {
        APPLY_AUDIT("APPLY_AUDIT"),
        KEY_DEFAULT("KEY_DEFAULT");

        private final String key;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.redis y crear la clase RedisAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.redis;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.domain.usecase.out.RedisPort;
    import co.com.microservice.aws.infrastructure.output.redis.repository.RedisCacheRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class RedisAdapter implements RedisPort {
        private final RedisCacheRepository redisRepository;
        private final LoggerBuilder logger;

        @Override
        public Mono<String> find(String key) {
            return redisRepository.find(key).doOnNext(logger::info);
        }

        @Override
        public Mono<Boolean> save(String key, String value) {
            return redisRepository.save(key, value);
        }

        @Override
        public Mono<Boolean> delete(String key) {
            return redisRepository.delete(key);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y modificar la clase ParameterUseCase.java, al momento de obtener el parámetro este será guardado en la cache redis configurada
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Parameter;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.FindByNameUseCase;
    import co.com.microservice.aws.domain.usecase.out.FindByNamePort;
    import co.com.microservice.aws.domain.usecase.out.RedisPort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Collections;
    import java.util.List;

    @UseCase
    @RequiredArgsConstructor
    public class ParameterUseCase implements FindByNameUseCase {
        private final FindByNamePort<Parameter> parameterFinder;
        private final RedisPort redisPort;

        @Override
        public Mono<TransactionResponse> findByName(TransactionRequest request) {
            return Mono.just(request)
                    .map(rq -> Parameter.builder().name(rq.getParams().get("param1")).build())
                    .flatMap(parameterFinder::findByName)
                    .flatMap(pv -> redisPort.save(CacheKey.APPLY_AUDIT.getKey(), pv.toString()).thenReturn(pv))
                    .flatMap(c -> this.buildResponse(List.of(c)));
        }

        private Mono<TransactionResponse> buildResponse(List<Parameter> parameters){
            TransactionResponse response = TransactionResponse.builder()
                    .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                    .size(parameters.size())
                    .response(Collections.singletonList(parameters))
                    .build();

            return Mono.just(response);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.config y crear la clase RedisDefaultConfig.java, esta funcionalidad permite crear una clave valor por defecto en redis cache
    ```
    package co.com.microservice.aws.application.config;

    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.usecase.out.RedisPort;
    import lombok.RequiredArgsConstructor;
    import org.springframework.boot.context.event.ApplicationReadyEvent;
    import org.springframework.context.event.EventListener;
    import org.springframework.core.annotation.Order;
    import org.springframework.stereotype.Component;

    @Component
    @RequiredArgsConstructor
    public class RedisDefaultConfig {
        private final RedisPort redisPort;

        @Order(2)
        @EventListener(ApplicationReadyEvent.class)
        public void initialDefaultRedis() {
            redisPort.save(CacheKey.KEY_DEFAULT.getKey(), "Value by started application")
                    .subscribe();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y modificar la clase CountryUseCaseImpl.java para consultar el valor de la cache y para efectos de la prueba se imprime en los logs
    ```
    Carga la aplicación, busca el parámetro y lo guarda en la cache con el código: APPLY_AUDIT
    {
        "instant": {
            "epochSecond": 1752635718,
            "nanoOfSecond": 326958800
        },
        "thread": "lettuce-nioEventLoop-5-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"List parameters\",\"messageId\":\"07316988-e443-4402-96b1-9e99c76d44d1\",\"service\":\"ParameterLoaderConfig\",\"method\":\"initialParamterStatus\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"message\":\"Listed successfull!\",\"size\":1,\"response\":[[{\"id\":1,\"name\":\"Aply_audit\",\"value\":true,\"dateCreation\":\"2025-07-15T16:50:56\"}]]}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 64,
        "threadPriority": 5
    }

    Consumimos el servicio de listar
    {
        "instant": {
            "epochSecond": 1752635746,
            "nanoOfSecond": 625991200
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"List all records\",\"messageId\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"7a214936-5e93-11ec-bf63-0242ac130002\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 66,
        "threadPriority": 5
    }

    Mostramos el acceso al dato almacenado en Redis con la clave: APPLY_AUDIT
    {
        "instant": {
            "epochSecond": 1752635746,
            "nanoOfSecond": 639269500
        },
        "thread": "lettuce-nioEventLoop-5-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "Parameter(id=1, name=Aply_audit, value=true, dateCreation=2025-07-15T16:50:56)",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 64,
        "threadPriority": 5
    }
    ```

# <div id='id13'/>
# 13. Crear instancia en podman

- Comandos podman para acceder a Redis Cache Local y ver la información
    ```
    -- Descargar la imagen
    podman run -d --name redis-container -p 6379:6379 docker.io/library/redis:latest

    -- Listar los contenedores en ejecución
    podman ps
    
    -- Elegimos el nombre que le hemos dado al contendor, en este caso: redis-container

    -- Crear secreto
    aws secretsmanager create-secret --name local-redis --description "Connection to Redis" --secret-string "{\"host\":\"localhost\",\"port\":\"6379\",\"password\":\"\"}" --endpoint-url=http://localhost:4566

    -- Ingresar al CLI del contenedor
    podman exec -it redis-container redis-cli

    -- Obtener el valor de la clave en Redis
    get APPLY_AUDIT

    -- 10 minutos después cuando se consulte el parámetro ya no existe
    127.0.0.1:6379> get APPLY_AUDIT
    (nil)

    -- Obtener el valor de la clave guardada por defecto al iniciar la aplicación, clave: KEY_DEFAULT
    127.0.0.1:6379> get KEY_DEFAULT
    "Value by started application"
    ```

# <div id='id14'/>
# 14. Configurar uso de CRON

- Tabla general de caracteres especiales en expresiones CRON

| Símbolo | Significado | Ejemplo | Explicación |
| ------- | ----------- | ------- | ----------- |
| `*`     | Todos los valores posibles         | `* * * * * *`       | Ejecuta cada segundo, minuto, hora, día, mes, día de la semana.                                 |
| `?`     | Sin especificar                    | `0 0 12 * * ?`      | Ejecuta a las 12:00 pm todos los días (cuando no necesitas día del mes **y** día de la semana). |
| `*/n`   | Cada `n` unidades                  | `0 */5 * * * ?`     | Cada 5 minutos.                                                                                 |
| `n1-n2` | Rango de valores                   | `0 0 9-17 * * ?`    | Cada hora entre 9 am y 5 pm.                                                                    |
| `n1,n2` | Lista de valores específicos       | `0 0 8,14,20 * * ?` | A las 8 am, 2 pm y 8 pm.                                                                        |
| `L`     | Último día del mes o semana        | `0 0 0 L * ?`       | A medianoche del **último día del mes**.                                                        |
| `W`     | Día hábil más cercano al día dado  | `0 0 0 15W * ?`     | Día hábil más cercano al día 15 del mes.                                                        |
| `#`     | N.º de ocurrencia de un día en mes | `0 0 8 ? * 2#1`     | Primer lunes (`2`) del mes a las 8 am.                                                          |

- En el archivo application.yaml agregar la configuración del timer para el cron
```
entries:
  web:
    path-base: "${PATH_BASE:/api/v1/microservice-aws}"
    path-countries: "${PATH_COUNTRY:/country}"
    listAll: "/list-all"
    findByShortCode: "/findByShortCode/{shortCode}"
    save: "/save"
    update: "/update"
    delete: "/delete/{id}"
  regex-body-wr:
    name: "${REGEX_COUNTRY_NAME:^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$}"
    codeShort: "${REGEX_COUNTRY_CODE_SHORT:^[a-zA-Z]{3,4}$}"
  properties:
    expression-timer: "${EXPRESSION_TIMER:0 */5 * * * ?}"
    process-on-schedule: "${PROCESS_ON_SCHEDULE:Y}"
```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.backgroudtask y crear la clase BackgroundTasks.java
    ```
    package co.com.microservice.aws.infrastructure.input.backgroudtask;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.usecase.out.RedisPort;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.scheduling.annotation.EnableScheduling;
    import org.springframework.scheduling.annotation.Scheduled;

    @EnableScheduling
    @Configuration
    @EnableAutoConfiguration
    @RequiredArgsConstructor
    public class BackgroundTasks {
        private static final String FLAG_PROCESS_YES = "Y";

        private final RedisPort redisPort;
        private final LoggerBuilder logger;

        @Value("${entries.properties.process-on-schedule}")
        private String processOnSchedule;

        @Scheduled(cron = "${entries.properties.expression-timer}")
        public void updateRedisKeyDefault() {
            if (processOnSchedule.equals(FLAG_PROCESS_YES)) {
                logger.info("Executed cron");
                redisPort.save(CacheKey.KEY_DEFAULT.getKey(), "Value modified by cron after five minutes")
                        .subscribe();
            }
        }
    }
    ```

- Explicación del uso de CRON

| Posición | Valor | Significado | Descripción |
| -------- | ----- | ----------- | ------------|
| 1        | `0`   | **Segundos**    | Ejecuta cuando el segundo sea 0    |
| 2        | `*/5` | **Minutos**     | Cada 5 minutos (`5, 10, 15...`) |
| 3        | `*`   | **Horas**       | Cualquier hora                     |
| 4        | `*`   | **Día del mes** | Cualquier día                      |
| 5        | `*`   | **Mes**         | Cualquier mes                      |
| 6        | `?`   | **Día de la semana** (`?` o específico) | `?` indica que **no se especifica** (se usa cuando ya se definió día del mes) |


# <div id='id15'/>
# 15. Logs y validación en redis

- Resultado logs y valor en Redis cache local
    ```
    {
        "instant": {
            "epochSecond": 1752642393,
            "nanoOfSecond": 810350500
        },
        "thread": "restartedMain",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "Executed key default",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 42,
        "threadPriority": 5
    }

    -- Obtener el valor de la clave guardada por defecto al iniciar la aplicación, clave: KEY_DEFAULT
    127.0.0.1:6379> get KEY_DEFAULT
    "Value by started application"

    -- five minutes latter...

    127.0.0.1:6379> get KEY_DEFAULT
    "Value modified by cron after five minutes"

    {
        "instant": {
            "epochSecond": 1752719400,
            "nanoOfSecond": 36943000
        },
        "thread": "scheduling-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "Executed cron",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 60,
        "threadPriority": 5
    }

    ```

    **Nota:** El cron se ejecuta cuando el reloj marca el minuto 5, para efectos de la prueba se activó la aplicación a las 6:00pm y a las 6:05pm se ejecutó, si se lanza a las 6:04pm, el cron se ejecutará a las 6:05pm, justo cuando marca el minuto 05, 10, 15... 

# <div id='id16'/>
# 16. Configurar Rabbit MQ

💡 [>> Overview reactive-commons](https://bancolombia.github.io/reactive-commons-java/docs/intro)

💡 [>> Examples reactive-commons](https://github.com/reactive-commons)

- Ubicarse en el archivo build.gradle y modificar
    ```
    ext {
        awsSdkVersion = '2.25.17'
        reactiveCommonsVersion = '4.1.4'
    }

    dependencias {
        //... otras dependencias

        implementation "org.reactivecommons:async-commons-rabbit-starter:${reactiveCommonsVersion}"
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.rabbiteventbus.config y crear la clase RabbitMqConfig.java con uso de secrets-manager
    ```
    package co.com.microservice.aws.infrastructure.output.rabbiteventbus.config;

    import co.com.microservice.aws.application.helpers.utils.SecretUtil;
    import org.reactivecommons.async.rabbit.config.RabbitProperties;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Primary;
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

    import java.util.Map;

    @Configuration
    public class RabbitMqConfig {
        private final SecretsManagerClient secretsClient;
        private final String secretNameRabbit;

        public RabbitMqConfig(@Qualifier("awsSecretManagerSyncConnector") SecretsManagerClient secretsClient,
                            @Value("${adapters.secrets-manager.nameRabbitMq}") String secretNameRabbit){
            this.secretsClient = secretsClient;
            this.secretNameRabbit = secretNameRabbit;
        }

        @Primary
        @Bean
        public RabbitProperties customRabbitProperties() {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretNameRabbit)
                    .build();

            GetSecretValueResponse response = secretsClient.getSecretValue(request);
            String secretJson = response.secretString();

            Map<String, String> secrets = SecretUtil.parseSecret(secretJson);

            RabbitProperties properties = new RabbitProperties();
            properties.setHost(secrets.get("hostname"));
            properties.setPort(Integer.parseInt(secrets.get("port")));
            properties.setVirtualHost(secrets.get("virtualhost"));
            properties.setUsername(secrets.get("username"));
            properties.setPassword(secrets.get("password"));
            return properties;
        }
    }
    ```

# <div id='id17'/>
# 17. Publicador

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase Event.java
    ```
    package co.com.microservice.aws.domain.model.events;

    import java.io.Serial;
    import java.io.Serializable;

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
    public class Event<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String type;
        private String specVersion;
        private String source;
        private String id;
        private String time;
        protected String invoker;
        private String dataContentType;

        protected transient T data;

        public String getEventId() {
            return id.concat("-".concat(type));
        }

        public Event<T> complete(String source, String specVersion, String dataContentType) {
            this.setSource(source);
            this.setSpecVersion(specVersion);
            this.setDataContentType(dataContentType);
            return this;
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase EventData.java

    ```
    package co.com.microservice.aws.domain.model.events;

    import co.com.microservice.aws.domain.model.rq.Context;
    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class EventData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Context contextHeaders;
        private transient Object data;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase EventType.java

    ```
    package co.com.microservice.aws.domain.model.events;

    import lombok.experimental.UtilityClass;

    @UtilityClass
    public class EventType {
        public static final String EVENT_EMMITED_NOTIFICATION_SAVE = "myapp.notification.example-event-emited";
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository y crear la clase EventOperations.java

    ```
    package co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.events.Event;
    import lombok.RequiredArgsConstructor;
    import org.reactivecommons.api.domain.DomainEvent;
    import org.reactivecommons.api.domain.DomainEventBus;
    import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
    import reactor.core.publisher.Mono;

    import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
    import static reactor.core.publisher.Mono.from;

    @EnableDomainEventBus
    @RequiredArgsConstructor
    public class EventOperations {
        private static final String NAME_CLASS = EventOperations.class.getName();
        private static final String SPEC_VERSION = "1";
        private static final String APPLICATION_NAME = "microservice-aws";
        private static final String MSG_EVENT_EMITTED = "Event emitted";
        private final DomainEventBus domainEventBus;
        private final LoggerBuilder logger;

        public Mono<Void> emitEvent(Event<?> event, String messageId) {
            return generateDomainEvent(event).flatMap(domainEvent -> from(domainEventBus.emit(domainEvent)))
                .doOnSuccess(e ->
                    logger.info(TransactionLog.Request.builder().body(event).build(), MSG_EVENT_EMITTED,
                            messageId, "generateDomainEvent", NAME_CLASS))
                .onErrorResume(this::printErroEmit);
        }

        private Mono<DomainEvent<?>> generateDomainEvent(Event<?> incompleteEvent) {
            return Mono.just(APPLICATION_NAME)
                    .map(app -> incompleteEvent.complete(app, SPEC_VERSION, APPLICATION_JSON_VALUE))
                    .map(event -> new DomainEvent<>(event.getType(), event.getId(), event));
        }

        private Mono<Void> printErroEmit(Throwable throwable) {
            logger.error(throwable);
            return Mono.empty();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase EventPort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import co.com.microservice.aws.domain.model.events.Event;
    import reactor.core.publisher.Mono;

    public interface EventPort {
        Mono<Void> emitEvent(Event<Object> event, String messageId);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.rabbiteventbus y crear la clase ReactiveEventAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.rabbiteventbus;

    import co.com.microservice.aws.domain.model.events.Event;
    import co.com.microservice.aws.domain.usecase.out.EventPort;
    import co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository.EventOperations;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class ReactiveEventAdapter implements EventPort {
        private final EventOperations eventOperations;

        @Override
        public Mono<Void> emitEvent(Event<Object> event, String messageId) {
            return Mono.just(event).flatMap(e -> eventOperations.emitEvent(e, messageId));
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase SentEventUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.Context;

    public interface SentEventUseCase {
        void sentEvent(Context context, String typeEvent, Object response);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase SentEventUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.events.Event;
    import co.com.microservice.aws.domain.model.events.EventData;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.in.SentEventUseCase;
    import co.com.microservice.aws.domain.usecase.out.EventPort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;
    import reactor.core.scheduler.Schedulers;

    import java.time.LocalDateTime;
    import java.util.UUID;

    @UseCase
    @RequiredArgsConstructor
    public class SentEventUseCaseImpl implements SentEventUseCase {
        private static final String INVOKER = "From-My-App";
        private final EventPort eventPort;

        @Override
        public void sentEvent(Context context, String typeEvent, Object response) {
            Mono.defer(() -> emitEvent(
                    buildEvent(context, response), typeEvent, context.getId()
                ).subscribeOn(Schedulers.single())).subscribe();
        }

        private Mono<Void> emitEvent(Event<Object> event, String typeEvent, String messageId) {
            event.setId(UUID.randomUUID().toString());
            event.setType(typeEvent);
            event.setTime(LocalDateTime.now().toString());
            event.setInvoker(INVOKER);
            return eventPort.emitEvent(event, messageId);
        }

        private static Event<Object> buildEvent(Context context, Object response) {
            return Event.builder().data(EventData.builder().contextHeaders(context).data(response).build()).build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y modificar la clase CountryUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.*;
    import co.com.microservice.aws.domain.usecase.out.*;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_RECORD_NOT_FOUND;
    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;
    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;
    import static co.com.microservice.aws.domain.model.events.EventType.EVENT_EMMITED_NOTIFICATION_SAVE;

    @UseCase
    @RequiredArgsConstructor
    public class CountryUseCaseImpl implements CountryUseCase {
        private final SavePort<Country> countrySaver;
        private final ListAllPort<Country> countryLister;
        private final UpdatePort<Country> countryUpdater;
        private final DeletePort<Country> countryDeleter;
        private final FindByShortCodePort<Country> countryFinder;
        private final RedisPort redisPort;
        private final SentEventUseCase eventUseCase;

        @Override
        public Mono<TransactionResponse> listAll(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> redisPort.find(CacheKey.APPLY_AUDIT.getKey()).thenReturn(req))
                .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> save(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(country -> countrySaver.save(country, request.getContext()))
                .doOnNext(country -> eventUseCase.sentEvent(request.getContext(),
                        EVENT_EMMITED_NOTIFICATION_SAVE, Country.builder().name(country.getName()).description(country.getDescription()).build()))
                .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
        }

        @Override
        public Mono<String> delete(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().id(Long.valueOf(rq.getParams().get("id"))).build())
                    .flatMap(countryDeleter::delete)
                    .thenReturn(ResponseMessageConstant.MSG_DELETED_SUCCESS);
        }

        @Override
        public Mono<TransactionResponse> findByShortCode(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().shortCode(rq.getParams().get("shortCode")).build())
                    .flatMap(countryFinder::findByShortCode)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                    .flatMap(c -> this.buildResponse(List.of(c))
                    ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> update(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::executeUpdate)
                    .thenReturn(ResponseMessageConstant.MSG_UPDATED_SUCCESS);
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                    .shortCode(country.getShortCode()).status(country.isStatus())
                    .dateCreation(country.getDateCreation()).description(country.getDescription())
                    .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<Country> executeUpdate(Object object){
            if (object instanceof Country country) {
                return countryFinder.findByShortCode(Country.builder().shortCode(country.getShortCode()).build())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                        .map(ca -> Country.builder().id(ca.getId()).name(country.getName())
                                .shortCode(country.getShortCode()).status(country.isStatus())
                                .dateCreation(country.getDateCreation()).description(country.getDescription())
                                .build())
                        .flatMap(countryUpdater::update);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Country> countries){
            TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(countries.size())
                .response(new ArrayList<>(countries))
                .build();

            return Mono.just(response);
        }
    }
    ```
# <div id='id18'/>
# 18. Publicador pruebas

- Crear ambiente RabbitMQ local
    ```
    podman machine start

    podman start localstack

    podman run -d --name rabbitmq-container -p 5672:5672 -p 15672:15672 docker.io/library/rabbitmq:latest

    podman exec -it rabbitmq-container rabbitmq-plugins enable rabbitmq_management
    ```

- Crear secreto
    ```
    aws secretsmanager create-secret --name local-rabbitmq --description "Connection to RabbitMQ" --secret-string "{\"virtualhost\":\"/\",\"hostname\":\"localhost\",\"username\":\"guest\",\"password\":\"guest\",\"port\":5672}" --endpoint-url=http://localhost:4566
    ```

- Configurar cola en rabbit para ver los mensajes emitidos
    - Ingresar a: http://localhost:15672
    - Elegir en el menú: Queues and streams
    - Ubicarse en: Add a new queue
    - Dar un nombre: name: test-queue
    - Dar a boton: Add queue

    ![](./img/modules/5_rabbit_config_queue.png)

    - Seleccionar el registro de la cola creada: test-queue
    - Seleccionar la sección de bindings
    - Escribir en From exchange: domainEvents
    - Escribir en Routing key: myapp.notification.example-event-emited
    - Presionar el boton Bind

    ![](./img/modules/5_rabbit_config_queue_binding.png)

- Ejecutar la aplicación y ejecutar el metodo save country
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/save' \
    --header 'user-name: usertest' \
    --header 'message-id: 9999999-9999-0001' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ECU",
        "name": "Ecuador",
        "description": "Cuenta con una población estimada de 10 millones de habitantes.",
        "status": true,
        "dateCreation": "2025-07-12T08:00:00"
    }'
    ```

# <div id='id19'/>
# 19. Publicador logs

- Ver logs: guardar y e información del evento emitido, para el caso ejemplo message-id es 9999999-9999-0001
    ```
    {
        "instant": {
            "epochSecond": 1752814380,
            "nanoOfSecond": 154895700
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Save one record\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"9999999-9999-0001\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 85,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1752814380,
            "nanoOfSecond": 478357000
        },
        "thread": "RMessageSender1-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Event emitted\",\"messageId\":\"9999999-9999-0001\",\"service\":\"generateDomainEvent\",\"method\":\"co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository.EventOperations\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"type\":\"myapp.notification.example-event-emited\",\"specVersion\":\"1\",\"source\":\"microservice-aws\",\"id\":\"04356cd6-8253-4800-a93e-0fe13d4fb505\",\"time\":\"2025-07-17T23:53:00.453305400\",\"invoker\":\"From-My-App\",\"dataContentType\":\"application/json\",\"data\":{\"contextHeaders\":{\"id\":\"9999999-9999-0001\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}},\"data\":{\"id\":null,\"shortCode\":null,\"name\":\"Ecuador\",\"description\":\"Cuenta con una población estimada de 10 millones de habitantes.\",\"status\":false,\"dateCreation\":null}},\"eventId\":\"04356cd6-8253-4800-a93e-0fe13d4fb505-myapp.notification.example-event-emited\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 105,
        "threadPriority": 5
    }
    ```

- Ver mensaje en la cola creada
    - Ubicarse en la cola: test-queue
    - Ubicarse en la sección de GetMessage
    - Presionar boton Get Message(s)
    - Ver lo que se evidencia en PayLoad

    ```
    {
        "name": "myapp.notification.example-event-emited",
        "eventId": "ce8fbd41-0e7d-470b-abdc-123a12d27542",
        "data": {
            "type": "myapp.notification.example-event-emited",
            "specVersion": "1",
            "source": "microservice-aws",
            "id": "ce8fbd41-0e7d-470b-abdc-123a12d27542",
            "time": "2025-07-18T16:30:15.001640700",
            "invoker": "From-My-App",
            "dataContentType": "application/json",
            "data": {
                "headers": {
                    "user-name": "usertest",
                    "platform-type": "postman",
                    "ip": "172.34.45.12",
                    "id": "9999999-9999-0001",
                    "user-agent": "application/json"
                },
                "data": {
                    "id": null,
                    "shortCode": null,
                    "name": "Ecuador",
                    "description": "Cuenta con una población estimada de 10 millones de habitantes.",
                    "status": false,
                    "dateCreation": null
                }
            },
            "eventId": "ce8fbd41-0e7d-470b-abdc-123a12d27542-myapp.notification.example-event-emited"
        }
    }
    ```

    ![](./img/modules/5_rabbit_config_queue_get-message.png)

# <div id='id20'/>
# 20. Consumidor

- Ubicarse en el archivo application-local.yaml y colocar la siguiente información: corresponde a los eventos que vamos a estar escuchando.
```
listen:
  event:
    names:
      saveCountry: "${EVENT_NAME_SAVE_COUNTRY:business.myapp.save.country}"
      saveCacheCountCountry: "${EVENT_NAME_COUNT_IN_CACHE_COUNTRY:business.myapp.save-cache-count.country}"
```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.listenevent.config y crear la clase EventNameProperties.java
    ```
    package co.com.microservice.aws.infrastructure.input.listenevent.config;

    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.annotation.Configuration;

    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "listen.event.names")
    public class EventNameProperties {
        private String saveCountry;
        private String saveCacheCountCountry;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.listenevent.util y crear la clase EventData.java
    ```
    package co.com.microservice.aws.infrastructure.input.listenevent.util;

    import com.fasterxml.jackson.databind.DeserializationFeature;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.experimental.UtilityClass;
    import org.reactivecommons.api.domain.DomainEvent;

    @UtilityClass
    public class EventData {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        static {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        public static <T> T getValueData(DomainEvent<Object> event, Class<T> clazz) {
            return objectMapper.convertValue(event.getData(), clazz);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase Headers.java
    ```
    package co.com.microservice.aws.domain.model.events;

    import com.fasterxml.jackson.annotation.JsonAlias;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.io.Serial;
    import java.io.Serializable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Headers implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonAlias({ "message-id" })
        private String messageId;
        private String ip;
        @JsonAlias({ "user-name" })
        private String username;
        @JsonAlias({ "user-agent" })
        private String userAgent;
        @JsonAlias({ "platform-type" })
        private String platformType;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase SaveCountry.java
    ```
    package co.com.microservice.aws.domain.model.events;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.io.Serial;
    import java.io.Serializable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SaveCountry implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private TrxData data = new TrxData();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TrxData implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private TransactionRequest transactionRequest = new TransactionRequest();
            private TransactionResponse transactionResponse = new TransactionResponse();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TransactionRequest implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private Headers headers = new Headers();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TransactionResponse implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private String statusResponse;
            private Country country = new Country();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder(toBuilder = true)
        public static class Country implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private String shortCode;
            private String name;
            private String description;
            private boolean status;
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase CountByStatusUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface CountByStatusUseCase {
        Mono<Integer> countByStatus(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y modificar la clase CountryUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    public interface CountryUseCase extends SaveUseCase, UpdateUseCase, DeleteUseCase,
            ListAllUseCase, FindByShortCodeUseCase, CountByStatusUseCase{
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql.repository y modificar la clase CountryRepository.java, para efectos de la prueba y abordar otras formas de hacer consultas se crea usando @Query, pero si el metodo solo dice countByStatus(boolean status) es suficiente.
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql.repository;

    import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
    import org.springframework.data.r2dbc.repository.Query;
    import org.springframework.data.r2dbc.repository.R2dbcRepository;
    import org.springframework.data.repository.query.Param;
    import reactor.core.publisher.Mono;

    public interface CountryRepository extends R2dbcRepository<CountryEntity, Long> {
        Mono<CountryEntity> findByShortCode(String shortCode);

        @Query("SELECT COUNT(*) FROM worldregion.countries WHERE status = :status")
        Mono<Integer> countByStatus(@Param("status") boolean status);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase CountByStatusPort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import reactor.core.publisher.Mono;

    public interface CountByStatusPort {
        Mono<Integer> countByStatus(boolean status);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.postgresql y modificar la clase CountryAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.postgresql;

    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.*;
    import co.com.microservice.aws.infrastructure.output.postgresql.mapper.CountryEntityMapper;
    import co.com.microservice.aws.infrastructure.output.postgresql.repository.CountryRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class CountryAdapter implements SavePort<Country>, ListAllPort<Country>,
            UpdatePort<Country>, DeletePort<Country>, FindByShortCodePort<Country>, CountByStatusPort {
        private final CountryEntityMapper mapper;
        private final CountryRepository countryRepository;

        @Override
        public Mono<Country> save(Country country, Context context) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::save)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Flux<Country> listAll(Context context) {
            return countryRepository.findAll().map(mapper::toModelFromEntity);
        }

        @Override
        public Mono<Void> delete(Country country) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::delete);
        }

        @Override
        public Mono<Country> findByShortCode(Country country) {
            return Mono.just(country)
                    .map(Country::getShortCode)
                    .flatMap(countryRepository::findByShortCode)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Mono<Country> update(Country country) {
            return Mono.just(country)
                    .map(mapper::toEntityFromModel)
                    .flatMap(countryRepository::save)
                    .map(mapper::toModelFromEntity);
        }

        @Override
        public Mono<Integer> countByStatus(boolean status) {
            return countryRepository.countByStatus(status);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y modificar la clase CacheKey.java
    ```
    package co.com.microservice.aws.domain.model.commons.enums;

    import lombok.Getter;
    import lombok.RequiredArgsConstructor;

    @Getter
    @RequiredArgsConstructor
    public enum CacheKey {
        APPLY_AUDIT("APPLY_AUDIT"),
        KEY_DEFAULT("KEY_DEFAULT"),
        KEY_COUNT_BY_STATUS("KEY_COUNT_BY_STATUS");

        private final String key;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase CountryUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.*;
    import co.com.microservice.aws.domain.usecase.out.*;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.time.LocalDateTime;
    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_RECORD_NOT_FOUND;
    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;
    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;
    import static co.com.microservice.aws.domain.model.events.EventType.EVENT_EMMITED_NOTIFICATION_SAVE;

    @UseCase
    @RequiredArgsConstructor
    public class CountryUseCaseImpl implements CountryUseCase {
        private final SavePort<Country> countrySaver;
        private final ListAllPort<Country> countryLister;
        private final UpdatePort<Country> countryUpdater;
        private final DeletePort<Country> countryDeleter;
        private final FindByShortCodePort<Country> countryFinder;
        private final RedisPort redisPort;
        private final SentEventUseCase eventUseCase;
        private final CountByStatusPort countryCounter;

        @Override
        public Mono<TransactionResponse> listAll(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> redisPort.find(CacheKey.APPLY_AUDIT.getKey()).thenReturn(req))
                .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> save(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(country -> countrySaver.save(country, request.getContext()))
                .doOnNext(country -> eventUseCase.sentEvent(request.getContext(),
                        EVENT_EMMITED_NOTIFICATION_SAVE, Country.builder().name(country.getName())
                                .description(country.getDescription()).shortCode(country.getShortCode())
                                .status(country.isStatus()).build()))
                .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
        }

        @Override
        public Mono<String> delete(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().id(Long.valueOf(rq.getParams().get("id"))).build())
                    .flatMap(countryDeleter::delete)
                    .thenReturn(ResponseMessageConstant.MSG_DELETED_SUCCESS);
        }

        @Override
        public Mono<TransactionResponse> findByShortCode(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().shortCode(rq.getParams().get("shortCode")).build())
                    .flatMap(countryFinder::findByShortCode)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                    .flatMap(c -> this.buildResponse(List.of(c))
                    ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> update(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::executeUpdate)
                    .thenReturn(ResponseMessageConstant.MSG_UPDATED_SUCCESS);
        }

        @Override
        public Mono<Integer> countByStatus(TransactionRequest request) {
            return Mono.just(request)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(c -> countryCounter.countByStatus(c.isStatus()))
                .flatMap(count ->
                    redisPort.save(CacheKey.KEY_COUNT_BY_STATUS.getKey(), String.valueOf(count))
                        .thenReturn(count));
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                    .shortCode(country.getShortCode()).status(country.isStatus())
                    .dateCreation(LocalDateTime.now()).description(country.getDescription())
                    .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<Country> executeUpdate(Object object){
            if (object instanceof Country country) {
                return countryFinder.findByShortCode(Country.builder().shortCode(country.getShortCode()).build())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                        .map(ca -> Country.builder().id(ca.getId()).name(country.getName())
                                .shortCode(country.getShortCode()).status(country.isStatus())
                                .dateCreation(country.getDateCreation()).description(country.getDescription())
                                .build())
                        .flatMap(countryUpdater::update);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Country> countries){
            TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(countries.size())
                .response(new ArrayList<>(countries))
                .build();

            return Mono.just(response);
        }


    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.listenevent.events y crear la clase CountryEventListener.java
    ```
    package co.com.microservice.aws.infrastructure.input.listenevent.events;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.events.SaveCountry;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.CountryUseCase;
    import co.com.microservice.aws.infrastructure.input.listenevent.config.EventNameProperties;
    import co.com.microservice.aws.infrastructure.input.listenevent.util.EventData;
    import lombok.RequiredArgsConstructor;
    import org.reactivecommons.api.domain.DomainEvent;
    import org.reactivecommons.async.api.HandlerRegistry;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Primary;
    import reactor.core.publisher.Mono;

    @Configuration
    @RequiredArgsConstructor
    public class CountryEventListener {
        private static final String NAME_CLASS = CountryEventListener.class.getName();

        private final EventNameProperties eventNameProperties;
        private final CountryUseCase countryUseCase;
        private final LoggerBuilder logger;

        @Bean
        @Primary
        public HandlerRegistry handlerRegistry() {
            logger.info(eventNameProperties.getSaveCountry());
            return HandlerRegistry.register()
                .listenEvent(eventNameProperties.getSaveCountry(), this::saveCountry, Object.class)
                .listenEvent(eventNameProperties.getSaveCacheCountCountry(), this::saveCacheCountCountryByStatus,
                    Object.class);
        }

        private Mono<Void> saveCountry(DomainEvent<Object> event) {
            var saveCountry = EventData.getValueData(event, SaveCountry.class);
            var saveCountryData = saveCountry.getData();
            var headers = saveCountryData.getTransactionRequest().getHeaders();
            var request = TransactionRequest.builder()
                .item(buildCountry(saveCountryData.getTransactionResponse().getCountry()))
                .context(Context.builder()
                    .customer(Context.Customer.builder().username(headers.getUsername()).build()).build())
                .build();

            printEventData(event, headers.getMessageId());
            return Mono.just(request).flatMap(countryUseCase::save)
                    .onErrorResume(this::printFailed).then();
        }

        private Mono<Void> saveCacheCountCountryByStatus(DomainEvent<Object> event) {
            var saveCountry = EventData.getValueData(event, SaveCountry.class);
            var saveCountryData = saveCountry.getData();
            var headers = saveCountryData.getTransactionRequest().getHeaders();
            var request = TransactionRequest.builder()
                    .item(buildCountry(saveCountryData.getTransactionResponse().getCountry())).build();
            var status = saveCountryData.getTransactionResponse().getCountry().isStatus();

            printEventData(event, headers.getMessageId());
            return Mono.just(request).flatMap(countryUseCase::countByStatus)
                    .doOnNext(count -> logger.info(String.format("country status: %s, count: %s", status, count)))
                    .doOnError(this::printFailed).then();
        }

        private Country buildCountry(SaveCountry.Country country){
            return Country.builder()
                    .shortCode(country.getShortCode())
                    .name(country.getName())
                    .description(country.getDescription())
                    .status(country.isStatus())
                    .build();
        }

        private void printEventData(DomainEvent<?> event, String messageId) {
            logger.info(TransactionLog.Request.builder().body(event).build(),
                    "Event save country", messageId, "Save Country", NAME_CLASS);
        }

        private Mono<String> printFailed(Throwable throwable) {
            logger.error(throwable);
            return Mono.empty();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws y modificar la clase MicroserviceAwsApplication.java
    ```
    package co.com.microservice.aws;

    import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
    import org.reactivecommons.async.impl.config.annotations.EnableEventListeners;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @EnableDomainEventBus
    @EnableEventListeners
    @SpringBootApplication(exclude = {
            org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration.class
    })
    public class MicroserviceAwsApplication {
        public static void main(String[] args) {
            SpringApplication.run(MicroserviceAwsApplication.class, args);
        }
    }
    ```

    **Importante:** 
    - @EnableDomainEventBus → Necesaria para el binding en rabbitmq de forma automatica.
    - @EnableEventListeners → Necesaria para escuchar y manejar los eventos.

# <div id='id21'/>
# 21. Consumidor pruebas

- Ingresar a RabbitMQ local
    - Ir a la opción de menú exchanges
    - Elegir el exchanges domaintEvents
    - Validar que se visualicen los eventos que queremos escuchar

    ![](./img/modules/6_rabbit_consumer_listeners.png)

    ![](./img/modules/6_rabbit_consumer_listeners_listed.png)

    - Preparamos la información para los eventos de acuerdo a la estructura que hemos definido en la clase SaveCountry.java en el paquete co.com.microservice.aws.domain.model.events

    - Routing Key: business.myapp.save.country
    - Payload: 
    ```
    {
        "name": "business.myapp.save.country",
        "eventId": "2ee1b68b-fc21-4250-9be5-d4ce81d972ab",
        "data": {
            "type": "business.myapp.save.country",
            "specVersion": "1.x-wip",
            "source": "other-microservicio",
            "id": "8888888-8888-8888",
            "time": "2025-07-18T08:44:02",
            "dataContentType": "application/json",
            "invoker": "",
            "data": {
            "transactionRequest": {
                "headers": {
                "user-name": "usertest",
                "platform-type": "postman",
                "ip": "172.34.45.12",
                "message-id": "9999999-9999-0001",
                "user-agent": "application/json"
                    }
                },
            "transactionResponse": {
                "statusResponse": "SUCCESS",
                "country": {
                    "shortCode": "BOL",
                    "name": "Bolivia",
                    "description": "Cuenta con una población estimada de 8 millones de habitantes.",
                    "status": false
                    }
                }
            }
        }
    }
    ```

    - Enviar el mensaje

    ![](./img/modules/6_rabbit_consumer_publish_message.png)

    - Validar que el mensaje se envió con éxito, si dice que no fue enrutado es porque ningun microservicio esta escuchando el evento

    ![](./img/modules/6_rabbit_consumer_publish_message_ok.png)

    - Resultando validando la base de datos: debe aparecer el pais que hemos lanzado para ser guardado

    ![](./img/modules/6_rabbit_consumer_publish_message_bd.png)

    - Preparamos la información para los eventos de acuerdo a la estructura que hemos definido en la clase SaveCountry.java en el paquete co.com.microservice.aws.domain.model.events, notese que en este caso solo es necesario enviar el status atributo por el cual vamos a contar cuantos elementos tiene, para efectos de la prueba tenemos en la base de datos 

    ```
    select count(1) from worldregion.countries c where c.status = false  
    ```

    ![](./img/modules/7_rabbit_consumer_count_by_status_bd.png)

    - Enviamos el evento en rabbitMQ

    - Routing Key: business.myapp.save-cache-count.country
    - Payload: 
    ```
    {
        "name": "business.myapp.save-cache-count.country",
        "eventId": "2ee1b68b-fc21-4250-9be5-d4ce81d972ab",
        "data": {
            "type": "business.myapp.save-cache-count.country",
            "specVersion": "1.x-wip",
            "source": "other-microservicio",
            "id": "8888888-8888-8888",
            "time": "2025-07-18T08:44:02",
            "dataContentType": "application/json",
            "invoker": "",
            "data": {
            "transactionRequest": {
                "headers": {
                "user-name": "usertest",
                "platform-type": "postman",
                "ip": "172.34.45.12",
                "message-id": "9999999-9999-0001",
                "user-agent": "application/json"
                    }
                },
            "transactionResponse": {
                "statusResponse": "SUCCESS",
                "country": {
                    "status": false
                    }
                }
            }
        }
    }
    ```

    - Enviar el mensaje

    ![](./img/modules/7_rabbit_consumer_count_by_status_message_ok.png)

    - Resultando validando redis cache en la consola

# <div id='id22'/>
# 22. Consumidor logs

    ```
    -- Ingresar al CLI del contenedor
    podman exec -it redis-container redis-cli

    -- Obtener el valor de la clave en Redis
    get APPLY_AUDIT
    ```

    - Vemos el resultado de la cache

    ![](./img/modules/7_rabbit_consumer_count_by_status_cache_redis.png)

    - Logs de la ejecución

    ```
    {
        "instant": {
            "epochSecond": 1752955755,
            "nanoOfSecond": 881965600
        },
        "thread": "lettuce-nioEventLoop-5-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "country status: false, count: 3",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 86,
        "threadPriority": 5
    }
    ```

# <div id='id23'/>
# 23. Configurar consumo de APIs Externas con Webclient

- Ubicarse en el archivo application-local.yaml y agregar
```
adapters:
  rest-country:
    timeout: ${TIMEOUT:5000}
    url: ${URL_COUNTRIES:http://localhost:3000/api/v3/microservice-countries}
    info:
      exist: "${COUNTRY_EXIST:/country/exist}"
    retry:
      retries: ${REST_COUNTRY_RETRIES:3}
      retryDelay: ${REST_COUNTRY_RETRY_DELAY:2}
 ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.restconsumer.config y crear la clase RestConsumerProperties.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer.config;

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
    @ConfigurationProperties(prefix = "adapters.rest-country")
    public class RestConsumerProperties {
        private String url;
        private int timeout;
    }
    ```

- Ubicarse en el paquete ApiInfoProperties y crear la clase ApiInfoProperties.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer.config;

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
    @ConfigurationProperties(prefix = "adapters.rest-country.info")
    public class ApiInfoProperties {
        private String exist;
    }
    ```

- Ubicarse en el paquete ApiInfoProperties y crear la clase ApiInfoProperties.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer.config;

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
    @ConfigurationProperties(prefix = "adapters.rest-country.retry")
    public class RetryProperties {
        private int retries;
        private int retryDelay;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws y crear la clase RestConsumerUtils.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer.config;

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

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.restconsumer.config y crear la clase RestConsumerConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer.config;

    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.web.reactive.function.client.WebClient;

    import static co.com.microservice.aws.infrastructure.output.restconsumer.config.RestConsumerUtils.getClientHttpConnector;

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

- Ubicarse en el paquete co.com.microservice.aws.domain.model y crear la clase InfoCountry.java
    ```
    package co.com.microservice.aws.domain.model;

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
    public class InfoCountry implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String code;
        private String name;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase WorldCountryPort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import co.com.microservice.aws.domain.model.rq.Context;
    import reactor.core.publisher.Mono;

    public interface WorldCountryPort {
        Mono<Boolean> exist(Context context, String name);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.restconsumer y crear la clase WorldCountryAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.restconsumer;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.InfoCountry;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.WorldCountryPort;
    import co.com.microservice.aws.infrastructure.output.restconsumer.config.ApiInfoProperties;
    import co.com.microservice.aws.infrastructure.output.restconsumer.config.RetryProperties;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.http.HttpStatusCode;
    import org.springframework.http.MediaType;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.ClientResponse;
    import org.springframework.web.reactive.function.client.WebClient;
    import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
    import reactor.core.publisher.Mono;
    import reactor.util.retry.Retry;

    import java.time.Duration;
    import java.util.Objects;

    import static co.com.microservice.aws.domain.model.commons.util.LogMessage.MESSAGE_SERVICE;

    @Service
    public class WorldCountryAdapter implements WorldCountryPort {
        private static final String NAME_CLASS = WorldCountryAdapter.class.getName();
        private final WebClient webClientConfig;
        private final ApiInfoProperties apiInfoProperties;
        private final RetryProperties retryProperties;
        private final LoggerBuilder logger;

        public WorldCountryAdapter(@Qualifier(value = "webClientConfig") WebClient webClientConfig,
                                ApiInfoProperties apiInfoProperties, LoggerBuilder loggerBuilder,
                                RetryProperties retryProperties){
            this.webClientConfig = webClientConfig.mutate().build();
            this.apiInfoProperties = apiInfoProperties;
            this.logger = loggerBuilder;
            this.retryProperties = retryProperties;
        }

        @Override
        public Mono<Boolean> exist(Context context, String name) {
            logger.info("rest get info country", context.getId(), NAME_CLASS, "exist");
            return this.getCountry(context, apiInfoProperties.getExist(), name);
        }

        private Mono<Boolean> getCountry(Context context, String urlPath, String name) {
            return this.buildGetRequestWithHeaders(context, urlPath.concat(name))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> this.errorStatusFunction(res, context))
                    .bodyToMono(InfoCountry.class)
                    .retryWhen(Retry
                            .fixedDelay(retryProperties.getRetries(), Duration.ofSeconds(retryProperties.getRetryDelay()))
                            .doBeforeRetry(signal -> this.printErrorRetry(signal, context))
                            .doAfterRetry(retrySignal -> logger.info("Retry: " + (retrySignal.totalRetries() + 1), context.getId(), NAME_CLASS, "isAuditOnList")))
                    .doOnNext(res -> this.printOnProcess(context, res))
                    .doOnError(logger::error)
                    .onErrorMap(original -> new TechnicalException(TECHNICAL_REST_CLIENT_ERROR))
                    .flatMap(this::createResponse);
        }

        private Mono<Boolean> createResponse(InfoCountry result) {
            return !Objects.isNull(result) && !result.getCode().isEmpty() ?
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
            logger.info(messageInfo, context.getId(), NAME_CLASS, "printErrorRetry");
            logger.error(retrySignal.failure());
        }

        private Mono<Throwable> errorStatusFunction(ClientResponse response, Context context) {
            var messageInfo = String.format("rest get info country %s", response.statusCode());
            logger.info(messageInfo, context.getId(), NAME_CLASS, "errorStatusFunction");
            return response.bodyToMono(String.class).switchIfEmpty(Mono.just(response.statusCode().toString()))
                    .map(msg -> new TechnicalException(new RuntimeException(msg),
                            TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR));
        }

        private void printOnProcess(Context context, InfoCountry infoCountry){
            logger.info(TransactionLog.Request.builder().body(context).build(),
                    context.getId(), context.getId(), MESSAGE_SERVICE, NAME_CLASS);
            logger.info(TransactionLog.Response.builder().body(infoCountry).build(),
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
    | `.bodyToMono(InfoCountry.class)` | Convierte el cuerpo de la respuesta a un `Mono<InfoCountry>` |
    | `.retryWhen(...)` | Reintenta la llamada si falla (por excepción), según una política definida |
    | `Retry.fixedDelay(...)` | Número de reintentos y tiempo de espera fijo entre intentos fallidos (por ejemplo, 3 reintentos cada 2 segundos) |
    | `.doBeforeRetry(...)` | Acción a ejecutar antes de cada reintento (ej: loguear el error del intento anterior) |
    | `.doAfterRetry(...)` | Acción a ejecutar después de que se ha planificado el reintento (ej: loguear intento actual) |
    | `.doOnNext(...)` | Acción adicional cuando llega una respuesta exitosa (ej: imprimir logs del parámetro recibido) |
    | `.doOnError(...)` | Loguea cualquier error que no haya sido capturado anteriormente |
    | `.flatMap(this::createResponse)` | Transforma el resultado a un `Mono<Boolean>` final según la lógica de negocio |
    | `.option(CONNECT_TIMEOUT_MILLIS, timeout.intValue())` | Define cuánto tiempo máximo se esperará por la respuesta del servidor antes de lanzar TimeoutException |

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in y crear la clase WorldCountryUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in;

    public interface WorldCountryUseCase extends FindByNameUseCase{
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase InfoCountryUseCase.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.WorldCountryUseCase;
    import co.com.microservice.aws.domain.usecase.out.WorldCountryPort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.time.LocalDateTime;
    import java.util.Collections;
    import java.util.List;

    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

    @UseCase
    @RequiredArgsConstructor
    public class WorldCountryUseCaseImpl implements WorldCountryUseCase {
        private final WorldCountryPort worldCountryPort;

        @Override
        public Mono<TransactionResponse> findByName(TransactionRequest request) {
            return Mono.just(request)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::buildCountry)
                    .flatMap(c -> worldCountryPort.exist(request.getContext(), c.getName()))
                    .flatMap(res -> this.buildResponse(List.of(res)));
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                        .shortCode(country.getShortCode()).status(country.isStatus())
                        .dateCreation(LocalDateTime.now()).description(country.getDescription())
                        .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Boolean> existCountries){
            TransactionResponse response = TransactionResponse.builder()
                    .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                    .size(existCountries.size())
                    .response(new ArrayList<>(existCountries))
                    .build();

            return Mono.just(response);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase CountryUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.domain.model.Country;
    import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
    import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.model.rs.TransactionResponse;
    import co.com.microservice.aws.domain.usecase.in.CountryUseCase;
    import co.com.microservice.aws.domain.usecase.in.SentEventUseCase;
    import co.com.microservice.aws.domain.usecase.in.WorldCountryUseCase;
    import co.com.microservice.aws.domain.usecase.out.*;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.*;
    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;
    import static co.com.microservice.aws.domain.model.events.EventType.EVENT_EMMITED_NOTIFICATION_SAVE;

    @UseCase
    @RequiredArgsConstructor
    public class CountryUseCaseImpl implements CountryUseCase {
        private final SavePort<Country> countrySaver;
        private final ListAllPort<Country> countryLister;
        private final UpdatePort<Country> countryUpdater;
        private final DeletePort<Country> countryDeleter;
        private final FindByShortCodePort<Country> countryFinder;
        private final RedisPort redisPort;
        private final CountByStatusPort countryCounter;

        private final SentEventUseCase eventUseCase;
        private final WorldCountryUseCase useCaseFinder;

        @Override
        public Mono<TransactionResponse> listAll(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> redisPort.find(CacheKey.APPLY_AUDIT.getKey()).thenReturn(req))
                .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> save(TransactionRequest request) {
            return Mono.just(request)
                .filter(this::userIsRequired)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))))
                .filterWhen(req -> this.countryExist(useCaseFinder.findByName(req)))
                .switchIfEmpty(Mono.error(new BusinessException(BUSINESS_COUNTRY_NOT_EXIST)))
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(country -> countrySaver.save(country, request.getContext()))
                .doOnNext(country -> eventUseCase.sentEvent(request.getContext(),
                        EVENT_EMMITED_NOTIFICATION_SAVE, Country.builder().name(country.getName())
                                .description(country.getDescription()).shortCode(country.getShortCode())
                                .status(country.isStatus()).build()))
                .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
        }

        @Override
        public Mono<String> delete(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().id(Long.valueOf(rq.getParams().get("id"))).build())
                    .flatMap(countryDeleter::delete)
                    .thenReturn(ResponseMessageConstant.MSG_DELETED_SUCCESS);
        }

        @Override
        public Mono<TransactionResponse> findByShortCode(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(rq -> Country.builder().shortCode(rq.getParams().get("shortCode")).build())
                    .flatMap(countryFinder::findByShortCode)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                    .flatMap(c -> this.buildResponse(List.of(c))
                    ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
        }

        @Override
        public Mono<String> update(TransactionRequest request) {
            return Mono.just(request)
                    .filter(this::userIsRequired)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::executeUpdate)
                    .thenReturn(ResponseMessageConstant.MSG_UPDATED_SUCCESS);
        }

        @Override
        public Mono<Integer> countByStatus(TransactionRequest request) {
            return Mono.just(request)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(c -> countryCounter.countByStatus(c.isStatus()))
                .flatMap(count ->
                    redisPort.save(CacheKey.KEY_COUNT_BY_STATUS.getKey(), String.valueOf(count))
                        .thenReturn(count));
        }

        private Boolean userIsRequired(TransactionRequest request){
            return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
        }

        private Mono<Country> buildCountry(Object object){
            if (object instanceof Country country) {
                return Mono.just(Country.builder().name(country.getName())
                    .shortCode(country.getShortCode()).status(country.isStatus())
                    .dateCreation(LocalDateTime.now()).description(country.getDescription())
                    .build());
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<Country> executeUpdate(Object object){
            if (object instanceof Country country) {
                return countryFinder.findByShortCode(Country.builder().shortCode(country.getShortCode()).build())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                        .map(ca -> Country.builder().id(ca.getId()).name(country.getName())
                                .shortCode(country.getShortCode()).status(country.isStatus())
                                .dateCreation(country.getDateCreation()).description(country.getDescription())
                                .build())
                        .flatMap(countryUpdater::update);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<TransactionResponse> buildResponse(List<Country> countries){
            TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(countries.size())
                .response(new ArrayList<>(countries))
                .build();

            return Mono.just(response);
        }

        private Mono<Boolean> countryExist(Mono<TransactionResponse> res){
            return res.map(rs -> {
                List<Object> response = rs.getResponse();
                return !response.isEmpty() && Boolean.TRUE.equals(response.getFirst());
            });
        }
    }
    ```

# <div id='id24'/>
# 24. Webclient pruebas

- Ejecutar la aplicación

- Logs: se imprimé que intentó guardar el registro y que se reintentó la petición hasta 3 veces cada 2 segundos, pero se lanza excepción indicando que ocurrió un error
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/save' \
    --header 'user-name: usertest' \
    --header 'message-id: 9999999-9999-0001' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "ECU",
        "name": "Ecuador",
        "description": "Cuenta con una población estimada de 10 millones de habitantes.",
        "status": true
    }'

    -- respuesta en postman

    {
        "errors": [
            {
                "reason": "WRT02 - An error has occurred in the Rest Client",
                "domain": "POST:/api/v1/microservice-aws/country/save",
                "code": "WRT02",
                "message": "An error has occurred in the Rest Client"
            }
        ]
    }

# <div id='id25'/>
# 25. Webclient logs

    -- logs relevantes
    
    -- Pasó por el metodo guardar
    {
        "instant": {
            "epochSecond": 1753138089,
            "nanoOfSecond": 145151800
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Save one record\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"9999999-9999-0001\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 93,
        "threadPriority": 5
    }
    
    -- Pasó por el metodo obtener información del pais
    {
        "instant": {
            "epochSecond": 1753138089,
            "nanoOfSecond": 204889300
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"rest get info country\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"exist\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 93,
        "threadPriority": 5
    }

    -- Reintento 1
    {
        "instant": {
            "epochSecond": 1753138089,
            "nanoOfSecond": 271307500
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '1'\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 93,
        "threadPriority": 5
    }

    -- Pasan 2 segundos, Reintento 2
    {
        "instant": {
            "epochSecond": 1753138091,
            "nanoOfSecond": 342309000
        },
        "thread": "reactor-http-nio-4",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '2'\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }

    -- Pasan 2 segundos, Reintento 3
    {
        "instant": {
            "epochSecond": 1753138093,
            "nanoOfSecond": 361146100
        },
        "thread": "reactor-http-nio-5",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '3'\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 95,
        "threadPriority": 5
    }

    -- Finalmente lanza el error técnico
    ```

- Debido a que de momento no tenemos un microservicio que entregue la información del pais entonces vamos a simular la respuesta con la estructura que esperamos, utilizando **wiremock**

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
    curl -X POST http://localhost:3000/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
        "request": {
        "method": "GET",
        "urlPathPattern": "/api/v3/microservice-countries/country/exist/.*"
        },
        "response": {
        "status": 500,
        "body": "Internal Server Error"
        },
        "scenarioName": "AuditParamRetry",
        "requiredScenarioState": "Started",
        "newScenarioState": "SecondAttempt"
    }'
    ```

    ```
    curl -X POST http://localhost:3000/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
        "request": {
        "method": "GET",
        "urlPathPattern": "/api/v3/microservice-countries/country/exist/.*"
        },
        "response": {
        "fixedDelayMilliseconds": 10000
        },
        "scenarioName": "AuditParamRetry",
        "requiredScenarioState": "SecondAttempt",
        "newScenarioState": "ThirdAttempt"
    }'
    ```
    
    ```
    curl -X POST http://localhost:3000/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
        "request": {
        "method": "GET",
        "urlPathPattern": "/api/v3/microservice-countries/country/exist/.*"
        },
        "response": {
        "status": 200,
        "body": "{ \"code\": \"LLL\", \"name\": \"nameCountry\"}",
        "headers": {
            "Content-Type": "application/json"
        }
        },
        "scenarioName": "AuditParamRetry",
        "requiredScenarioState": "ThirdAttempt",
        "newScenarioState": "Completed"
    }'
    ```

    -- curl guardar nuevo pais
    ```
    curl --location 'localhost:8080/api/v1/microservice-aws/country/save' \
    --header 'user-name: usertest' \
    --header 'message-id: 9999999-9999-0001' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Content-Type: application/json' \
    --data '{
        "shortCode": "USA",
        "name": "Estados Unidos",
        "description": "Cuenta con una población estimada de 300 millones de habitantes.",
        "status": true
    }'
    ```
    
    -- logs relevantes
    -- Pasó por el metodo guardar
    ```
    {
        "instant": {
            "epochSecond": 1753146977,
            "nanoOfSecond": 692667900
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Save one record\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"id\":\"9999999-9999-0001\",\"customer\":{\"ip\":\"172.34.45.12\",\"username\":\"usertest\",\"device\":{\"userAgent\":\"application/json\",\"platformType\":\"postman\"}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }
    
    -- Pasó por el metodo obtener información del pais
    {
        "instant": {
            "epochSecond": 1753146977,
            "nanoOfSecond": 741437700
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"rest get info country\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"exist\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }

    -- Reintento 1, Error 500
    {
        "instant": {
            "epochSecond": 1753147319,
            "nanoOfSecond": 174606000
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"rest get info country 500 INTERNAL_SERVER_ERROR\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"errorStatusFunction\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }

    -- Pasan 2 segundos, Reintento 2, Error por timeout
    {
        "instant": {
            "epochSecond": 1753147319,
            "nanoOfSecond": 201915700
        },
        "thread": "reactor-http-nio-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"WRT02 - An error has occurred in the Rest Client, wating retry: '1'\",\"messageId\":\"9999999-9999-0001\",\"service\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"method\":\"printErrorRetry\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }

    -- Pasan 2 segundos, Reintento 3, Status 200
    {
        "instant": {
            "epochSecond": 1753147328,
            "nanoOfSecond": 455661400
        },
        "thread": "reactor-http-nio-4",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"9999999-9999-0001\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Service Api Rest world regions\",\"method\":\"co.com.microservice.aws.infrastructure.output.restconsumer.WorldCountryAdapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"code\":\"LLL\",\"name\":\"nameCountry\"}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 109,
        "threadPriority": 5
    }

    -- Se obtiene la información correctamente:
    "response":{
        "headers":"null",
        "body":{
            "code":"LLL",
            "name":"nameCountry"
        }
    }
    ```

    **Importante**: Cuando se ejecuten los escenarios se debe reiniciar su estado o sino dará error 404

# <div id='id26'/>
# 26. Configurar S3 (getFile, Uploadfile)

- Ubicarse en el archivo application-local.yaml y colocar la siguiente información: agregar en adapters la información de s3
```
adapters:
  s3:
    region: "${AWS_REGION:us-east-1}"
    endpoint: http://localhost:4566
    accessKey: "test"
    secretKey: "test"
```

- Ubicarse en el archivo build.gradle y colocar la siguiente dependencia:
    ```
    ext {
        awsSdkVersion = '2.25.17'
        reactiveCommonsVersion = '4.1.4'
        tikaVersion = '3.1.0'
        commonsCompressVersion = '1.27.1'
    }

    dependencies {
        implementation 'software.amazon.awssdk:s3'
        implementation "org.apache.tika:tika-core:${tikaVersion}"
        implementation "org.apache.commons:commons-compress:${commonsCompressVersion}"
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.s3repository.config y crear la clase S3ConnectionProperties.java
    ```
    package co.com.microservice.aws.infrastructure.output.s3repository.config;

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
    @ConfigurationProperties(prefix = "adapters.s3")
    public class S3ConnectionProperties {
        private String region;
        private String endpoint;
        private String accessKey;
        private String secretKey;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.s3repository.config y crear la clase S3Config.java
    ```
    package co.com.microservice.aws.infrastructure.output.s3repository.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
    import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
    import software.amazon.awssdk.regions.Region;
    import software.amazon.awssdk.services.s3.S3AsyncClient;
    import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;

    import java.net.URI;

    @Configuration
    public class S3Config {
        @Profile({ "!local" })
        @Bean(name = "s3Connection")
        S3AsyncClient s3AsyncClient(S3ConnectionProperties s3Properties) {
            return getBuilder(s3Properties).build();
        }

        @Profile("local")
        @Bean(name = "s3Connection")
        S3AsyncClient localS3AsyncClient(S3ConnectionProperties props) {
            return S3AsyncClient.builder()
                    .region(Region.of(props.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                    .endpointOverride(URI.create(props.getEndpoint()))
                    .forcePathStyle(true)
                    .build();
        }

        private S3AsyncClientBuilder getBuilder(S3ConnectionProperties s3Properties) {
            return S3AsyncClient.builder().region(Region.of(s3Properties.getRegion()));
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.commons.enums y crear la clase TechnicalExceptionMessage.java
    ```
    package co.com.microservice.aws.domain.model.commons.enums;

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
        TECHNICAL_REQUEST_ERROR("WRT04", "There is an error in the request body"),
        TECHNICAL_EXCEPTION_REPOSITORY("WRT05", "An error has occurred in the repository"),
        TECHNICAL_GETTING_S3_OBJECT_FAILED("WRT06", "Error obteniendo objeto de S3"),
        ZIP_FILE_IS_WRONG("WRT07", "Error, the file ZIP is incorrect"),
        ZIP_FILE_HASNT_ONLY_ONE_FILE("WRT08", "Error, the file ZIP has more one files"),
        FILE_ISNT_TXT("WRT09", "Error, the file is not flat text"),
        TXT_FILE_HAS_INVALID_CHARS("PAT0017", "Error, the file TXT has invalid characters");

        private final String code;
        private final String message;

        public String getDescription() {
            return String.join(" - ", this.getCode(), this.getMessage());
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.s3repository.operations y crear la clase S3Operations.java
    ```
    package co.com.microservice.aws.infrastructure.output.s3repository.operations;

    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Mono;
    import software.amazon.awssdk.core.BytesWrapper;
    import software.amazon.awssdk.core.async.AsyncRequestBody;
    import software.amazon.awssdk.core.async.AsyncResponseTransformer;
    import software.amazon.awssdk.services.s3.S3AsyncClient;
    import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
    import software.amazon.awssdk.services.s3.model.GetObjectRequest;
    import software.amazon.awssdk.services.s3.model.PutObjectRequest;

    @Component
    @RequiredArgsConstructor
    public class S3Operations {
        private final S3AsyncClient s3AsyncClient;

        public Mono<Boolean> uploadObject(String bucketName, String objectKey, byte[] fileContent) {
            return Mono
                    .fromFuture(s3AsyncClient.putObject(configurePutObject(bucketName, objectKey),
                            AsyncRequestBody.fromBytes(fileContent)))
                    .map(response -> response.sdkHttpResponse().isSuccessful());
        }

        public Mono<byte[]> getObject(String bucketName, String objectKey) {
            return Mono
                    .fromFuture(
                            s3AsyncClient.getObject(GetObjectRequest.builder().key(objectKey).bucket(bucketName).build(),
                                    AsyncResponseTransformer.toBytes()))
                    .map(BytesWrapper::asByteArray).onErrorMap(Exception.class,
                            exception -> new TechnicalException(exception, TechnicalExceptionMessage.TECHNICAL_GETTING_S3_OBJECT_FAILED));
        }

        public Mono<Boolean> deleteObject(String bucketName, String objectKey) {
            return Mono
                    .fromFuture(s3AsyncClient
                            .deleteObject(DeleteObjectRequest.builder().key(objectKey).bucket(bucketName).build()))
                    .map(response -> response.sdkHttpResponse().isSuccessful());
        }

        private PutObjectRequest configurePutObject(String bucketName, String objectKey) {
            return PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();
        }

    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase FileStoragePort.java
    ```
    package co.com.microservice.aws.domain.usecase.out;

    import co.com.microservice.aws.domain.model.rq.Context;
    import reactor.core.publisher.Mono;
    public interface FileStoragePort {
        Mono<Boolean> uploadObject(Context context, String bucketName, String objectKey, byte[] file);
        Mono<Boolean> deleteObject(Context context, String bucketName, String objectKey);
        Mono<byte[]> getFile(Context context, String bucketName, String objectPath);
    }
    ```

- Ubicarse en el paquete cco.com.microservice.aws.infrastructure.output.s3repository y crear la clase S3Adapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.s3repository;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
    import co.com.microservice.aws.infrastructure.output.s3repository.operations.S3Operations;
    import lombok.AllArgsConstructor;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Mono;

    @Component
    @AllArgsConstructor
    public class S3Adapter implements FileStoragePort {
        private static final String NAME_CLASS = S3Adapter.class.getName();
        private final S3Operations s3Operations;
        private final LoggerBuilder logger;

        @Override
        public Mono<Boolean> uploadObject(Context context, String bucketName, String objectKey, byte[] file) {
            return s3Operations.uploadObject(bucketName, objectKey, file).doOnSuccess(
                    success -> logger.info("uploadObject success", context.getId(), "uploadObject", NAME_CLASS))
                    .doOnError(logger::error);
        }

        @Override
        public Mono<Boolean> deleteObject(Context context, String bucketName, String objectKey) {
            return s3Operations.deleteObject(bucketName, objectKey)
                    .doOnSuccess(success -> logger.info("deleteObject success", context.getId(), "deleteObject", NAME_CLASS))
                    .doOnError(logger::error);
        }

        @Override
        public Mono<byte[]> getFile(Context context, String bucketName, String objectPath) {
            return s3Operations.getObject(bucketName, objectPath)
                    .doOnSuccess(success -> logger.info("getFile success", context.getId(), "getFile", NAME_CLASS))
                    .doOnError(logger::error);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model.events y crear la clase ProccessWorldRegionFile.java
    ```
    package co.com.microservice.aws.domain.model.events;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.io.Serial;
    import java.io.Serializable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProccessWorldRegionFile implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private TrxData data = new TrxData();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TrxData implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private TransactionRequest transactionRequest = new TransactionRequest();
            private TransactionResponse transactionResponse = new TransactionResponse();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TransactionRequest implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private Headers headers = new Headers();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TransactionResponse implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;
            private String statusResponse;
            private InfoBucket response = new InfoBucket();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder(toBuilder = true)
        public static class InfoBucket implements Serializable {
            @Serial
            private static final long serialVersionUID = 1L;

            private String fileName;
            private String bucketName;
            private String path;
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.in.commons y crear la clase ProcessFileUseCase.java
    ```
    package co.com.microservice.aws.domain.usecase.in.commons;

    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import reactor.core.publisher.Mono;

    public interface ProcessFileUseCase {
        Mono<String> processFile(TransactionRequest request);
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.mime y crear la clase MimeDetect.java
    ```
    package co.com.microservice.aws.application.helpers.file.mime;

    import org.apache.tika.Tika;

    import lombok.experimental.UtilityClass;

    @UtilityClass
    public class MimeDetect {
        public static String getMimeType(byte[] fileBytes) {
            var tika = new Tika();
            return tika.detect(fileBytes);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.mime y crear la clase MimeTypes.java
    ```
    package co.com.microservice.aws.application.helpers.file.mime;

    import lombok.experimental.UtilityClass;

    @UtilityClass
    public final class MimeTypes {
        public static final String TEXT_PLAIN = "text/plain";
        public static final String ZIP = "application/zip";
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.model y crear la clase FileBytes.java
    ```
    package co.com.microservice.aws.application.helpers.file.model;

    import java.io.Serial;
    import java.io.Serializable;

    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class FileBytes implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private byte[] bytes;
        private boolean zip;
        private TechnicalExceptionMessage technicalExceptionMessage;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.model y crear la clase FileData.java
    ```
    package co.com.microservice.aws.application.helpers.file.model;

    import java.io.Serial;
    import java.io.Serializable;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class FileData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String data;
        private boolean zip;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.model y crear la clase ZipValidatorResult.java
    ```
    package co.com.microservice.aws.application.helpers.file.model;

    import java.io.Serializable;

    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class ZipValidatorResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private boolean hasOnlyOneFile;
        private TechnicalExceptionMessage technicalExceptionMessage;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.zipfile y crear la clase ZipValidator.java
    ```
    package co.com.microservice.aws.application.helpers.file.zipfile;

    import co.com.microservice.aws.application.helpers.file.mime.MimeDetect;
    import co.com.microservice.aws.application.helpers.file.mime.MimeTypes;
    import co.com.microservice.aws.application.helpers.file.model.FileBytes;
    import co.com.microservice.aws.application.helpers.file.model.ZipValidatorResult;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import lombok.experimental.UtilityClass;
    import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.io.IOException;

    @UtilityClass
    public class ZipValidator {
        private static final int BUFFER_SIZE = 4;

        public static FileBytes extractFileFromZip(byte[] bytes) {
            var returnValidation = isValidZipOnlyOneFile(bytes);

            var fileBytes = FileBytes.builder().bytes(bytes)
                    .technicalExceptionMessage(returnValidation.getTechnicalExceptionMessage())
                    .build();

            if (null != returnValidation.getTechnicalExceptionMessage()) {
                return fileBytes;
            }

            if (returnValidation.isHasOnlyOneFile()) {
                getOnlyOneFile(fileBytes);
            } else {
                fileBytes.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_HASNT_ONLY_ONE_FILE);
            }
            return fileBytes;
        }

        public static ZipValidatorResult isValidZipOnlyOneFile(byte[] bytes) {
            var result = ZipValidatorResult.builder().build();

            if (!isValidZip(bytes)) {
                return result;
            }

            try (var zipInput = new ZipArchiveInputStream(new ByteArrayInputStream(bytes))) {
                int entryCount = 0;
                while (zipInput.getNextEntry() != null) {
                    entryCount++;
                }
                result.setHasOnlyOneFile(entryCount == 1);
            } catch (IOException e) {
                result.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_IS_WRONG);
            }
            return result;
        }

        public static boolean isValidZip(byte[] bytes) {
            var returnValidation = false;
            var mime = MimeDetect.getMimeType(bytes);
            if (MimeTypes.ZIP.equals(mime)) {
                returnValidation = true;
            }
            return returnValidation;
        }

        public static void getOnlyOneFile(FileBytes fileBytes) {
            fileBytes.setZip(true);
            try (var inputStream = new ByteArrayInputStream(fileBytes.getBytes());
                var zipInput = new ZipArchiveInputStream(inputStream);
                var outputStream = new ByteArrayOutputStream()) {

                if (zipInput.getNextEntry() != null) {
                    var buffer = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = zipInput.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    fileBytes.setBytes(outputStream.toByteArray());
                }

            } catch (IOException e) {
                fileBytes.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_IS_WRONG);
            }
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file.txt y crear la clase TxtValidator.java
    ```
    package co.com.microservice.aws.application.helpers.file.txt;

    import co.com.microservice.aws.application.helpers.file.mime.MimeDetect;
    import co.com.microservice.aws.application.helpers.file.mime.MimeTypes;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import lombok.experimental.UtilityClass;

    import java.nio.charset.StandardCharsets;
    import java.util.regex.Pattern;

    @UtilityClass
    public class TxtValidator {
        public static boolean txtHasValidChars(byte[] bytes) {
            var returnValidation = false;
            if (isValidTxt(bytes)) {
                returnValidation = hasValidChars(bytes);
            } else {
                throw new TechnicalException(TechnicalExceptionMessage.FILE_ISNT_TXT);
            }
            return returnValidation;
        }

        public static boolean isValidTxt(byte[] bytes) {
            var returnValidation = false;
            var mime = MimeDetect.getMimeType(bytes);
            if (MimeTypes.TEXT_PLAIN.equals(mime)) {
                returnValidation = true;
            }
            return returnValidation;
        }

        private static boolean hasValidChars(byte[] bytes) {
            var regex = "[´¦¢£¥©§¶`µ\\\\×°¡¿]";
            var pattern = Pattern.compile(regex);
            var content = new String(bytes, StandardCharsets.UTF_8);
            var matcher = pattern.matcher(content);
            return !matcher.find();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.file y crear la clase FlatFile.java
    ```
    package co.com.microservice.aws.application.helpers.file;

    import co.com.microservice.aws.application.helpers.file.model.FileBytes;
    import co.com.microservice.aws.application.helpers.file.model.FileData;
    import co.com.microservice.aws.application.helpers.file.txt.TxtValidator;
    import co.com.microservice.aws.application.helpers.file.zipfile.ZipValidator;
    import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import lombok.experimental.UtilityClass;
    import reactor.core.publisher.Mono;

    import java.nio.charset.StandardCharsets;

    @UtilityClass
    public class FlatFile {
        public static Mono<FileData> getValue(byte[] bytes) {
            if (ZipValidator.isValidZip(bytes)) {
                var fileBytes = ZipValidator.extractFileFromZip(bytes);
                if (null == fileBytes.getTechnicalExceptionMessage()) {
                    return getValueTxtFile(fileBytes);
                } else {
                    return Mono.error(new TechnicalException(fileBytes.getTechnicalExceptionMessage()));
                }
            } else {
                var fileBytes = FileBytes.builder().bytes(bytes).build();
                return getValueTxtFile(fileBytes);
            }
        }

        public static Mono<FileData> getValueTxtFile(FileBytes fileBytes) {
            try {
                if (TxtValidator.txtHasValidChars(fileBytes.getBytes())) {
                    var fileData = FileData.builder().zip(fileBytes.isZip()).build();
                    fileData.setData(new String(fileBytes.getBytes(), StandardCharsets.UTF_8));
                    return Mono.just(fileData);
                } else {
                    return Mono.error(new TechnicalException(TechnicalExceptionMessage.TXT_FILE_HAS_INVALID_CHARS));
                }
            } catch (TechnicalException technicalException) {
                return Mono.error(technicalException);
            }
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.helpers.utils y crear la clase FileStructureValidator.java
    ```
    package co.com.microservice.aws.application.helpers.utils;

    import lombok.experimental.UtilityClass;

    import java.util.regex.Pattern;

    @UtilityClass
    public class FileStructureValidator {
        private static final Pattern patternLines = Pattern.compile("\\r?\\n");

        public static String[] getFileLines(String data) {
            return patternLines.split(data);
        }

        public static String getDataLine(String[] fileLines, int num) {
            return fileLines[num - 1];
        }

        private static boolean validatByRegEx(String regex, String data) {
            return Pattern.matches(regex, data);
        }

        public static String changeZipToTxt(String zipPath) {
            var toReplace = ".zip";
            var replacement = ".txt";
            int lastIndex = zipPath.lastIndexOf(toReplace);
            if (lastIndex == -1) {
                return zipPath + replacement;
            }
            return zipPath.substring(0, lastIndex) +
                    replacement +
                    zipPath.substring(lastIndex + toReplace.length());
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y crear la clase WorldRegionUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.application.helpers.file.FlatFile;
    import co.com.microservice.aws.application.helpers.file.model.FileData;
    import co.com.microservice.aws.application.helpers.utils.FileStructureValidator;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
    import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Mono;

    import java.util.Arrays;

    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

    @UseCase
    @RequiredArgsConstructor
    public class WorldRegionUseCaseImpl implements WorldRegionUseCase {
        private final FileStoragePort fileStoragePort;

        @Override
        public Mono<String> processFile(TransactionRequest request) {
            return Mono.just(request)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::buildWorldRegion)
                    .flatMap(infobucket -> this.getFile(infobucket, request.getContext()));
        }

        private Mono<String> getFile(ProccessWorldRegionFile.InfoBucket infoBucket, Context context){
            return Mono.just(infoBucket)
                    .flatMap(ib -> fileStoragePort.getFile(context, ib.getBucketName(), ib.getPath()))
                    .flatMap(FlatFile::getValue).onErrorResume(Mono::error)
                    .flatMap(flatfile -> this.validateFile(flatfile, infoBucket, context))
                    .doOnEach(signal -> {
                        if (signal.isOnNext()) {
                            System.out.println(Arrays.toString(signal.get()));
                        }
                        if (signal.isOnError()) {
                            signal.getThrowable().printStackTrace();
                        }
                    })
                    .thenReturn("Guardó");
        }

        private Mono<String[]> validateFile(FileData fileData, ProccessWorldRegionFile.InfoBucket infoBucket, Context context) {
            var fileLines = FileStructureValidator.getFileLines(fileData.getData());
            var monoReturn = Mono.just(Boolean.TRUE);
            if (fileData.isZip()) {
                var path = FileStructureValidator.changeZipToTxt(infoBucket.getPath());
                var bucketName = infoBucket.getBucketName();

                monoReturn = fileStoragePort.uploadObject(context, bucketName, path,
                        fileData.getData().getBytes());
            }

            return monoReturn.flatMap(b -> Mono.just(fileLines));
        }

        private Mono<ProccessWorldRegionFile.InfoBucket> buildWorldRegion(Object object){
            if (object instanceof ProccessWorldRegionFile.InfoBucket infoFile) {
                return Mono.just(infoFile);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.listenevent.events y crear la clase WorldRegionEventLister.java
    ```
    package co.com.microservice.aws.infrastructure.input.listenevent.events;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
    import co.com.microservice.aws.infrastructure.input.listenevent.config.EventNameProperties;
    import co.com.microservice.aws.infrastructure.input.listenevent.util.EventData;
    import lombok.RequiredArgsConstructor;
    import org.reactivecommons.api.domain.DomainEvent;
    import org.reactivecommons.async.api.HandlerRegistry;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import reactor.core.publisher.Mono;

    @Configuration
    @RequiredArgsConstructor
    public class WorldRegionEventLister {
        private static final String NAME_CLASS = WorldRegionEventLister.class.getName();

        private final EventNameProperties eventNameProperties;
        private final WorldRegionUseCase worldRegionUseCase;
        private final LoggerBuilder logger;

        @Bean
        public HandlerRegistry handlerRegistryWorldRegion() {
            logger.info(eventNameProperties.getSaveWorldRegion());
            return HandlerRegistry.register()
                    .listenEvent(eventNameProperties.getSaveWorldRegion(), this::saveWorldRegion, Object.class);
        }

        private Mono<Void> saveWorldRegion(DomainEvent<Object> event) {
            var saveWorldRegion = EventData.getValueData(event, ProccessWorldRegionFile.class);
            var saveWorldRegionData = saveWorldRegion.getData();
            var headers = saveWorldRegionData.getTransactionRequest().getHeaders();
            var request = TransactionRequest.builder()
                    .item(saveWorldRegionData.getTransactionResponse().getResponse())
                    .context(Context.builder().id(headers.getMessageId()).build())
                    .build();

            printEventData(event, headers.getMessageId());
            return Mono.just(request).flatMap(worldRegionUseCase::processFile)
                    .onErrorResume(this::printFailed).then();
        }

        private void printEventData(DomainEvent<?> event, String messageId) {
            logger.info(TransactionLog.Request.builder().body(event).build(),
                    "Event save WorldRegion", messageId, "Save WorldRegion", NAME_CLASS);
        }

        private Mono<String> printFailed(Throwable throwable) {
            logger.error(throwable);
            return Mono.empty();
        }
    }
    ```

# <div id='id27'/>
# 27. S3 Pruebas Txt

- Lectura del evento
- Mostrar log de extracción de datos (ZIP y TXT)

- Ubicarse en la siguiente carpeta, para este caso será el disco C: de windows
    ```
    C:\example-s3
    ```
    y crear el archivo: world-region-example.txt con los siguientes datos

    ```
    REGION-LATAM;d40b2031-2e14-4845-91cb-af0bf87a8ce3;Colombia;COUNTRY-COL
    REGION-LATAM;ff50f4f8-2dd1-4466-a55f-47ce560e1f19;argentina;COUNTRY-ARG
    COUNTRY-COL;0c3cbfbb-ef59-4e7e-a629-d64394f3dd77;Antioquia;DEPARTMENT-ANT
    DEPARTMENT-ANT;f46a680a-5b1d-4d18-a01b-a07e90176e3c;Medellin;CITY-MED
    ```

    ![](./img/modules/8_s3_carpeta_example.png)

- Abrir la consola de comandos desde la carpeta donde está el archivo y escribir los siguientes comandos para crear el bucket en nuestro localstack
    ```
    podman machine start
    podman start localstack

    -- Creamos el bucket
    aws --endpoint-url=http://localhost:4566 s3 mb s3://local-bucket-worldregion

    -- Subimos el archivo al bucket con una estructura de carpetas
    aws --endpoint-url=http://localhost:4566 s3 cp world-region-example.txt s3://local-bucket-worldregion/maestro/save/lote/world-region-example.txt

    -- Validamos que si se haya subido
    C:\example-s3>aws --endpoint-url=http://localhost:4566 s3 ls s3://local-bucket-worldregion/maestro/save/lote/

    -- Resultado
    2025-07-23 19:00:22        289 world-region-example.txt

    -- Iniciar contenedor de rabbit
    podman start rabbitmq-container

    -- Crear secreto si no se tiene
    aws secretsmanager create-secret --name local-rabbitmq --description "Connection to RabbitMQ" --secret-string "{\"virtualhost\":\"/\",\"hostname\":\"localhost\",\"username\":\"guest\",\"password\":\"guest\",\"port\":5672}" --endpoint-url=http://localhost:4566
    ```

- Ahora preparamos el evento para RabbitMQ
    ```
    {
        "name": "business.myapp.save-all.world-region",
        "eventId": "2ee1b68b-fc21-4250-9be5-d4ce81d972ab",
        "data": {
            "type": "business.myapp.save.country",
            "specVersion": "1.x-wip",
            "source": "other-microservicio",
            "id": "8888888-8888-8888",
            "time": "2025-07-18T08:44:02",
            "dataContentType": "application/json",
            "invoker": "",
            "data": {
                "transactionRequest": {
                    "headers": {
                        "user-name": "usertest",
                        "platform-type": "postman",
                        "ip": "172.34.45.12",
                        "message-id": "9999999-9999-0001",
                        "user-agent": "application/json"
                    }
                },
            "transactionResponse": {
                "statusResponse": "SUCCESS",
                "response": {
                    "fileName": "world-region-example.txt",
                    "bucketName": "local-bucket-worldregion",
                    "path": "maestro/save/lote/world-region-example.txt"
                    }
                }
            }
        }
    }
    ```

# <div id='id27'/>
# 28. S3 logs Txt
    ```
    {
        "instant": {
            "epochSecond": 1753324776,
            "nanoOfSecond": 998397600
        },
        "thread": "ApplicationEventListener-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Event save WorldRegion\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Save WorldRegion\",\"method\":\"co.com.microservice.aws.infrastructure.input.listenevent.events.WorldRegionEventLister\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"name\":\"business.myapp.save-all.world-region\",\"eventId\":\"2ee1b68b-fc21-4250-9be5-d4ce81d972ab\",\"data\":{\"type\":\"business.myapp.save.country\",\"specVersion\":\"1.x-wip\",\"source\":\"other-microservicio\",\"id\":\"8888888-8888-8888\",\"time\":\"2025-07-18T08:44:02\",\"dataContentType\":\"application/json\",\"invoker\":\"\",\"data\":{\"transactionRequest\":{\"headers\":{\"user-name\":\"usertest\",\"platform-type\":\"postman\",\"ip\":\"172.34.45.12\",\"message-id\":\"9999999-9999-0001\",\"user-agent\":\"application/json\"}},\"transactionResponse\":{\"statusResponse\":\"SUCCESS\",\"response\":{\"fileName\":\"world-region-example.txt\",\"bucketName\":\"local-bucket-worldregion\",\"path\":\"maestro/save/lote/world-region-example.txt\"}}}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 90,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753324777,
            "nanoOfSecond": 144724500
        },
        "thread": "sdk-async-response-1-0",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"getFile success\",\"messageId\":\"9999999-9999-0001\",\"service\":\"getFile\",\"method\":\"co.com.microservice.aws.infrastructure.output.s3repository.S3Adapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 99,
        "threadPriority": 5
    }
    [
        REGION-LATAM;d40b2031-2e14-4845-91cb-af0bf87a8ce3;Colombia;COUNTRY-COL, 
        REGION-LATAM;ff50f4f8-2dd1-4466-a55f-47ce560e1f19;argentina;COUNTRY-ARG, 
        COUNTRY-COL;0c3cbfbb-ef59-4e7e-a629-d64394f3dd77;Antioquia;DEPARTMENT-ANT, 
        DEPARTMENT-ANT;f46a680a-5b1d-4d18-a01b-a07e90176e3c;Medellin;CITY-MED
    ]
    ```

# <div id='id29'/>
# 29. S3 Pruebas zip

- Duplicar el archivo Txt con el nombre: world-region-example-for-zip.txt y comprimir este archivo

- Abrir la consola de comandos desde la carpeta donde está el archivo y escribir los siguientes comandos para crear el bucket en nuestro localstack
    ```
    -- Subimos el archivo al bucket con una estructura de carpetas
    aws --endpoint-url=http://localhost:4566 s3 cp world-region-example-for-zip.zip s3://local-bucket-worldregion/maestro/save/lote/world-region-example-for-zip.zip

    -- Validamos que si se haya subido
    aws --endpoint-url=http://localhost:4566 s3 ls s3://local-bucket-worldregion/maestro/save/lote/

    -- Resultado
    2025-07-24 11:06:03        442 world-region-example-for-zip.zip
    2025-07-23 19:00:22        289 world-region-example.txt
    ```

    ![](./img/modules/8_s3_carpeta_example_zip.png)

- Ahora preparamos el evento para RabbitMQ
    ```
    {
        "name": "business.myapp.save-all.world-region",
        "eventId": "2ee1b68b-fc21-4250-9be5-d4ce81d972ab",
        "data": {
            "type": "business.myapp.save.country",
            "specVersion": "1.x-wip",
            "source": "other-microservicio",
            "id": "8888888-8888-8888",
            "time": "2025-07-18T08:44:02",
            "dataContentType": "application/json",
            "invoker": "",
            "data": {
                "transactionRequest": {
                    "headers": {
                        "user-name": "usertest",
                        "platform-type": "postman",
                        "ip": "172.34.45.12",
                        "message-id": "9999999-9999-0001",
                        "user-agent": "application/json"
                    }
                },
            "transactionResponse": {
                "statusResponse": "SUCCESS",
                "response": {
                    "fileName": "world-region-example-for-zip.zip",
                    "bucketName": "local-bucket-worldregion",
                    "path": "maestro/save/lote/world-region-example-for-zip.zip"
                    }
                }
            }
        }
    }
    ```

# <div id='id30'/>
# 30. S3 logs zip
    ```
    {
        "instant": {
            "epochSecond": 1753375279,
            "nanoOfSecond": 694347700
        },
        "thread": "ApplicationEventListener-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Event save WorldRegion\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Save WorldRegion\",\"method\":\"co.com.microservice.aws.infrastructure.input.listenevent.events.WorldRegionEventLister\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"name\":\"business.myapp.save-all.world-region\",\"eventId\":\"2ee1b68b-fc21-4250-9be5-d4ce81d972ab\",\"data\":{\"type\":\"business.myapp.save.country\",\"specVersion\":\"1.x-wip\",\"source\":\"other-microservicio\",\"id\":\"8888888-8888-8888\",\"time\":\"2025-07-18T08:44:02\",\"dataContentType\":\"application/json\",\"invoker\":\"\",\"data\":{\"transactionRequest\":{\"headers\":{\"user-name\":\"usertest\",\"platform-type\":\"postman\",\"ip\":\"172.34.45.12\",\"message-id\":\"9999999-9999-0001\",\"user-agent\":\"application/json\"}},\"transactionResponse\":{\"statusResponse\":\"SUCCESS\",\"response\":{\"fileName\":\"world-region-example-for-zip.zip\",\"bucketName\":\"local-bucket-worldregion\",\"path\":\"maestro/save/lote/world-region-example-for-zip.zip\"}}}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 94,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753375279,
            "nanoOfSecond": 874320100
        },
        "thread": "sdk-async-response-1-0",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"getFile success\",\"messageId\":\"9999999-9999-0001\",\"service\":\"getFile\",\"method\":\"co.com.microservice.aws.infrastructure.output.s3repository.S3Adapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 103,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753375301,
            "nanoOfSecond": 150359700
        },
        "thread": "sdk-async-response-1-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"uploadObject success\",\"messageId\":\"9999999-9999-0001\",\"service\":\"uploadObject\",\"method\":\"co.com.microservice.aws.infrastructure.output.s3repository.S3Adapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 111,
        "threadPriority": 5
    }
    [
        REGION-LATAM;d40b2031-2e14-4845-91cb-af0bf87a8ce3;Colombia;COUNTRY-COL, 
        REGION-LATAM;ff50f4f8-2dd1-4466-a55f-47ce560e1f19;argentina;COUNTRY-ARG, 
        COUNTRY-COL;0c3cbfbb-ef59-4e7e-a629-d64394f3dd77;Antioquia;DEPARTMENT-ANT, 
        DEPARTMENT-ANT;f46a680a-5b1d-4d18-a01b-a07e90176e3c;Medellin;CITY-MED
    ]
    ```

- Ahora veamos en el bucket el archivo descomprimido
    ```
    aws --endpoint-url=http://localhost:4566 s3 ls s3://local-bucket-worldregion/maestro/save/lote/

    -- Resultado
    2025-07-24 11:41:41        289 world-region-example-for-zip.txt
    2025-07-24 11:06:03        442 world-region-example-for-zip.zip
    2025-07-23 19:00:22        289 world-region-example.txt
    ```

# <div id='id31'/>
# 31. DynamoDB Configuración

- Ubicarse en el archivo application-local.yaml y colocar la siguiente información: agregar en adapters la información de dynamodb
```
adapters:
  dynamodb:
    endpoint: "http://localhost:4566"
  repositories:
    tables:
      namesmap:
        world_region: local_worldregions
```

- Ubicarse en el archivo build.gradle y colocar la dependencia de dynamodb
    ```
    implementation 'software.amazon.awssdk:dynamodb-enhanced'
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.config y crear la clase DynamoDbTableAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.config;

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

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.model y crear la clase SourceName.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.model;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class SourceName {
        public static final String WORLD_REGION = "world_region";
    }
    ```

    **Importante**: se debe colocar tal cual está en el archivo de configuración

    ![](./img/config-driver-dynamodb.png)

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.model y crear la clase ModelEntityWorldRegion.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.model;

    import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDbTableAdapter;
    import lombok.Data;
    import lombok.Getter;
    import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

    @Data
    @DynamoDbBean
    @DynamoDbTableAdapter(tableName = SourceName.WORLD_REGION)
    public class ModelEntityWorldRegion {
        @Getter(onMethod_ = @DynamoDbPartitionKey)
        private String region;

        @Getter(onMethod_ = @DynamoDbSortKey)
        private String code;

        private String name;
        private String codeRegion;
        private String creationDate;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.config y crear la clase DynamoDBConfig.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Profile;
    import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
    import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
    import software.amazon.awssdk.core.SdkSystemSetting;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.regions.Region;
    import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

    import java.net.URI;

    @Configuration
    public class DynamoDBConfig {
        @Bean
        @Profile({"local"})
        public DynamoDbAsyncClient amazonDynamoDB(@Value("${adapters.dynamodb.endpoint}") String endpoint) {
            return DynamoDbAsyncClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create("default"))
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(SdkSystemSetting.AWS_REGION.environmentVariable()))
                    .build();
        }
        @Bean
        @Profile({"!local"})
        public DynamoDbAsyncClient amazonDynamoDBAsync(@Value("${adapters.dynamodb.region}") String region) {
            return DynamoDbAsyncClient.builder()
                    .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .region(Region.of(region)).build();
        }
        @Bean
        public DynamoDbEnhancedAsyncClient dynamoClient(DynamoDbAsyncClient dynamoDbAsyncClient){
            return DynamoDbEnhancedAsyncClient.builder()
                    .dynamoDbClient(dynamoDbAsyncClient)
                    .build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.config y crear la clase DynamoDBTablesProperties.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.config;

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

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.operations y crear la clase DynamoDBOperations.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.operations;

    import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDBTablesProperties;
    import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDbTableAdapter;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.enhanced.dynamodb.Key;
    import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

    import java.util.function.Function;

    public class DynamoDBOperations<E, D>{
        protected DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient;
        protected Function<E, D> fnToData;
        protected Function<D, E> fnToEntity;
        protected DynamoDbAsyncTable<D> dataTable;

        public DynamoDBOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                                DynamoDBTablesProperties tablesProperties, Function<E, D> fnToData,
                                Function<D, E> fnToEntity, Class<D> dataClass) {

            this.dbEnhancedAsyncClient = dbEnhancedAsyncClient;
            this.fnToData = fnToData;
            this.fnToEntity = fnToEntity;
            DynamoDbTableAdapter dynamoDbTableAdapter = dataClass.getAnnotation(DynamoDbTableAdapter.class);
            String tableName = tablesProperties.getNamesmap().get(dynamoDbTableAdapter.tableName());
            dataTable = dbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
        }

        public Mono<E> save(E entity) {
            return Mono.just(entity).map(this::toData).flatMap(this::saveData).thenReturn(entity);
        }

        protected Mono<E> findOne(Key id) {
            return Mono.fromFuture(dataTable.getItem(id)).map(this::toEntity);
        }

        protected Mono<E> delete(Key id) {
            return deleteData(id).map(this::toEntity);
        }

        protected Mono<E> update(E entity) {
            return Mono.fromFuture(dataTable.updateItem(toData(entity))).map(this::toEntity);
        }

        protected Mono<D> saveData(D data) {
            return Mono.fromFuture(dataTable.putItem(data)).thenReturn(data);
        }

        protected Mono<D> deleteData(Key id) {
            return Mono.fromFuture(dataTable.deleteItem(id));
        }

        protected Flux<E> doQueryMany(Flux<D> query) {
            return query.map(this::toEntity);
        }

        protected D toData(E entity) {
            return fnToData.apply(entity);
        }

        protected E toEntity(D data) {
            return data != null ? fnToEntity.apply(data) : null;
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.operations y crear la clase AdapterOperations.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.operations;

    import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDBTablesProperties;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

    import java.util.function.Function;

    public class AdapterOperations<E, D> extends DynamoDBOperations<E, D> {

        public AdapterOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                                DynamoDBTablesProperties tablesProperties,
                                Function<E, D> fnToData, Function<D, E> fnToEntity,
                                Class<D> dataClass) {

            super(dbEnhancedAsyncClient, tablesProperties, fnToData, fnToEntity, dataClass);
        }

        @Override
        protected E toEntity(D data) {
            return fnToEntity.apply(data);
        }

        protected Flux<E> findByQuery(QueryEnhancedRequest queryRequest) {
            return Mono.just(dataTable)
                    .flatMap(index -> Mono.from(index.query(queryRequest)))
                    .flatMapMany(page -> doQueryMany(Flux.fromIterable(page.items())))
                    .onErrorResume(err -> Flux.empty());
        }

    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.model y crear la clase WorldRegion.java
    ```
    package co.com.microservice.aws.domain.model;

    import lombok.*;

    import java.io.Serial;
    import java.io.Serializable;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class WorldRegion implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String region;
        private String code;
        private String name;
        private String codeRegion;
        private String creationDate;
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb.mapper y crear la clase WorldRegionDataMapper.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb.mapper;

    import co.com.microservice.aws.domain.model.WorldRegion;
    import co.com.microservice.aws.infrastructure.output.dynamodb.model.ModelEntityWorldRegion;
    import org.springframework.stereotype.Component;

    @Component
    public class WorldRegionDataMapper {

        public ModelEntityWorldRegion toEntity(WorldRegion worldRegion) {
            if (worldRegion == null) return null;

            ModelEntityWorldRegion entity = new ModelEntityWorldRegion();
            entity.setRegion(worldRegion.getRegion());
            entity.setCode(worldRegion.getCode());
            entity.setName(worldRegion.getName());
            entity.setCodeRegion(worldRegion.getCodeRegion());
            entity.setCreationDate(worldRegion.getCreationDate());

            return entity;
        }

        public WorldRegion toData(ModelEntityWorldRegion entity) {
            if (entity == null) return null;

            return WorldRegion.builder()
                    .region(entity.getRegion())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .codeRegion(entity.getCodeRegion())
                    .creationDate(entity.getCreationDate())
                    .build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.domain.usecase.out y crear la clase WorldRegionPort.java
    ```
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
    ```

- Ubicarse en el paquete co.com.microservice.aws.infrastructure.output.dynamodb y crear la clase WorldRegionAdapter.java
    ```
    package co.com.microservice.aws.infrastructure.output.dynamodb;

    import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.aws.application.helpers.logs.TransactionLog;
    import co.com.microservice.aws.domain.model.WorldRegion;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.usecase.out.WorldRegionPort;
    import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDBTablesProperties;
    import co.com.microservice.aws.infrastructure.output.dynamodb.mapper.WorldRegionDataMapper;
    import co.com.microservice.aws.infrastructure.output.dynamodb.model.ModelEntityWorldRegion;
    import co.com.microservice.aws.infrastructure.output.dynamodb.operations.AdapterOperations;
    import org.springframework.stereotype.Component;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;
    import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
    import software.amazon.awssdk.enhanced.dynamodb.Key;
    import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
    import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

    @Component
    public class WorldRegionAdapter extends AdapterOperations<WorldRegion, ModelEntityWorldRegion>
                implements WorldRegionPort {
        private LoggerBuilder logger;

        public WorldRegionAdapter(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                DynamoDBTablesProperties tablesProperties,
                WorldRegionDataMapper mapper, LoggerBuilder loggerBuilder) {

            super(dbEnhancedAsyncClient, tablesProperties, mapper::toEntity,
                    mapper::toData, ModelEntityWorldRegion.class);

            this.logger = loggerBuilder;

        }

        @Override
        public Flux<WorldRegion> findByRegion(Context context, String region) {
            QueryEnhancedRequest request = QueryEnhancedRequest
                    .builder()
                    .queryConditional(QueryConditional.keyEqualTo(buildKey(region)))
                    .build();

            return super.findByQuery(request);
        }

        @Override
        public Mono<WorldRegion> findOne(Context context, String region, String code) {
            return super.findOne(buildKey(region, code));
        }

        @Override
        public Mono<WorldRegion> save(Context context, WorldRegion worldRegion) {
            logger.info("save dynamodb", context.getId(), "WorldRegionAdapter", "save");
            logger.info(TransactionLog.Request.builder().body(worldRegion).build(), "request", context.getId(), "", "");
            return super.save(worldRegion)
                    .doOnNext(r -> logger.info(TransactionLog.Response.builder().body(r).build(), "response save", context.getId(), "", ""))
                    .switchIfEmpty(Mono.fromRunnable(() ->
                            logger.info("No se retornó ninguna respuesta del guardado", context.getId(), "", "")
                    ));
        }

        @Override
        public Mono<WorldRegion> update(Context context, WorldRegion worldRegion) {
            return super.update(worldRegion);
        }

        @Override
        public Mono<WorldRegion> delete(Context context, String region, String code) {
            return super.delete(buildKey(region, code));
        }

        private Key buildKey(String partitionValue, String sortValue) {
            return Key.builder().partitionValue(partitionValue).sortValue(sortValue).build();
        }

        private Key buildKey(String partitionValue) {
            return Key.builder().partitionValue(partitionValue).build();
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.aws.application.usecase y modificar la clase WorldRegionUseCaseImpl.java
    ```
    package co.com.microservice.aws.application.usecase;

    import co.com.microservice.aws.application.helpers.commons.UseCase;
    import co.com.microservice.aws.application.helpers.file.FlatFile;
    import co.com.microservice.aws.application.helpers.file.model.FileData;
    import co.com.microservice.aws.application.helpers.utils.FileStructureValidator;
    import co.com.microservice.aws.domain.model.WorldRegion;
    import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
    import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
    import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
    import co.com.microservice.aws.domain.model.rq.Context;
    import co.com.microservice.aws.domain.model.rq.TransactionRequest;
    import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
    import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
    import co.com.microservice.aws.domain.usecase.out.WorldRegionPort;
    import lombok.RequiredArgsConstructor;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.time.LocalDateTime;

    import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

    @UseCase
    @RequiredArgsConstructor
    public class WorldRegionUseCaseImpl implements WorldRegionUseCase {
        private final FileStoragePort fileStoragePort;
        private final WorldRegionPort worldRegionPort;

        @Override
        public Mono<String> processFile(TransactionRequest request) {
            return Mono.just(request)
                    .map(TransactionRequest::getItem)
                    .flatMap(this::buildWorldRegion)
                    .flatMap(infobucket -> this.getFile(infobucket, request.getContext()));
        }

        private Mono<String> getFile(ProccessWorldRegionFile.InfoBucket infoBucket, Context context){
            return Mono.just(infoBucket)
                .flatMap(ib -> fileStoragePort.getFile(context, ib.getBucketName(), ib.getPath()))
                .flatMap(FlatFile::getValue).onErrorResume(Mono::error)
                .flatMap(flatfile -> this.validateFile(flatfile, infoBucket, context))
                .flatMap(flatfile -> this.save(flatfile, context))
                .flatMap(msg -> fileStoragePort.deleteObject(context, infoBucket.getBucketName(), infoBucket.getPath()).thenReturn(msg))
                .onErrorResume(Mono::error);
        }

        private Mono<String[]> validateFile(FileData fileData, ProccessWorldRegionFile.InfoBucket infoBucket, Context context) {
            var fileLines = FileStructureValidator.getFileLines(fileData.getData());
            var monoReturn = Mono.just(Boolean.TRUE);
            if (fileData.isZip()) {
                var path = FileStructureValidator.changeZipToTxt(infoBucket.getPath());
                var bucketName = infoBucket.getBucketName();

                monoReturn = fileStoragePort.uploadObject(context, bucketName, path,
                        fileData.getData().getBytes());
            }

            return monoReturn.flatMap(b -> Mono.just(fileLines));
        }

        private Mono<ProccessWorldRegionFile.InfoBucket> buildWorldRegion(Object object){
            if (object instanceof ProccessWorldRegionFile.InfoBucket infoFile) {
                return Mono.just(infoFile);
            } else {
                return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
            }
        }

        private Mono<String> save(String[] flatfile, Context context){
            return Flux.fromArray(flatfile)
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> {
                    String[] parts = line.split(";");
                    return WorldRegion.builder().region(parts[0])
                            .code(parts[1]).name(parts[2])
                            .codeRegion(parts[3]).creationDate(LocalDateTime.now().toString())
                            .build();
                })
                .flatMap(worldRegion -> worldRegionPort.save(context, worldRegion))
                .then(Mono.just(ResponseMessageConstant.MSG_SAVED_SUCCESS));
        }
    }
    ```

# <div id='id32'/>
# 32. DynamoDB: Pruebas

- Creación de la estructura de la tabla
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name local_worldregions --attribute-definitions AttributeName=region,AttributeType=S AttributeName=code,AttributeType=S --key-schema AttributeName=region,KeyType=HASH AttributeName=code,KeyType=RANGE --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
    ```
    ### Aclaración de atributos en el comando `aws dynamodb create-table`

    - partitionkey → `region`, country-col, region-latam, etc.

    - sortkey → `code` (usaremos UUID para simular unicidad)

    ### Buenas prácticas de diseño
    | Concepto                      | Explicación breve                                                                 |
    |------------------------------|------------------------------------------------------------------------------------|
    | 🔑 partitionKey debe repetirse | Así puedes agrupar varios ítems relacionados y usar query por PK.                 |
    | 📚 sortKey debe diferenciar ítems | Dentro del grupo de PK, sirve para ordenar o filtrar.                            |
    | 🔍 getItem(PK, SK)            | Recupera 1 solo ítem (requiere ambos).                                            |
    | 📈 query(PK)                  | Recupera todos los ítems con esa PK (opcionalmente con condiciones en SK).       |

    ### Conclusiones:

    - ✔️ Usa partitionKey para agrupar ítems relacionados
    - ✔️ Usa sortKey para ordenar o identificar únicos dentro del grupo
    - ✔️ La combinación PK+SK es lo que hace único un ítem
    - ✔️ Puedes hacer query(PK) sin SK para traer todos los del grupo

- Así queda creada la tabla
    ```
    {
        "TableDescription": {
            "AttributeDefinitions": [
                {
                    "AttributeName": "region",
                    "AttributeType": "S"
                },
                {
                    "AttributeName": "code",
                    "AttributeType": "S"
                }
            ],
            "TableName": "local_worldregions",
            "KeySchema": [
                {
                    "AttributeName": "region",
                    "KeyType": "HASH"
                },
                {
                    "AttributeName": "code",
                    "KeyType": "RANGE"
                }
            ],
            "TableStatus": "ACTIVE",
            "CreationDateTime": "2025-06-24T23:30:18.460000-05:00",
            "ProvisionedThroughput": {
                "LastIncreaseDateTime": "1969-12-31T19:00:00-05:00",
                "LastDecreaseDateTime": "1969-12-31T19:00:00-05:00",
                "NumberOfDecreasesToday": 0,
                "ReadCapacityUnits": 5,
                "WriteCapacityUnits": 5
            },
            "TableSizeBytes": 0,
            "ItemCount": 0,
            "TableArn": "arn:aws:dynamodb:us-east-1:000000000000:table/local_worldregions"
        }
    }
    ```

- Ingreso items a la tabla (*Los comandos estan organizados para ejecutar en command line de windows*)
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"REGION-LATAM\"},\"code\":{\"S\":\"d40b2031-2e14-4845-91cb-af0bf87a8ce3\"},\"name\":{\"S\":\"Colombia\"},\"codeRegion\":{\"S\":\"COUNTRY-COL\"},\"creationDate\":{\"S\":\"2025-06-24T20:15:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"REGION-LATAM\"},\"code\":{\"S\":\"ff50f4f8-2dd1-4466-a55f-47ce560e1f19\"},\"name\":{\"S\":\"argentina\"},\"codeRegion\":{\"S\":\"COUNTRY-ARG\"},\"creationDate\":{\"S\":\"2025-06-24T20:16:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"COUNTRY-COL\"},\"code\":{\"S\":\"0c3cbfbb-ef59-4e7e-a629-d64394f3dd77\"},\"name\":{\"S\":\"Antioquia\"},\"codeRegion\":{\"S\":\"DEPARTMENT-ANT\"},\"creationDate\":{\"S\":\"2025-06-24T20:17:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"DEPARTMENT-ANT\"},\"code\":{\"S\":\"f46a680a-5b1d-4d18-a01b-a07e90176e3c\"},\"name\":{\"S\":\"Medellin\"},\"codeRegion\":{\"S\":\"CITY-MED\"},\"creationDate\":{\"S\":\"2025-06-24T20:18:00Z\"}}"
    ```

- Ubicarse en la siguiente carpeta, para este caso será el disco C: de windows
    ```
    C:\example-s3
    ```
    y crear el archivo: world-region-example-dynamo.txt con los siguientes datos

    ```
    REGION-LATAM;d40b2031-2e14-4845-91cb-af0bf87a8c99;Estados unidos;COUNTRY-USA
    REGION-LATAM;ff50f4f8-2dd1-4466-a55f-47ce560e1f99;Brasil;COUNTRY-BRA
    COUNTRY-USA;0c3cbfbb-ef59-4e7e-a629-d64394f3dd00;Texas;DEPARTMENT-TEX
    DEPARTMENT-ANT;f46a680a-5b1d-4d18-a01b-a07e90176e00;Envigado;CITY-ENV
    ```

- Subimos el archivo a S3 local
    ```
    aws --endpoint-url=http://localhost:4566 s3 cp world-region-example-dynamo.txt s3://local-bucket-worldregion/maestro/save/lote/world-region-example-dynamo.txt
    ```

- Ahora preparamos el evento para RabbitMQ
    ```
    {
        "name": "business.myapp.save-all.world-region",
        "eventId": "2ee1b68b-fc21-4250-9be5-d4ce81d972ab",
        "data": {
            "type": "business.myapp.save.country",
            "specVersion": "1.x-wip",
            "source": "other-microservicio",
            "id": "8888888-8888-8888",
            "time": "2025-07-18T08:44:02",
            "dataContentType": "application/json",
            "invoker": "",
            "data": {
                "transactionRequest": {
                    "headers": {
                        "user-name": "usertest",
                        "platform-type": "postman",
                        "ip": "172.34.45.12",
                        "message-id": "9999999-9999-0001",
                        "user-agent": "application/json"
                    }
                },
            "transactionResponse": {
                "statusResponse": "SUCCESS",
                "response": {
                    "fileName": "world-region-example-dynamo.txt",
                    "bucketName": "local-bucket-worldregion",
                    "path": "maestro/save/lote/world-region-example-dynamo.txt"
                    }
                }
            }
        }
    }
    ```

- Consultamos la información creada en DynamoDB
    ```
    aws dynamodb scan --table-name local_worldregions --endpoint-url http://localhost:4566 --output json

    -- Se muestran algunos datos

    "Items": [
        {
            "name": {
                "S": "Texas"
            },
            "code": {
                "S": "0c3cbfbb-ef59-4e7e-a629-d64394f3dd00"
            },
            "codeRegion": {
                "S": "DEPARTMENT-TEX"
            },
            "creationDate": {
                "S": "2025-07-24T18:46:29.133736900"
            },
            "region": {
                "S": "COUNTRY-USA"
            }
        },
         "Items": [
        {
            "name": {
                "S": "Texas"
            },
            "code": {
                "S": "0c3cbfbb-ef59-4e7e-a629-d64394f3dd00"
            },
            "codeRegion": {
                "S": "DEPARTMENT-TEX"
            },
            "creationDate": {
                "S": "2025-07-24T18:46:29.133736900"
            },
            "region": {
                "S": "COUNTRY-USA"
            }
        },
        {
            "name": {
                "S": "Antioquia"
            },
            "code": {
                "S": "0c3cbfbb-ef59-4e7e-a629-d64394f3dd77"
            },
            "codeRegion": {
                "S": "DEPARTMENT-ANT"
            },
            "creationDate": {
                "S": "2025-07-24T17:33:25.077302800"
            },
            "region": {
                "S": "COUNTRY-COL"
            }
        }
    ]
    ```

- Validamos que el archivo fue borrado en el S3
    ```
    aws --endpoint-url=http://localhost:4566 s3 ls s3://local-bucket-worldregion/maestro/save/lote/
    ```

# <div id='id33'/>
# 33. DynamoDb: Logs

- Ahora preparamos el evento para RabbitMQ
    ```
    {
        "instant": {
            "epochSecond": 1753400700,
            "nanoOfSecond": 3540400
        },
        "thread": "scheduling-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "Executed cron",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 79,
        "threadPriority": 5
    }

    -- Imprime el evento
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 62483700
        },
        "thread": "ApplicationEventListener-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Event save WorldRegion\",\"messageId\":\"9999999-9999-0001\",\"service\":\"Save WorldRegion\",\"method\":\"co.com.microservice.aws.infrastructure.input.listenevent.events.WorldRegionEventLister\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"name\":\"business.myapp.save-all.world-region\",\"eventId\":\"2ee1b68b-fc21-4250-9be5-d4ce81d972ab\",\"data\":{\"type\":\"business.myapp.save.country\",\"specVersion\":\"1.x-wip\",\"source\":\"other-microservicio\",\"id\":\"8888888-8888-8888\",\"time\":\"2025-07-18T08:44:02\",\"dataContentType\":\"application/json\",\"invoker\":\"\",\"data\":{\"transactionRequest\":{\"headers\":{\"user-name\":\"usertest\",\"platform-type\":\"postman\",\"ip\":\"172.34.45.12\",\"message-id\":\"9999999-9999-0001\",\"user-agent\":\"application/json\"}},\"transactionResponse\":{\"statusResponse\":\"SUCCESS\",\"response\":{\"fileName\":\"world-region-example-dynamo.txt\",\"bucketName\":\"local-bucket-worldregion\",\"path\":\"maestro/save/lote/world-region-example-dynamo.txt\"}}}}}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 143,
        "threadPriority": 5
    }

    -- Obtiene el archivo desde S3
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 85694100
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"getFile success\",\"messageId\":\"9999999-9999-0001\",\"service\":\"getFile\",\"method\":\"co.com.microservice.aws.infrastructure.output.s3repository.S3Adapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }

    -- Para por el caso de uso para guardar
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 116420500
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"save dynamodb\",\"messageId\":\"9999999-9999-0001\",\"service\":\"WorldRegionAdapter\",\"method\":\"save\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }

    -- Pasa por el repositorio de dynamodb con la info
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 116420500
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"request\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"region\":\"REGION-LATAM\",\"code\":\"d40b2031-2e14-4845-91cb-af0bf87a8c99\",\"name\":\"Estados unidos\",\"codeRegion\":\"COUNTRY-USA\",\"creationDate\":\"2025-07-24T18:46:29.116420500\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 133736900
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"save dynamodb\",\"messageId\":\"9999999-9999-0001\",\"service\":\"WorldRegionAdapter\",\"method\":\"save\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 133736900
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"request\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"region\":\"REGION-LATAM\",\"code\":\"ff50f4f8-2dd1-4466-a55f-47ce560e1f99\",\"name\":\"Brasil\",\"codeRegion\":\"COUNTRY-BRA\",\"creationDate\":\"2025-07-24T18:46:29.133736900\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 133736900
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"save dynamodb\",\"messageId\":\"9999999-9999-0001\",\"service\":\"WorldRegionAdapter\",\"method\":\"save\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 133736900
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"request\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"region\":\"COUNTRY-USA\",\"code\":\"0c3cbfbb-ef59-4e7e-a629-d64394f3dd00\",\"name\":\"Texas\",\"codeRegion\":\"DEPARTMENT-TEX\",\"creationDate\":\"2025-07-24T18:46:29.133736900\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 150370600
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"save dynamodb\",\"messageId\":\"9999999-9999-0001\",\"service\":\"WorldRegionAdapter\",\"method\":\"save\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 150370600
        },
        "thread": "sdk-async-response-1-3",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"request\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"region\":\"DEPARTMENT-ANT\",\"code\":\"f46a680a-5b1d-4d18-a01b-a07e90176e3c\",\"name\":\"Envigado\",\"codeRegion\":\"CITY-ENV\",\"creationDate\":\"2025-07-24T18:46:29.150370600\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 144,
        "threadPriority": 5
    }

    -- Luego de guardar muestra el elemento almacenado en dynamodb
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 266035300
        },
        "thread": "sdk-async-response-4-4",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"response save\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"region\":\"REGION-LATAM\",\"code\":\"ff50f4f8-2dd1-4466-a55f-47ce560e1f99\",\"name\":\"Brasil\",\"codeRegion\":\"COUNTRY-BRA\",\"creationDate\":\"2025-07-24T18:46:29.133736900\"}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 145,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 266035300
        },
        "thread": "sdk-async-response-4-5",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"response save\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"region\":\"REGION-LATAM\",\"code\":\"d40b2031-2e14-4845-91cb-af0bf87a8c99\",\"name\":\"Estados unidos\",\"codeRegion\":\"COUNTRY-USA\",\"creationDate\":\"2025-07-24T18:46:29.116420500\"}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 146,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 267083100
        },
        "thread": "sdk-async-response-4-6",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"response save\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"region\":\"DEPARTMENT-ANT\",\"code\":\"f46a680a-5b1d-4d18-a01b-a07e90176e3c\",\"name\":\"Envigado\",\"codeRegion\":\"CITY-ENV\",\"creationDate\":\"2025-07-24T18:46:29.150370600\"}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 147,
        "threadPriority": 5
    }
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 286325800
        },
        "thread": "sdk-async-response-4-7",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"response save\",\"messageId\":\"9999999-9999-0001\",\"service\":\"\",\"method\":\"\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":{\"headers\":null,\"body\":{\"region\":\"COUNTRY-USA\",\"code\":\"0c3cbfbb-ef59-4e7e-a629-d64394f3dd00\",\"name\":\"Texas\",\"codeRegion\":\"DEPARTMENT-TEX\",\"creationDate\":\"2025-07-24T18:46:29.133736900\"}}}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 148,
        "threadPriority": 5
    }

    -- Finalmente elimina el archivo luego de procesar todo
    {
        "instant": {
            "epochSecond": 1753400789,
            "nanoOfSecond": 329644400
        },
        "thread": "sdk-async-response-1-4",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"deleteObject success\",\"messageId\":\"9999999-9999-0001\",\"service\":\"deleteObject\",\"method\":\"co.com.microservice.aws.infrastructure.output.s3repository.S3Adapter\",\"appName\":\"MicroserviceAws\"},\"request\":null,\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 149,
        "threadPriority": 5
    }
    ```

[< Volver al índice](README.md)

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)