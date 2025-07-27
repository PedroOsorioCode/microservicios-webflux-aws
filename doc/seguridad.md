# Comprender conceptos se aseguramiento de apis
> A continuación se explica qué es JWT, sus características principales e implementación

### Indice

* [1. Protocolos de Seguridad más usados en 2025](#id1)
* [2. Seguridad apis con Tokens](#id2)
* [3. Tokens](#id3)
* [4. JWT (JSON Web Token)](#id4)

# <div id='id1'/>
# 1. Protocolos de Seguridad más usados en 2025

| Protocolo             | Uso Principal                       | Observaciones                                             |
| --------------------- | ----------------------------------- | --------------------------------------------------------- |
| **HTTPS (TLS 1.3)**   | Encriptación de capa de transporte  | Indispensable. Cifra todo el tráfico.                     |
| **OAuth 2.1**         | Delegación de acceso (con tokens)   | Recomendado para APIs y aplicaciones modernas.            |
| **OpenID Connect**    | Autenticación basada en OAuth2      | Extensión para identificación de usuarios.                |
| **Mutual TLS (mTLS)** | Autenticación de cliente y servidor | Requiere certificados en ambos extremos. Uso empresarial. |

💡En 2025, el uso de OAuth 2.1 + OpenID Connect con JWT y HTTPS (TLS 1.3) es considerado el estándar moderno más seguro.

# <div id='id2'/>
# 2. ¿Por qué es importante asegurar las APIs con tokens?
Las APIs modernas exponen funcionalidades y datos críticos a través de internet. Si no están adecuadamente protegidas, son susceptibles a:

- Robo de información (por ejemplo, datos personales).
- Uso indebido de funcionalidades (por ejemplo, ejecución de transacciones).
- Ataques como man-in-the-middle, replay attacks, o injection.

# <div id='id3'/>
# 3. ¿Qué es un Token?
Un token es una cadena de caracteres (generalmente opaca o codificada) que representa de forma segura información sobre un usuario o un sistema. Se utiliza principalmente para:

- Autenticar usuarios.
- Autorizar el acceso a recursos.
- Mantener un contexto de seguridad sin necesidad de manejar sesiones tradicionales en el servidor.

## ¿Por qué usar tokens?

- **Desacopla la autenticación del backend:** el backend no necesita manejar sesiones.
- **Escalable:** ideal para arquitecturas distribuidas y microservicios.
- **Stateless:** la autenticación se maneja con cada solicitud, sin almacenar estado del usuario en el servidor.
- **Interoperable:** puede ser utilizado desde móviles, frontends JS (SPA), servicios, etc.

## 🔑 Tipos de Tokens
- JWT (JSON Web Token)
    - El más común.
    - Contiene información estructurada.
    - Usado con OAuth 2.0 y OpenID Connect.

- Opaque Token
    - No se puede leer sin consultar al servidor de autorización.
    - Menos información expuesta, pero requiere validación remota.

- PASETO (Platform-Agnostic Security Tokens)
    - Alternativa moderna a JWT, más segura por diseño.
    - No permite algoritmos inseguros (como none).
    - Usa cifrado simétrico o asimétrico, pero con decisiones más seguras predefinidas.

- SAML Tokens
    - XML en lugar de JSON.
    - Muy usado en autenticación corporativa (SSO).
    - Pesado y poco común en APIs modernas REST.

- Refresh Tokens
    - No son tokens de acceso directo.
    - Se utilizan para obtener nuevos tokens de acceso cuando el actual expira.

## 🔒 Codificación de la clave del token: Base64

Cuando usamos JWT (o PASETO, etc.), muchas veces la clave secreta del servidor se codifica en Base64 para:

- Convertir una cadena binaria a texto ASCII.
- Facilitar su almacenamiento en archivos .env, variables de entorno, etc.

⚠️ Importante: Base64 no es cifrado ni hashing, simplemente codifica datos para que puedan ser transmitidos fácilmente. Cualquiera puede decodificarlo.

# <div id='id4'/>
# 4. 🧾 ¿Qué es un JWT (JSON Web Token)?

Un JWT es un tipo específico de token que contiene información estructurada en formato JSON, y está firmado digitalmente para garantizar su integridad y autenticidad.

- Acrónimo de JSON Web Token.
- Definido en la RFC 7519.
- Comúnmente utilizado con OAuth 2.0, OpenID Connect, y APIs REST modernas.

## ✳️ Características de un JWT

| Característica           | Descripción                                                                   |
| ------------------------ | ----------------------------------------------------------------------------- |
| **Autocontenido**        | Contiene toda la información relevante (no requiere consultas al servidor).   |
| **Stateless**            | No requiere mantener sesiones en el backend. Ideal para APIs.                 |
| **Firmado**              | Verificable con clave secreta o pública. Garantiza que no ha sido modificado. |
| **Codificado en Base64** | Puede ser leído por humanos (pero no encriptado).                             |
| **Expirable**            | Tiene campos como `exp` para definir vencimiento.                             |
| **Transporte seguro**    | Debe enviarse sobre HTTPS para proteger su confidencialidad.                  |

## 📦 Estructura de un JWT

Un JWT está compuesto por tres partes separadas por puntos (.):

xxxxx.yyyyy.zzzzz  
Header.Payload.Signature

### 🔷 Header (Encabezado)  
Define el tipo de token y el algoritmo de firma.
```
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### 🟨 Payload (Cuerpo)
Contiene las claims (información sobre el usuario o permisos).
```
{
  "sub": "1234567890",
  "name": "Jhon Smith",
  "iat": 1621234567,
  "exp": 1621238167,
  "role": "admin"
}
```

- sub: Identificador del sujeto.
- iat: Fecha de emisión (issued at).
- exp: Fecha de expiración.
- role: Rol del usuario, por ejemplo.

### 🔐 Signature (Firma)
Garantiza que el token no ha sido alterado.

### 👉 Ejemplo:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJ1c2VyIjoiUGVkcm8iLCJyb2xlIjoiYWRtaW4ifQ.
xJpP9iUvlJ9wZjBhq8_xGnJQlmUYYZlRGPBtRfDApvc
```
- Primera parte (**Header**): contiene información del algoritmo (alg) y tipo (typ).
- Segunda parte (**Payload**): contiene información del usuario (user, role).
- Ambas se pueden leer directamente al decodificar Base64URL.
- Tercera parte (**Signature**): La firma garantiza que el contenido del token no ha sido modificado desde que fue generado por el servidor.

### 🔍 ¿Qué pasa cuando alguien recibe el token?

- El receptor (por ejemplo, otro backend o middleware) vuelve a calcular la firma con el mismo header.payload y su clave secreta.
- Luego compara la firma calculada con la que viene en el token:
- Si coinciden, significa que el token es auténtico y no ha sido modificado.
- Si no coinciden, el token fue alterado o es falso.

## 🛑 ¿Qué pasa si un atacante roba un JWT?

- 📌 Escenario:
    - Un atacante logra robar un token JWT desde:
    - El almacenamiento local del navegador (como localStorage o sessionStorage).
    - A través de una vulnerabilidad XSS (cross-site scripting).
     - Capturando tráfico no cifrado (HTTP sin TLS).

- 📌 ¿Qué puede hacer con ese token?
    - Puede suplantar al usuario legítimo.
    - Realizar operaciones maliciosas con sus permisos.
    - Acceder a datos privados desde otras ubicaciones (si el token no tiene restricciones).

## 🛡️ ¿Cómo se previenen estos ataques?

- ✅ 1. Usar HTTPS (TLS) siempre
    - Nunca enviar un token por HTTP.
    - TLS cifra la comunicación y evita robo en tránsito (man-in-the-middle).

- ✅ 2. Evitar guardar el token en localStorage o sessionStorage
Esos espacios pueden ser accedidos por scripts maliciosos en caso de una vulnerabilidad XSS.

    - Mejor opción: usar cookies con las siguientes características:
        - HttpOnly: evita que JavaScript acceda al token.
        - Secure: solo se envía por HTTPS.
        - SameSite=Strict o Lax: limita su envío en solicitudes externas.

- ✅ 3. Usar expiración corta en los tokens (exp)
    - Reduce el tiempo de exposición si un token es robado.
    - Complementar con refresh tokens seguros si se necesita mantener la sesión más tiempo.

- ✅ 4. Verificación de IP, User-Agent, ubicación
Guardar metadatos del token (IP, navegador) y verificar que coincidan en cada request.

    - Si hay un cambio sospechoso, invalidar el token.

- ✅ 5. Revocación de tokens / lista negra
Aunque los JWT no se almacenan en servidor (stateless), puedes mantener una blacklist de tokens comprometidos.

    - También se puede manejar con una versión de sesión, y si cambia, se invalida el token.

- ✅ 6. Implementar mecanismos de detección
    - Detectar actividad anómala:
    - Múltiples accesos desde diferentes regiones.
    - Frecuencia inusual de peticiones.
    - Cambios de IP o dispositivo.

⚠️ Aunque JWT es seguro en su diseño, la seguridad depende totalmente de cómo lo implementes. Un token robado no se puede "detener" desde su contenido, porque es válido. Por eso, las medidas de protección externa (como expiración, verificación de contexto, almacenamiento seguro) son esenciales.

## ✅ Datos que sí puedes guardar en un JWT (no sensibles)

| Campo       | Descripción                                                                 |
| ----------- | --------------------------------------------------------------------------- |
| `sub`       | ID del usuario (no debe ser un número de cédula ni email completo)          |
| `username`  | Nombre de usuario público (no contraseña ni correo completo)                |
| `role`      | Rol o tipo de usuario (`admin`, `user`, `viewer`, etc.)                     |
| `scope`     | Permisos o ámbitos autorizados (`read`, `write`, etc.)                      |
| `iat`       | Fecha de emisión del token (Issued At, timestamp)                           |
| `exp`       | Fecha de expiración (timestamp)                                             |
| `aud`       | Audiencia: para quién está destinado el token (ej. nombre de la app)        |
| `iss`       | Emisor: quién generó el token (ej. `auth.myapp.com`)                        |
| `jti`       | ID único del token (útil para revocación o trazabilidad)                    |
| `sessionId` | ID de sesión en backend (si se cruza con Redis, sin exponer contenido real) |
| `lang`      | Preferencia de idioma del usuario                                           |
| `device`    | Tipo de dispositivo: `web`, `mobile`, etc.                                  |
| `tenantId`  | En apps multicliente, ID de la empresa o cliente asociado                   |
| `features`  | Lista de funcionalidades activadas (`["darkMode", "betaUI"]`)               |

## ⚠️ Datos que NO debes guardar en un JWT sin cifrar

| ❌ Información sensible                    | ❌ ¿Por qué NO?                                       |
| ----------------------------------------- | ---------------------------------------------------- |
| Contraseñas                               | Obvio: puede ser leída fácilmente.                   |
| Email completo / teléfono                 | Facilita ataques dirigidos (phishing, suplantación). |
| Dirección IP o ubicación exacta           | Información privada, sensible y trazable.            |
| Tokens de terceros (ej: Google, Facebook) | Si los roban, se pueden reutilizar.                  |
| Información bancaria, tarjetas            | No va nunca en un JWT plano.                         |
| Historial de transacciones                | Datos personales y potencialmente legales.           |
| Documentos identificativos                | Cédula, pasaporte, etc.                              |

- ✅ Buenas prácticas aplicadas

    - 🔓 Rutas públicas: no requieren token (ej: /auth/login, /health, /docs, etc.).
    - 🔒 Rutas privadas: requieren JWT firmado y validado en cada petición.
    - 🧾 Tokens JWT: firmados (HS256 o RS256), con datos no sensibles y fechas de expiración.
    - 🚫 Validación de firma y expiración: siempre activa.
    - 🧠 Posibilidad de revocación: mediante jti + Redis si lo implementas.
    - 🛡️ Uso de HTTPS: obligatorio para evitar sniffing del token.
    - 🍪 Cookies seguras (opcional): con HttpOnly y Secure.
    - 📤 Header Authorization: estándar para enviar el token.

---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – SEGURIDAD DEL API JWT](../SECURITY.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)
