
### Indice

* [1. Doc Webclient](#id1)
* [2. Doc Wiremock](#id2)

# <div id='id1'/>
# 1. Consumo de apis externas
> En esta sección se explica qué es WebClient, sus principales características, cómo utilizarlo para consumir servicios externos de manera reactiva, así como las recomendaciones para realizar pruebas usando WireMock como herramienta de simulación.

## ¿Qué es WebClient?

**WebClient** es el cliente HTTP no bloqueante incluido en el módulo spring-webflux. Está diseñado para trabajar con flujos reactivos (Mono y Flux), permitiendo consumir servicios REST de forma eficiente, sin bloquear los hilos del sistema.

Es el reemplazo moderno de RestTemplate y se integra naturalmente con aplicaciones reactivas basadas en Project Reactor.

## Características principales

- Comunicación no bloqueante y reactiva.
- Soporte para múltiples métodos HTTP: GET, POST, PUT, DELETE, etc.
- Manejo de encabezados, parámetros y cuerpo de la solicitud de forma fluida.
- Configuración de timeouts, reintentos con espera personalizada y manejo de errores HTTP.
- Integración directa con Spring Boot y soporte para filtros, interceptores y loggers personalizados.
- Compatible con pruebas simuladas mediante herramientas como WireMock.

## Protocolos de comunicación compatibles
WebClient permite realizar llamadas a servicios que operen sobre los siguientes protocolos:

- HTTP / HTTPS: Principal protocolo utilizado para consumir APIs RESTful.
- WebSocket (en casos específicos): a través de una extensión con WebSocketClient.
- No es compatible directamente con gRPC, pero existen adaptadores y clientes externos para este caso.

# <div id='id2'/>
# 2. Montaje ambiente local Wiremock
> A continuación se explica que es wiremock, características e indicaciones del paso a paso que se debe realizar para crear mocks

### Requisitos: 

⚠️ Debes haber realizado el instructivo de [Primeros pasos ambiente local](podman-localstack-aws.md)

## ¿Qué es WireMock?

WireMock es una herramienta de simulación de APIs (mock server) que permite emular servicios HTTP externos para pruebas.  Te permite simular respuestas de servicios REST sin necesidad de que estén realmente disponibles.  Ideal para pruebas unitarias, de integración o para trabajar desconectado del backend real.

## Principales características de WireMock:

| Característica                                 | Descripción                                                                                                            |
| ---------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **Simulación de endpoints HTTP**            | Puedes definir stubs para emular respuestas HTTP (GET, POST, PUT, etc.) con status, headers y cuerpos personalizados.  |
| **Respuestas dinámicas**                    | Puede responder con datos basados en parámetros de la petición, headers o query params.                                |
| **Standalone o embebido**                   | Se puede correr como un servidor independiente o incluir como dependencia en tests de Java (JUnit, Spring Boot, etc.). |
| **Definición de stubs por JSON o API REST** | Puedes definir los mocks usando archivos `.json` o enviarlos vía HTTP con su API administrativa.                       |
| **Ver historial de peticiones**            | WireMock registra todas las peticiones recibidas, lo que te permite verificar si se llamó correctamente.               |
| **Simulación de latencia y fallos**          | Puedes simular timeouts, errores 500, respuestas lentas, etc. para probar comportamientos frente a fallos.             |
| **Reproducción de escenarios**              | Puedes configurar respuestas secuenciales (por ejemplo: primero 200, luego 500, luego timeout).                        |
| **Soporte para HTTPS**                      | Puedes simular servicios que responden sobre TLS/SSL.                                                                  |
| **Soporte para Docker**                     | Es muy fácil correr WireMock como contenedor para pruebas locales o CI/CD.                                             |

## ¿Cuándo usar WireMock?

- Cuando quieres probar un microservicio sin depender de otros.
- Cuando el backend aún no existe, pero tú ya puedes empezar a desarrollar o probar.
- Para automatizar pruebas con respuestas controladas.
- Para simular comportamientos de red o fallos (como reintentos o timeouts).

## Ejemplos de uso

- Instalar la imagen e iniciar el contenedor
    ```
    podman run -d --name mock-server -p 8089:8080 docker.io/wiremock/wiremock
    ```

- Subir el contenedor si la imagen ya fue descargada
    ```
    podman start mock-server
    ```

- Curls para respuesta status 200, return json
    ```
    curl -X POST http://localhost:8089/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
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

- Curls para respuestas secuenciales (500, timeout, 200), útil para probar reintentos
    ```
    curl -X POST http://localhost:8089/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
        "request": {
        "method": "GET",
        "url": "/api/v3/microservice-param/auditOnSave"
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
    curl -X POST http://localhost:8089/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
        "request": {
        "method": "GET",
        "url": "/api/v3/microservice-param/auditOnSave"
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
    curl -X POST http://localhost:8089/__admin/mappings \
    -H "Content-Type: application/json" \
    -d '{
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
        },
        "scenarioName": "AuditParamRetry",
        "requiredScenarioState": "ThirdAttempt",
        "newScenarioState": "Completed"
    }'

    {
        "instant": {
            "epochSecond": 1753149252,
            "nanoOfSecond": 502441200
        },
        "thread": "RMessageSender1-1",
        "level": "INFO",
        "loggerName": "co.com.microservice.aws.application.helpers.logs.LoggerBuilder",
        "message": "{\"app\":{\"message\":\"Event emitted\",\"messageId\":\"9999999-9999-0001\",\"service\":\"generateDomainEvent\",\"method\":\"co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository.EventOperations\",\"appName\":\"MicroserviceAws\"},\"request\":{\"headers\":null,\"body\":{\"type\":\"myapp.notification.example-event-emited\",\"specVersion\":\"1\",\"source\":\"microservice-aws\",\"id\":\"2ea60812-025f-486d-9162-843d56421b5d\",\"time\":\"2025-07-21T20:54:12.477500100\",\"invoker\":\"From-My-App\",\"dataContentType\":\"application/json\",\"data\":{\"data\":{\"id\":null,\"shortCode\":\"USA\",\"name\":\"Estados Unidos\",\"description\":\"Cuenta con una población estimada de 300 millones de habitantes.\",\"status\":true,\"dateCreation\":null},\"headers\":{\"user-name\":\"usertest\",\"platform-type\":\"postman\",\"ip\":\"172.34.45.12\",\"id\":\"9999999-9999-0001\",\"user-agent\":\"application/json\"}},\"eventId\":\"2ea60812-025f-486d-9162-843d56421b5d-myapp.notification.example-event-emited\"}},\"response\":null}",
        "endOfBatch": false,
        "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
        "threadId": 131,
        "threadPriority": 5
    }

    Response postman
    Saved successfull!
    ```

    **Importante**: Cuando se ejecuten los escenarios se debe reiniciar su estado o sino dará error 404

    ```
    curl --location --request POST 'http://localhost:3000/__admin/scenarios/reset'
    ```

    **¿Qué hace esto?**

    1er intento: responde 500 Internal Server Error.

    2do intento: simula un timeout tardando 10 segundos.

    3er intento: responde 200 OK con el parámetro.

    reset: reinicia los escenarios

    Esto es perfecto para probar reintentos reactivos (.retryWhen) en WebClient.

- Curls para obtener los curls creados
    ```
    curl --location 'http://localhost:8089/__admin/mappings'
    ```

- Curls para eliminar por el UUID
    ```
    curl -X DELETE http://localhost:8089/__admin/mappings/2ec5a4d8-09fd-45a6-bf55-ece2cf09b715
    ```

---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](../PRINCIPAL.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)