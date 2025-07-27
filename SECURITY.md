# Instructivo paso a paso API REST simple, Spring security y JWT

> A continuación se describe la configuración base de un proyecto construido bajo principios de microservicios y arquitectura limpia, integrando seguridad

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

### Requisitos

- ⚠️ Instalar Java 17 o superior
- ⚠️ Gradle 8.8 o posterior
- ⚠️ Instalar Docker o Podman
- ⚠️ Instalar Postman
- ⚠️ Instalar Intellij
- ⚠️ Instalar Plugin lombok en intellij


### Indice

* [1. Crear proyecto](#id1)
* [2. Crear la aplicación](#id2)
* [3. Crear servicios GET](#id3)

# <div id='id1'/>
# 1. Crear y configurar el proyecto:

1. Datos del proyecto
    - Visitar el sitio [Spring initializr](https://start.spring.io/)
    - Ver historial

    ![](./img/modules/security/1-spring-initializr.png)

    - Información:
      - group: co.com.microservice.jwt
      - artifact: microservice-jwt
      - name: microservice-jwt
      - description: A microservice based on Clean Architecture, built using Spring WebFlux for reactive, non-blocking operations, and security with JWT
      - package-name: co.com.microservice.jwt
      - packaging: jar 

    - Dependencias base:
      - Spring Reactive Web (spring-boot-starter-webflux)
      - Lombok (org.projectlombok:lombok)
      - Spring Boot DevTools

    - Configurar proyecto

    ![](./img/modules/security/2-spring-initializr.png)

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

        rootProject.name = 'microservice-jwt'
        ```
        ⚠️ Actualizar dependencias
    
    - Ubicarse en la raiz del proyecto y modificar el archivo build.gradle por
        ```
        plugins {
            id 'java'
            id 'org.springframework.boot' version '3.5.3'
            id 'io.spring.dependency-management' version '1.1.7'
        }

        group = 'co.com.microservice.jwt'
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
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation "org.apache.logging.log4j:log4j-core:2.24.3"
            implementation "org.apache.logging.log4j:log4j-api:2.24.3"
            implementation "org.apache.logging.log4j:log4j-to-slf4j:2.24.3"

            compileOnly 'org.projectlombok:lombok'
            developmentOnly 'org.springframework.boot:spring-boot-devtools'
            annotationProcessor 'org.projectlombok:lombok'
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
        ⚠️ Actualizar dependencias

    - Ubicarse en src > main > resources y crear el archivo application.yaml y de igual forma application-local.yaml con lo siguiente
```
server:
  port: ${APP_PORT:8080}

spring:
  application:
    name: "${APP_NAME:microservice-jwt}"

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
      base-path: "${PATH_BASE:/api/v1/microservice-jwt/}"
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
    path-base: "${PATH_BASE:/api/v1/microservice-jwt}"
    path-public: "${PATH_PUBLIC:/public}"
    path-private: "${PATH_PRIVATE:/private}"
    path-user: "${PATH_USER:/user}"
    path-info: "${PATH_INFO:/info}"
    validate-security: "${PATH_VALIDATE_SECURITY:/validate}"
    token: "${PATH_TOKEN_SECURITY:/token}"
```
⚠️ Algunas configuraciones serán de importancia en desarrollos mas adelante

- Abrir el archivo MicroserviceJwtApplication.java y click derecho y ejecutar la aplicación

- Configurar la aplicación para ejecutar de forma local
    ```
    SPRING_PROFILES_ACTIVE=local
    ```

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

- 💡 **Tip**, si en algun momento requieres mover una clase a un paquete, lo haces haciendo click derecho sobre la clase, elegir opción refactor, luego la opción move class y escribes el paquete o en los "..." puedes elegir en forma de arbol el paquete donde deseas ubicar la clase

- Ubicarse en el paquete co.com.microservice.jwt.application.helpers.logs y crear la clase TransactionLog.java
    ```
    package co.com.microservice.jwt.application.helpers.logs;

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

- Ubicarse en el paquete co.com.microservice.jwt.application.helpers.logs y crear la clase LoggerBuilder.java

    ```
    package co.com.microservice.jwt.application.helpers.logs;

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

- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.config y crear la clase RouterProperties.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.config;

    import lombok.Data;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;

    @Data
    @Component
    @ConfigurationProperties(prefix = "entries.web")
    public class RouterProperties {
        private String pathBase;
        private String pathPublic;
        private String pathPrivate;
        private String pathUser;
        private String pathInfo;
        private String validateSecurity;
        private String token;
    }
    ```
- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.handler y crear la clase CountryHandler.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.handler;

    import co.com.microservice.jwt.application.helpers.logs.LoggerBuilder;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class UserHandler {
        private static final String NAME_CLASS = UserHandler.class.getName();
        private static final String MESSAGE_ID = "message-id";
        private final LoggerBuilder logger;

        public Mono<ServerResponse> validate(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            printOnProcess(headers.get(MESSAGE_ID), "validate user");
            return ServerResponse.ok().bodyValue("Hi validate, " + headers.get("user-name") + "!");
        }

        public Mono<ServerResponse> info(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            printOnProcess(headers.get(MESSAGE_ID), "info user");
            return ServerResponse.ok().bodyValue("Hi info user");
        }

        private void printOnProcess(String messageId, String messageService){
            logger.info("Api Rest simple", messageId, messageService, NAME_CLASS);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.router y crear la clase CountryRouterRest.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.router;

    import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
    import co.com.microservice.jwt.infrastructure.input.rest.api.handler.UserHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.RouterFunctions;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class UserRouterRest {
        private final RouterProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerUserFunction(UserHandler userHandler) {
            return RouterFunctions.route()
                .GET(createRoutePublic(properties.getPathInfo()), userHandler::info)
                .GET(createRoutePrivate(
                    properties.getPathUser().concat(properties.getValidateSecurity())), userHandler::validate)
                .build();
        }

        private String createRoutePublic(String route){
            return properties.getPathBase()
                    .concat(properties.getPathPublic())
                    .concat(route);
        }

        private String createRoutePrivate(String route){
            return properties.getPathBase()
                    .concat(properties.getPathPrivate())
                    .concat(route);
        }
    }
    ```

- Curls para hacer pruebas
    - Url privada
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/private/user/validate' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'
    ```

    - Url pública
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/private/user/validate' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'
    ```

# <div id='id3'/>
# 3. Asegurar la API

- Ubicarse en la raiz del proyecto y modificar el archivo build.gradle por
    ```
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-webflux'
        implementation "org.apache.logging.log4j:log4j-core:2.24.3"
        implementation "org.apache.logging.log4j:log4j-api:2.24.3"
        implementation "org.apache.logging.log4j:log4j-to-slf4j:2.24.3"

        implementation 'org.springframework.boot:spring-boot-starter-security'
        implementation 'org.springframework.boot:spring-boot-starter-webflux'
        implementation 'io.jsonwebtoken:jjwt-api:0.11.5'

        runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
        runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'

        compileOnly 'org.projectlombok:lombok'
        developmentOnly 'org.springframework.boot:spring-boot-devtools'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testImplementation 'io.projectreactor:reactor-test'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }
    ```

- Ubicarse en el paquete co.com.microservice.jwt.application.helpers.util y crear la clase JwtUtil.java
    ```
    package co.com.microservice.jwt.application.helpers.util;

    import io.jsonwebtoken.JwtException;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.stereotype.Component;

    import java.security.Key;
    import java.time.Instant;
    import java.time.temporal.ChronoUnit;
    import java.util.Base64;
    import java.util.Date;

    @Component
    public class JwtUtil {

        public boolean isTokenValid(String token) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (JwtException e) {
                return false;
            }
        }

        public String getUsername(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        public String generateToken(String subject){
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                    .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        private Key getSigningKey() {
            String base64Key = "6YqJRPjVG+UZ5ihx5sXs8z0z4bW0rK9KURl2rOaF0Vs=";
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.jwt.application.security.config y crear la clase SecurityConfig.java
    ```
    package co.com.microservice.jwt.application.security.config;

    import co.com.microservice.jwt.application.helpers.util.JwtUtil;
    import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpHeaders;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
    import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
    import org.springframework.security.config.web.server.ServerHttpSecurity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.ReactiveSecurityContextHolder;
    import org.springframework.security.web.server.SecurityWebFilterChain;
    import org.springframework.web.server.WebFilter;

    import java.util.List;

    @Configuration
    @EnableWebFluxSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final JwtUtil jwtUtil;
        private final RouterProperties properties;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers(createPublicPath(),
                                properties.getPathBase() + "health",
                                properties.getPathBase() + "liveness",
                                properties.getPathBase() + "readiness",
                                properties.getPathBase() + "metrics"
                            ).permitAll()
                            .anyExchange().authenticated()
                    )
                    .addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                    .build();
        }

        @Bean
        public WebFilter jwtFilter() {
            return (exchange, chain) -> {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtUtil.isTokenValid(token)) {
                        String username = jwtUtil.getUsername(token);
                        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    }
                }
                return chain.filter(exchange);
            };
        }

        private String createPublicPath() {
            return properties.getPathBase() + properties.getPathPublic() + "/**";
        }
    }
    ```

| **Sección**                                | **Descripción**                                                                                  |
| ------------------------------------------ | ------------------------------------------------------------------------------------------------ |
| `@Configuration`                           | Indica que esta clase define beans de configuración para Spring.                                 |
| `@EnableWebFluxSecurity`                   | Habilita la seguridad para aplicaciones reactivas usando Spring WebFlux.                         |
| `@RequiredArgsConstructor`                 | Genera automáticamente un constructor con los campos `final` (`jwtUtil` y `properties`).         |
| `JwtUtil jwtUtil`                          | Componente para validar y extraer información del token JWT.                                     |
| `RouterProperties properties`              | Clase de propiedades que contiene los paths públicos configurables.                              |
| `securityWebFilterChain(...)`              | Método que configura el filtro de seguridad WebFlux:                                             |
|   • `.csrf(...::disable)`                  | Desactiva CSRF ya que normalmente no se usa en APIs REST.                                        |
|   • `.authorizeExchange(...)`              | Configura rutas que no requieren autenticación (`permitAll()`) y las que sí (`authenticated()`). |
|   • `.addFilterAt(jwtFilter(), ...)`       | Agrega un filtro personalizado para procesar JWT antes del filtro de autenticación.              |
| `jwtFilter()`                              | Define un filtro Web que intercepta cada request entrante:                                       |
|   • `authHeader = getFirst(AUTHORIZATION)` | Obtiene el header `Authorization`.                                                               |
|   • `startsWith("Bearer ")`                | Verifica si es un token tipo Bearer.                                                             |
|   • `jwtUtil.isTokenValid(token)`          | Valida el token JWT.                                                                             |
|   • `jwtUtil.getUsername(token)`           | Extrae el `username` del token.                                                                  |
|   • `UsernamePasswordAuthenticationToken`  | Crea una instancia de autenticación sin credenciales ni roles.                                   |
|   • `contextWrite(...)`                    | Inserta el contexto de seguridad reactivo con el usuario autenticado.                            |
| `createPublicPath()`                       | Concatena propiedades para generar el path público: `basePath + publicPath + "/**"`.             |

- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.handler y crear la clase SecurityHandler.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.handler;

    import co.com.microservice.jwt.application.helpers.logs.LoggerBuilder;
    import co.com.microservice.jwt.application.helpers.util.JwtUtil;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Mono;

    import java.util.Map;

    @Component
    @RequiredArgsConstructor
    public class SecurityHandler {
        private static final String NAME_CLASS = SecurityHandler.class.getName();
        private static final String MESSAGE_ID = "message-id";
        private static final String USER_NAME = "user-name";
        private final LoggerBuilder logger;
        private final JwtUtil jwtUtil;

        public Mono<ServerResponse> generateToken(ServerRequest serverRequest) {
            var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
            printOnProcess(headers.get(MESSAGE_ID), "get token");

            return ServerResponse.ok().bodyValue(
                Map.of("token", jwtUtil.generateToken(headers.get(USER_NAME))));
        }

        private void printOnProcess(String messageId, String messageService){
            logger.info("Security token", messageId, messageService, NAME_CLASS);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.router y crear la clase SecurityRouterRest.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.router;

    import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
    import co.com.microservice.jwt.infrastructure.input.rest.api.handler.SecurityHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.RouterFunctions;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class SecurityRouterRest {
        private final RouterProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerSecurityFunction(SecurityHandler securityHandler) {
            return RouterFunctions.route()
                    .GET(createRoute(properties.getToken()), securityHandler::generateToken)
                    .build();
        }

        private String createRoute(String route){
            return properties.getPathBase()
                    .concat(properties.getPathPublic())
                    .concat(route);
        }
    }
    ```

