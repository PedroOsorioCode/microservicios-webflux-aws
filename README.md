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
* [2. Proyecto Java Webflux (Caso práctico con plugin scaffold)](#id2)
  * [2.1 Proyecto base](#id2-1)
  * [2.2 API Rest webflux GET](#id2-2)
  * [2.3 API Rest webflux CRUD dynamoDB](#id2-3)
    * [2.3.1 API Rest webflux informar errores](#id2-3-1)
  * [2.4 Load variables y rest consumer](#id2-4)
  * [2.5 Secrets manager y redis cache](#id2-5)
  * [2.6 Microservio exponer parámetros](#id2-6)
  * [2.7 Enviar eventos RabbitMQ](#id2-7)
  * [2.8 Microservicio leer eventos RabbitMQ](#id2-8)
* [3. Proyecto Java Webflux (Caso práctico con spring initializr)](#id3)
  * [3.1 API Rest webflux CRUD Postgre SQL](#id3-1)

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

# <div id='id2'/>
# 2. Proyecto Java Webflux (Caso práctico con plugin scaffold)

# <div id='id2-1'/>
## 2.1 Proyecto base

### Ejecutar primeros pasos

[>> Primeros pasos crear proyecto)](2-1-crear-proyecto-base.md)

# <div id='id2-2'/>
## 2.2 API Rest Java Spring Webflux GET

### Ejecutar primeros pasos

Se indica paso a paso como crear un api rest y las diferentes formas de crearlo

[>> Crear proyecto api rest](2-2-crear-api-rest.md)

# <div id='id2-3'/>
## 2.3 API Rest webflux CRUD dynamoDB

### Ejecutar primeros pasos

Se indica paso a paso como crear un api rest para crear, actualizar, borrar y consultar información en una tabla en dynamodb

[>> Crear proyecto api rest CRUD DynamoDB](2-3-crear-api-rest-crud-dynamodb.md)

# <div id='id2-3-1'/>
### 2.3.1 API Rest webflux informar errores

### Ejecutar primeros pasos

Se indica paso a paso como configurar el proyecto para retornar errores técnicos o de negocio personalizados, evitando exponer la estructura del proyecto

[>> Crear proyecto api rest Informar errores técnicos y negocio](2-3-1-crear-api-rest-informar-errores.md)

# <div id='id2-4'/>
## 2.4 Load variables locales y consumo api rest con reintentos

Se indica paso a paso como configurar el proyecto para obtener variables locales, por consumos de servicios externos con reintentos en casos de fallo y servicios mockeados

[>> Crear proyecto load variables locales y consumo de servicios externos](2-4-crear-load-variables-rest-consumer.md)

# <div id='id2-5'/>
## 2.5 Almacenar parámetros en redis cache, uso de secretos

Se indica paso a paso como configurar el proyecto para guardar un parámetro en redis cache y configuración de la conexión con secrets-manager

[>> Crear proyecto secrets-manager y redis cache](2-5-crear-secrest-manager-redis-cache.md)


# <div id='id3'/>
# 3. Proyecto Java Webflux (Caso práctico con spring initializr)

# <div id='id3-1'/>
## 3.1 API Rest webflux CRUD PostgreSQL

### Ejecutar primeros pasos

Se indica paso a paso como crear un api rest para crear, actualizar, borrar y consultar información en una tabla en postgresql

[>> Crear proyecto api rest CRUD PostgreSQL](3-1-crear-api-rest-crud-postgresql.md)

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

## ¿Te ha sido útil este contenido?

Puedes apoyar este proyecto con una donación:

[☕ Invítame un café](https://www.buymeacoffee.com/tuusuario)  
[💸 Haz una donación vía PayPal](https://paypal.me/tuusuario)

> Gracias por respetar la licencia y reconocer el trabajo compartido.

> Gracias por apoyar el software libre 🙌