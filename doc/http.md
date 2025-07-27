# Comprender conceptos del protocolo HTTP
> A continuación se explica qué es http, https, sus características principales.


### Indice

* [1. HTTP](#id1)
* [2. HTPS](#id2)
* [3. REST y WebSockets](#id3)

# <div id='id1'/>
# 1. 📘 ¿Qué es HTTP?  

HTTP (HyperText Transfer Protocol) es un protocolo de comunicación de nivel de aplicación utilizado para la transferencia de información entre clientes (como navegadores) y servidores web. Es el fundamento de cualquier intercambio de datos en la Web.

## Características de HTTP

| Característica             | Descripción                                                                                     |
| -------------------------- | ----------------------------------------------------------------------------------------------- |
| **Sin estado (stateless)** | Cada petición es independiente; el servidor no guarda información del cliente entre peticiones. |
| **Basado en texto**        | Las solicitudes y respuestas están en formato texto legible.                                    |
| **Extensible**             | Permite cabeceras personalizadas.                                                               |
| **Flexible**               | Se adapta para diferentes tipos de contenido (JSON, HTML, XML, etc).                            |
| **Cliente-servidor**       | La arquitectura es clara: el cliente inicia solicitudes, el servidor responde.                  |

## 🔁 Métodos HTTP

Los métodos HTTP indican la intención de la solicitud. A continuación una descripción de los principales:

| Método      | Descripción                                                                                              |
| ----------- | -------------------------------------------------------------------------------------------------------- |
| **GET**     | Recupera información del servidor. No debe modificar el recurso. Idempotente.                            |
| **POST**    | Envía datos al servidor para crear un nuevo recurso. No idempotente.                                     |
| **PUT**     | Reemplaza por completo un recurso existente. Es idempotente (varias solicitudes tienen el mismo efecto). |
| **PATCH**   | Modifica parcialmente un recurso existente.                                                              |
| **DELETE**  | Elimina un recurso del servidor. También idempotente.                                                    |
| **OPTIONS** | Informa qué métodos están disponibles para un recurso. Útil para preflight requests en CORS.             |
| **HEAD**    | Similar a GET, pero sólo recupera los encabezados. No el cuerpo.                                         |

💡 idempotencia: Realizar la misma operación varias veces tiene el mismo efecto que hacerla una sola vez.


## 📌 Otras Consideraciones Relevantes

| Tema                   | Descripción                                                                                                                                  |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| **Status Codes**       | HTTP define códigos de estado como `200 OK`, `404 Not Found`, `500 Internal Server Error`, que informan el resultado de la petición.         |
| **Headers**            | HTTP utiliza encabezados (headers) para transportar metadatos como tipo de contenido (`Content-Type`), autenticación (`Authorization`), etc. |
| **CORS**               | Controla el acceso entre dominios diferentes. Usa cabeceras como `Access-Control-Allow-Origin`.                                              |
| **Cookies y Sesiones** | Aunque HTTP es sin estado, se pueden usar cookies para mantener el estado entre peticiones.                                                  |
| **HTTP/2 y HTTP/3**    | Nuevas versiones del protocolo que mejoran rendimiento, multiplexación y seguridad.                                                          |

## 📌 Consideraciones adicionales sobre HTTP

| Elemento            | Límite típico (aproximado)              | Comentarios                                                                        |
| ------------------- | --------------------------------------- | ---------------------------------------------------------------------------------- |
| **URL**             | \~2,000 caracteres (por compatibilidad) | Navegadores como IE tienen límite en 2,083 caracteres. Mejor mantener URLs cortas. |
| **Headers**         | \~8 KB en la mayoría de servidores      | Algunos servidores pueden configurarlo (ej. `client_header_buffer_size` en Nginx). |
| **Body (POST/PUT)** | Depende del servidor (varía mucho)      | En servidores modernos, puede ser desde 2MB hasta 2GB, pero configurable.          |

⚠️ Importante: Aunque HTTP como protocolo no impone estos límites, los servidores, proxies y navegadores sí lo hacen.

## 📤 Codificación del body

- Puedes enviar el cuerpo en distintos formatos:
    - application/json
    - application/xml
    - multipart/form-data
    - text/plain
    - etc

- Siempre se debe indicar el tipo con el header Content-Type.

## 🧩 Encabezados comunes

| Header          | Descripción                                                         |
| --------------- | ------------------------------------------------------------------- |
| `Content-Type`  | Tipo de contenido del cuerpo (`application/json`)                   |
| `Accept`        | Qué tipo de respuesta espera el cliente                             |
| `Authorization` | Encabezado para tokens Bearer o Basic Auth                          |
| `Cache-Control` | Controla el almacenamiento en caché                                 |
| `User-Agent`    | Información del cliente que hace la petición (navegador, app, etc.) |
| `Host`          | Indica a qué host se dirige la solicitud                            |

## 🧷 Seguridad

- No se debe enviar información sensible en la URL (GET), ya que se puede almacenar en logs o historiales.

- Usar siempre HTTPS para evitar que datos viajen en texto plano.

- Validar y sanear los datos enviados por el usuario para prevenir ataques como inyección o XSS.

## 🔁 Reintentos y errores

- GET es seguro para reintentar, pero POST puede duplicar efectos (como crear múltiples registros).

- Los códigos HTTP ayudan a manejar errores:

| Código | Descripción           |
| ------ | --------------------- |
| `200`  | OK                    |
| `201`  | Created               |
| `400`  | Bad Request           |
| `401`  | Unauthorized          |
| `403`  | Forbidden             |
| `404`  | Not Found             |
| `500`  | Internal Server Error |
| `503`  | Service Unavailable   |

## 🧠 Buenas prácticas para APIs HTTP

- Usar verbos HTTP correctamente (GET para leer, POST para crear, etc.).
- Incluir versiones en la URL o en headers: /api/v1/productos
- Responder con códigos de estado adecuados.
- No sobrecargar los headers con datos innecesarios.
- Documentar claramente los endpoints (OpenAPI / Swagger).

# <div id='id2'/>
# 2. 🔐 ¿Qué es HTTPS?

HTTPS (HTTP Secure) es la versión segura de HTTP. Utiliza TLS (Transport Layer Security) o su antecesor SSL para cifrar los datos intercambiados.

| Característica          | Descripción                                           |
| ----------------------- | ----------------------------------------------------- |
| **Cifrado**             | Protege los datos durante la transmisión.             |
| **Autenticación**       | Asegura que estás comunicándote con el servidor real. |
| **Integridad de datos** | Previene alteraciones o manipulaciones en los datos.  |
| **Puerto por defecto**  | 443 (en lugar del 80 de HTTP).                        |


# <div id='id3'/>
# 3. 🌐 HTTP como base de REST y WebSockets

HTTP es el protocolo base sobre el cual funcionan tanto REST como el handshake de WebSockets. A partir de ahí, siguen caminos diferentes.

## 📘 REST (Representational State Transfer)

## ✅ ¿Qué es REST?
REST es un estilo arquitectónico para diseñar servicios web, que usa HTTP como medio de transporte.

## 🔗 ¿Cómo se relaciona con HTTP?
REST usa los métodos HTTP (GET, POST, PUT, DELETE) para operar sobre recursos.

Usa URLs para identificar recursos (/usuarios/123).

El estado del recurso se representa usualmente en JSON.

## 🧩 Características REST:

| Concepto       | Ejemplo                                                        |
| -------------- | -------------------------------------------------------------- |
| Recursos       | `/productos`, `/usuarios/123`                                  |
| Verbos HTTP    | `GET`, `POST`, `PUT`, `DELETE`                                 |
| Stateless      | Cada petición contiene toda la información necesaria           |
| Representación | Se usa JSON o XML para representar datos                       |
| Cacheable      | Puede usar headers HTTP para controlar caché (`Cache-Control`) |

## 🔁 WebSockets

## ✅ ¿Qué es WebSockets?
WebSockets es un protocolo que permite comunicación bidireccional en tiempo real entre cliente y servidor, sobre una conexión persistente.

## 🔗 ¿Cómo se relaciona con HTTP?
WebSocket inicia con un handshake HTTP en un puerto como 80 o 443:

GET /chat HTTP/1.1
Host: servidor.com
Upgrade: websocket
Connection: Upgrade

- Si el servidor acepta, la conexión se "actualiza" y deja de usar HTTP, comenzando una sesión WebSocket.

## ⚙️ Características de WebSocket:

| Característica       | Descripción                                                  |
| -------------------- | ------------------------------------------------------------ |
| Conexión persistente | No se crea una conexión nueva por cada mensaje.              |
| Bidireccional        | Cliente y servidor pueden enviar datos en cualquier momento. |
| Tiempo real          | Ideal para chats, notificaciones, dashboards, videojuegos.   |
| Bajo overhead        | Reduce la sobrecarga de cabeceras en cada mensaje.           |

## 🥊 REST vs WebSocket (Comparación)

| Característica                | REST                                | WebSockets                            |
| ----------------------------- | ----------------------------------- | ------------------------------------- |
| **Transporte**                | HTTP                                | HTTP (solo para handshake), luego TCP |
| **Dirección**                 | Unidireccional (cliente → servidor) | Bidireccional (cliente ↔ servidor)    |
| **Estado de conexión**        | Sin estado (stateless)              | Conexión persistente                  |
| **Eficiencia en tiempo real** | Baja                                | Alta                                  |
| **Complejidad**               | Baja                                | Media/Alta                            |



[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)