- Ubicarse en el paquete co.com.microservice.jwt.infrastructure.input.rest.api.handler y modificar la clase UserHandler.java
    ```
    package co.com.microservice.jwt.infrastructure.input.rest.api.router;

    import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
    import co.com.microservice.jwt.infrastructure.input.rest.api.handler.UserHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.server.RouterFunction;
    import org.springframework.web.reactive.function.server.RouterFunctions;
    import org.springframework.web.reactive.function.server.ServerResponse;

    @Configuration
    @RequiredArgsConstructor
    public class UserRouterRest {
        private final RouterProperties properties;

        @Bean
        public RouterFunction<ServerResponse> routerUserFunction(UserHandler userHandler) {
            return RouterFunctions.route()
                .GET(createRoutePublic(properties.getPathInfo()), userHandler::info)
                .GET(createRoutePrivate(
                    properties.getPathUser().concat(properties.getValidateSecurity())), userHandler::validate)
                .build();
        }

        private String createRoutePublic(String route){
            return properties.getPathBase()
                    .concat(properties.getPathPublic())
                    .concat(route);
        }

        private String createRoutePrivate(String route){
            return properties.getPathBase()
                    .concat(properties.getPathPrivate())
                    .concat(route);
        }
    }
    ```

# <div id='id3'/>
# 4. Pruebas de validación

