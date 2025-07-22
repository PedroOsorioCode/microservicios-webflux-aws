# Creación Proyecto Java-Reactivo Stack tecnológico
> Este proyecto tiene como objetivo el desarrollo de un sistema backend reactivo, robusto y escalable basado en Spring WebFlux, que proporciona una API REST para realizar operaciones CRUD (crear, consultar, actualizar y eliminar) sobre diversas entidades almacenadas en bases de datos relacionales. El enfoque principal es ofrecer un diseño limpio, orientado a eventos, con una arquitectura moderna y desacoplada.

### Características Principales

- **API RESTful con Spring WebFlux**  
Implementación de endpoints reactivos para la gestión de recursos.

  Control de flujo no bloqueante basado en Mono y Flux.

  Separación clara entre respuestas técnicas (errores de infraestructura, validaciones, etc.) y respuestas de negocio (reglas funcionales del dominio).

  🔗 👉 [📘 Primeros pasos con Spring WebFlux](./doc/spring-webflux.md)

- **Bases de Datos Relacionales**  
  PostgreSQL: Base de datos principal usada para el almacenamiento de entidades del dominio.

  MySQL: Base de datos secundaria utilizada en módulos específicos que requieren interoperabilidad o sincronización de datos.

  🔗 👉 [📘 Primeros pasos bd relacionales](./doc/bd-relacionales.md)

- **Bases de Datos No Relacionales**  
  Utilizadas para almacenar información que no requiere esquemas rígidos, relaciones complejas o que necesita alta disponibilidad, baja latencia y escalabilidad horizontal.

  Son ideales para almacenamiento de eventos, cache, documentos, claves-valor o datos semiestructurados.

  🔗 👉 [📘 Primeros pasos bd No relacionales](./doc/bd-no-relacionales.md)

- **Simulación de servicios AWS con Podman + Localstack**  
  Uso de Podman como motor de contenedores liviano y compatible con Docker para orquestar servicios locales.
  
  Integración con Localstack para simular servicios de AWS como S3, DynamoDB, Secrets Manager, entre otros, permitiendo desarrollar y probar localmente sin necesidad de una cuenta real en AWS.

  Esta configuración facilita un entorno de desarrollo portátil, reproducible y libre de costos en la nube, manteniendo la compatibilidad con herramientas y SDKs oficiales de Amazon.

  🔗 👉 [📘 Primeros pasos con Podman + Localstack AWS](./doc/podman-localstack-aws.md)

- **Gestión de Credenciales con AWS Secrets Manager**  
  Acceso seguro a credenciales de bases de datos y otros servicios a través de Secrets Manager.

  Desacoplamiento de las configuraciones sensibles del código fuente.

  🔗 👉 [📘 Primeros pasos con Secret Manager](./doc/secret-manager.md)

- **Cache con Redis**  
  Integración de Redis Cache para mejorar el rendimiento mediante almacenamiento temporal de datos consultados frecuentemente.

  Configuración reactiva y uso eficiente con Spring Data Redis Reactive.

  🔗 👉 [📘 Primeros pasos con Redis Cache](./doc/redis-cache.md)

- **Mensajería Asíncrona con RabbitMQ**  
  Envío y recepción de eventos de dominio a través de RabbitMQ, usando la librería async-commons-rabbit-starter.

  Configuración de colas, intercambios y binding keys definidos en archivos YAML para facilitar la mantenibilidad y el versionamiento.

  🔗 👉 [📘 Primeros pasos con RabbitMQ](./doc/rabbitmq.md)

- **Consumo de servicio externos con WebClient**  
  Integración de servicios externos mediante WebClient, el cliente HTTP no bloqueante de Spring WebFlux.
  Se implementa un enfoque reactivo para realizar llamadas remotas, permitiendo controlar eficientemente los recursos del sistema.

  El consumo incluye una estrategia de reintentos automáticos con esperas configurables entre cada intento, lo que mejora la resiliencia ante errores temporales (como timeouts o fallas de red).
  Además, se manejan adecuadamente los errores HTTP para diferenciarlos entre fallas técnicas y de negocio, asegurando trazabilidad en logs y respuestas consistentes.
  
  🔗 👉 [📘 Primeros pasos con Webclient](./doc/webclient-wiremock-apirest.md)

- **Arquitectura Hexagonal**  
  Separación clara entre la lógica de negocio (dominio) y las dependencias externas (bases de datos, colas, controladores HTTP).

  Uso de puertos y adaptadores para mantener bajo acoplamiento y facilitar las pruebas unitarias.

  <a href="https://medium.com/@diego.coder/introducci%C3%B3n-a-las-clean-architectures-723fe9fe17fa">
  📘 Ver documentación externa sobre Clean Architecture (Medium - Bancolombia Tech)
  </a>

- **Sistema de Logging Estructurado**  
  Implementación de logs enriquecidos mediante un LoggerBuilder personalizado.

  Impresión detallada de headers, requests, responses y metadatos.

  Formato en JSON compatible con herramientas de monitoreo como ELK (Elasticsearch, Logstash, Kibana).

- **Estrategia de Respuestas Unificadas**  
  Respuestas técnicas con códigos HTTP precisos y mensajes claros.

  Respuestas de negocio estandarizadas, con códigos funcionales y descripciones comprensibles para el consumidor.

---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](PRINCIPAL.md)

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

---
## ¿Te ha sido útil este contenido?

Puedes apoyar este proyecto con una donación:

☕  
**[Invítame un café]** → Transferencia Nequí: **311-715-9402** → desde **$1 USD**  
☕

---

📩 ¿Tienes dudas, sugerencias o deseas contactarme?  
Puedes escribirme a: **pedro.osoriopavas.ibm@gmail.com**

---

- Gracias por respetar la licencia y reconocer el trabajo compartido.  
- Gracias por apoyar el software libre 🙌