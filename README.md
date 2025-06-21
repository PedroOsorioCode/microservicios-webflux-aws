# Creación microservicios Java + Webflux + AWS
> A continuación se indican diferentes instructivos que te guiaran para ejecutar el paso a paso y crear una aplicación basada en la nube, creando microservicios con las tecnologias podman, Java, Webflux, usando un plungin gradle para creación de proyectos basados en arquitecturas limpias con uso de servicios de AWS como dynamodb, S3, Redis cache, RabbitMQ, KMS, Secrets Manager, entre otros.

# **Indice**

* [1. Documentación](#id1)
  * [1.1 Podman + localstack](#id1-1)
  * [1.2 Servicios AWS](#id1-2)
    * [1.2.1 DynamoDB](#id1-2-1)
    * [1.2.2 SecretManager](#id1-2-2)
    * [1.2.3 S3 bucket](#id1-2-1)
  * [1.3 Arquitectura limpia](#id1-3)
  * [1.4 Webflux](#id1-4)
* [2. Proyecto Java Webflux](#id2)
  * [2.1 Proyecto base](#id2-1)
  * [2.2 API Rest webflux](#id2-2)

# <div id='id1'/>
# 1 documentación

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

[>> Primeros pasos Podman + Localstack AWS](1-1-podman-localstack-aws.md)

	
### Instructivo montaje local con Podman y servicios AWS
1. [Ver instructivo](README-GUIA-AMBIENTE-LOCAL.md)
### Instructivo crear proyecto Java Webflux
1. [Ver instructivo](README-PROYECTO-JAVA-WEBFLUX.md)

---

**Author**: Pedro Luis Osorio Pavas [Linkedin](www.linkedin.com/in/pedro-luis-osorio-pavas-68b3a7106)  
**Start Date**: 01-06-2025  
**Update Date**: 01-06-2025.