- Curls para hacer pruebas
    - Url pública
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/private/user/validate' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'
    ```

    - Url privada: Genera error si no se envía token o si es invalido
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/private/user/validate' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'
    ```

    ![](./img/modules/security/3-get_private_no_token.png)

    - Url pública: Obtener token: expira luego de 15 minutos
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/public/token' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman'

    -- Resultado:
    {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VydGVzdCIsImlhdCI6MTc1MzU1MTk5NSwiZXhwIjoxNzUzNTUyODk1fQ.n9F7RdVqClPsAJazaB5PhASyW2RQ-5tHEv2BV0cy7ic"
    }
    ```

    - Url privada: Con token valido
    ```
    curl --location 'localhost:8080/api/v1/microservice-jwt/private/user/validate' \
    --header 'user-name: usertest' \
    --header 'message-id: 7a214936-5e93-11ec-bf63-0242ac130002' \
    --header 'ip: 172.34.45.12' \
    --header 'user-agent: application/json' \
    --header 'platform-type: postman' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VydGVzdCIsImlhdCI6MTc1MzU1MTk5NSwiZXhwIjoxNzUzNTUyODk1fQ.n9F7RdVqClPsAJazaB5PhASyW2RQ-5tHEv2BV0cy7ic'

    -- Resultado:
    Hi validate token, usertest!
    ```
--- 

[< Volver al índice](README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)
