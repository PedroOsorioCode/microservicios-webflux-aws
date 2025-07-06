# Instructivo paso a paso montaje ambiente local Wiremock
> A continuación se explica que es wiremock, características e indicaciones del paso a paso que se debe realizar para crear mocks

### Requisitos: 

⚠️ Debes haber realizado el instructivo de [Primeros pasos ambiente local](README-AMBIENTE-LOCAL.md)

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
    ```

    **¿Qué hace esto?**

    1er intento: responde 500 Internal Server Error.

    2do intento: simula un timeout tardando 10 segundos.

    3er intento: responde 200 OK con el parámetro.

    Esto es perfecto para probar reintentos reactivos (.retryWhen) en WebClient.

- Curls para obtener los curls creados
    ```
    curl --location 'http://localhost:8089/__admin/mappings'
    ```

- Curls para eliminar por el UUID
    ```
    curl -X DELETE http://localhost:8089/__admin/mappings/2ec5a4d8-09fd-45a6-bf55-ece2cf09b715
    ```

[< Volver al índice](README.md)

---

**Author**: Pedro Luis Osorio Pavas [Linkedin](https://www.linkedin.com/in/pedro-luis-osorio-pavas-68b3a7106)  
**Start Date**: 01-07-2025  
**Update Date**: 01-07-2025.

