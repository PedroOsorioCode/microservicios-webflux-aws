# Instructivo paso a paso CRUD Postgresql database
> A continuación se realiza la creación base del proyecto basada en microservicios por contexto y arquitectura limpia

### Requisitos

- ⚠️ Java 17 o superior
- ⚠️ Gradle 8.8 o posterior
- ⚠️ Docker o Podman
- ⚠️ Postman
- ⚠️ Intellij

### Clean Architecture: 

[Plugin documentación](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro)

### Crear proyecto:

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

2. Cargar proyecto en intellij y crear paquetes de arquitectura limpia

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
        Actualizar dependencias

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

        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation "org.apache.logging.log4j:log4j-core:2.24.3"
            implementation "org.apache.logging.log4j:log4j-api:2.24.3"
            implementation "org.apache.logging.log4j:log4j-to-slf4j:2.24.3"
            implementation 'com.fasterxml.jackson.core:jackson-databind'
            implementation 'io.r2dbc:r2dbc-h2'
            compileOnly 'org.projectlombok:lombok'
            developmentOnly 'org.springframework.boot:spring-boot-devtools'
            runtimeOnly 'org.postgresql:postgresql'
            runtimeOnly 'org.postgresql:r2dbc-postgresql'
            annotationProcessor 'org.projectlombok:lombok'
            testImplementation 'org.springframework.boot:spring-boot-starter-test'
            testImplementation 'io.projectreactor:reactor-test'
            testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
        }

        // Para evitar conflicto con Logback
        configurations {
            all {
                exclude group: 'org.apache.logging.log4j', module: 'log4j-to-slf4j'
                exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
            }
        }

        tasks.named('test') {
            useJUnitPlatform()
        }
        ```
        Actualizar dependencias

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
        countries-web:
            path-base: "${PATH_BASE:/api/v2/microservice-aws}"
            listAll: "/list-all"
            findOne: "/find-one/{name}"
            save: "/save"
            update: "/update"
            delete: "/delete/{id}"
        regex-body-wr:
            name: "${REGEX_COUNTRY_NAME:^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$}"
            codeShort: "${REGEX_COUNTRY_CODE_SHORT:^[a-zA-Z]{3,4}$}"

        adapters:
        postgresql:
            url: r2dbc:postgresql://localhost:5432/my_postgres_db
            username: postgres
            password: postgres123
        mysql:
            url: r2dbc:mysql://localhost:3306/my_mysql_db
            username: root
            password: root123
        ```

    - Abrir el archivo MicroserviceAwsApplication.java y click derecho y ejecutar la aplicación
    
        ![](./img/modules/2-ejecutar-aplicacion.png)

    - Configurar la aplicación para ejecutar de forma local
        ```
        SPRING_PROFILES_ACTIVE=local
        ```

        ![](./img/proyecto-base-config-perfil-local.png)

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
            private transient Map<String, String> params;
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
            private String size;
            private List<Object> response;
        }
        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
    - Ubicarse en el paquete co.com.microservice.aws.infrastructure.input.rest.api.exception y crear la clase ExceptionHandler.java
        ```

        ```
