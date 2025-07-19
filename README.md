# Creación microservicios Java + Webflux + AWS
> A continuación se indican diferentes instructivos que te guiaran para ejecutar el paso a paso y crear una aplicación basada en la nube, creando microservicios con las tecnologias podman, Java, Webflux, usando un plungin gradle para creación de proyectos basados en arquitecturas limpias con uso de servicios de AWS como dynamodb, S3, Redis cache, RabbitMQ, KMS, Secrets Manager, entre otros.

# **Índice**

* [1. Documentación](#id1)
  * [1.1 Podman + localstack](#id1-1)
  * [1.2 Servicios AWS](#id1-2)
    * [1.2.1 DynamoDB](#id1-2-1)
    * [1.2.2 SecretManager](#id1-2-2)
    * [1.2.3 Redis cache](#id1-2-3)
    * [1.2.4 Almacenamiento S3 bucket](#id1-2-4)
  * [1.3 Arquitectura limpia](#id1-3)
  * [1.4 Spring Webflux](#id1-4)
  * [1.5 Servicios utilidades Docker](#id1-5)
    * [1.5.1 WireMock (Api rest)](#id1-5-1)
* [2. Proyecto Java Webflux (Caso práctico con spring initializr)](#id2)
  * [2.1 Proyecto Java-Reactivo Stack tecnológico](#id2-1)

# <div id='id1'/>
# 1 Documentación

# <div id='id1-1'/>
## 1.1 Podman + localstack

### ¿Qué es podman?

> Podman es una herramienta de línea de comandos para gestionar contenedores y pod similares a los de Docker, pero con algunas diferencias clave; A diferencia de Docker, Podman no necesita un servicio en segundo plano (daemon). Los comandos se ejecutan directamente en el sistema, lo que mejora la seguridad y reduce el uso de recursos; Podman usa los mismos comandos que Docker (podman run, podman build, etc.), y puede usar imágenes de Docker Hub u otros registros de contenedores; Podman es parte del proyecto libpod desarrollado por Red Hat, y está disponible bajo una licencia de código abierto.

### ¿Qué es localstack AWS?

> Es una herramienta que emula servicios de AWS en la máquina local. Es muy útil para desarrollo y pruebas sin necesidad de conectarte realmente a la nube de AWS ni generar costos; ayuda a desarrollar microservicios en entornos controlados sin tener que subirlos a AWS

### Ejecutar primeros pasos

[>> Primeros pasos Podman + Localstack AWS](1-1-podman-localstack-aws.md)

# <div id='id1-2'/>
## 1.2 Servicios AWS

# <div id='id1-2-1'/>
### 1.2.1 DynamoDB

### ¿Qué es Amazon DynamoDB?

Amazon **DynamoDB** es un servicio de base de datos **NoSQL** totalmente gestionado por AWS, diseñado para aplicaciones que requieren rendimiento rápido, alta disponibilidad y escalabilidad automática.

### Ejecutar primeros pasos

[>> Primeros pasos DynamoDB](1-2-1-dynamodb.md)

# <div id='id1-2-2'/>
### 1.2.2 Secret Manager

### ¿Qué es Amazon Secrets Manager?

AWS Secrets Manager es un servicio de Amazon Web Services que te permite almacenar, administrar y recuperar secretos de forma segura. Un "secreto" puede ser cualquier tipo de información sensible, como: Contraseñas de bases de datos, Claves API, Credenciales para servicios externos, Certificados, Tokens de autenticación

### Ejecutar primeros pasos

[>> Primeros pasos Secret Manager](1-2-2-secret-manager.md)

# <div id='id1-2-3'/>
### 1.2.3 Redis cache

### ¿AWS Cache?

Los servicios de caché están pensados para mejorar el rendimiento y reducir la latencia de tus aplicaciones, especialmente cuando acceden frecuentemente a datos que no cambian con frecuencia.

### Ejecutar primeros pasos

[>> Primeros pasos Secret Manager](1-2-3-redis-cache.md)

# <div id='id1-2-4'/>
### 1.2.4 Almacenamiento S3 Bucket (Simple Storage Service)

### ¿Qué es Amazon S3 (Simple Storage Service)?

Amazon S3 (Simple Storage Service) es un servicio de almacenamiento en la nube proporcionado por AWS (Amazon Web Services), diseñado para almacenar y recuperar cualquier cantidad de datos en cualquier momento y desde cualquier lugar en la web.

### ¿Qué es un S3 Bucket?

Un bucket en Amazon S3 es un contenedor lógico donde se almacenan objetos (archivos). Puedes pensar en un bucket como una carpeta raíz en la que subes tus archivos, aunque internamente S3 no funciona exactamente como un sistema de archivos tradicional.

### Ejecutar primeros pasos

[>> Primeros pasos Secret Manager](1-2-4-almacenamiento-s3.md)

[](README-SERVICIO-RABBITMQ.md)
[](README-KAFKA.md)
[](README-KUBERNETES.md)

# <div id='id1-3'/>
## 1.3 Arquitectura limpia

[>> Ver documentación (externo)](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# <div id='id1-4'/>
## 1.4 Spring Webflux

## ¿Qué es Webflux?
> Spring WebFlux es un módulo del ecosistema Spring Framework diseñado para construir aplicaciones web reactivas, es decir, aplicaciones que manejan peticiones de forma asíncrona y no bloqueante.

### Ejecutar primeros pasos

[>> Primeros pasos spring webflux)](1-3-spring-webflux.md)

# <div id='id1-5'/>
## 1.5 Mocks api rest

## ¿Qué es WireMock?

> WireMock es una herramienta de simulación de APIs (mock server) que permite emular servicios HTTP externos para pruebas.  Te permite simular respuestas de servicios REST sin necesidad de que estén realmente disponibles.  Ideal para pruebas unitarias, de integración o para trabajar desconectado del backend real.

### Ejecutar primeros pasos

[>> Primeros pasos spring webflux)](1-5-1-wiremock-apirest.md)

# <div id='id3'/>
# <div id='id3-1'/>
# 3. Proyecto Java Webflux (Caso práctico con spring initializr)
## 3.1 Proyecto Java-Reactivo Stack tecnológico

Este proyecto tiene como objetivo el desarrollo de un sistema backend reactivo, robusto y escalable basado en Spring WebFlux, que proporciona una API REST para realizar operaciones CRUD (crear, consultar, actualizar y eliminar) sobre diversas entidades almacenadas en bases de datos relacionales. El enfoque principal es ofrecer un diseño limpio, orientado a eventos, con una arquitectura moderna y desacoplada.

### Características Principales

- **API RESTful con Spring WebFlux**  
Implementación de endpoints reactivos para la gestión de recursos.

  Control de flujo no bloqueante basado en Mono y Flux.

  Separación clara entre respuestas técnicas (errores de infraestructura, validaciones, etc.) y respuestas de negocio (reglas funcionales del dominio).

- **Bases de Datos Relacionales**  
  PostgreSQL: Base de datos principal usada para el almacenamiento de entidades del dominio.

  MySQL: Base de datos secundaria utilizada en módulos específicos que requieren interoperabilidad o sincronización de datos.

- **Cache con Redis**  
  Integración de Redis Cache para mejorar el rendimiento mediante almacenamiento temporal de datos consultados frecuentemente.

  Configuración reactiva y uso eficiente con Spring Data Redis Reactive.

- **Gestión de Credenciales con AWS Secrets Manager**  
  Acceso seguro a credenciales de bases de datos y otros servicios a través de Secrets Manager.

  Desacoplamiento de las configuraciones sensibles del código fuente.

- **Mensajería Asíncrona con RabbitMQ**  
  Envío y recepción de eventos de dominio a través de RabbitMQ, usando la librería async-commons-rabbit-starter.

  Configuración de colas, intercambios y binding keys definidos en archivos YAML para facilitar la mantenibilidad y el versionamiento.

- **Arquitectura Hexagonal**  
  Separación clara entre la lógica de negocio (dominio) y las dependencias externas (bases de datos, colas, controladores HTTP).

  Uso de puertos y adaptadores para mantener bajo acoplamiento y facilitar las pruebas unitarias.

- **Sistema de Logging Estructurado**  
  Implementación de logs enriquecidos mediante un LoggerBuilder personalizado.

  Impresión detallada de headers, requests, responses y metadatos.

  Formato en JSON compatible con herramientas de monitoreo como ELK (Elasticsearch, Logstash, Kibana).

- **Estrategia de Respuestas Unificadas**  
  Respuestas técnicas con códigos HTTP precisos y mensajes claros.

  Respuestas de negocio estandarizadas, con códigos funcionales y descripciones comprensibles para el consumidor.

<a href="3-1-crear-api-rest-crud-postgresql.md" style="background-color:#4CAF50;color:white;padding:10px 20px;text-align:center;text-decoration:none;display:inline-block;border-radius:5px;font-weight:bold;">
📘 Ver Instructivo del Proyecto Java-Reactivo
</a>

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

## ¿Te ha sido útil este contenido?

Puedes apoyar este proyecto con una donación:

[☕ Invítame un café](https://www.buymeacoffee.com/tuusuario)  
[💸 Haz una donación vía PayPal](https://paypal.me/tuusuario)

> Gracias por respetar la licencia y reconocer el trabajo compartido.

> Gracias por apoyar el software libre 🙌