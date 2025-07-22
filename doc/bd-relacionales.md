# Bases de Datos Relacionales
> En este proyecto se utilizan bases de datos relacionales como PostgreSQL y MySQL para el almacenamiento persistente de entidades del dominio. Ambas tecnologías (Postgresql y Mysql) permiten estructurar y consultar información utilizando el lenguaje SQL, garantizando integridad, consistencia y confiabilidad en la gestión de datos.

### Indice

* [1. ¿Qué es una base de datos relacional?](#id1)
* [2. Postgresql](#id2)
* [3. Mysql](#id3)
* [4. Comparación](#id4)

# <div id='id1'/>
# 1. ¿Qué es una base de datos relacional?
Una base de datos relacional (RDBMS) organiza los datos en tablas relacionadas entre sí mediante claves primarias y foráneas. Estas bases de datos son ideales para representar entidades del mundo real, mantener integridad referencial y ejecutar consultas complejas con lenguaje estructurado (SQL).

## Características principales de las bases de datos relacionales
- 📋 Estructura tabular: los datos se almacenan en filas y columnas.
- 🔑 Llaves primarias y foráneas: permiten relaciones entre entidades.
- 📐 Esquemas rígidos: definen la estructura de cada tabla (tipos de datos, restricciones, etc.).
- 🧾 Soporte para transacciones: garantizan operaciones atómicas y consistentes (ACID).
- 📊 Lenguaje SQL: consultas estructuradas para manipular y recuperar datos.
- 🔒 Seguridad: control de acceso, roles, autenticación, etc.

## ¿Qué significa ACID?
| Letra | Propiedad        | Descripción                                                                                                            |
| ----- | ---------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **A** | **Atomicidad**   | Una transacción es **todo o nada**. Si una parte de la transacción falla, ninguna operación se aplica.                 |
| **C** | **Consistencia** | La base de datos debe pasar de un estado válido a otro. Las reglas y restricciones del esquema siempre se respetan.    |
| **I** | **Aislamiento**  | Las transacciones concurrentes no deben interferir entre sí. Cada una se ejecuta como si fuera la única en el sistema. |
| **D** | **Durabilidad**  | Una vez confirmada, la transacción **persiste permanentemente** en la base de datos, incluso ante fallos del sistema.  |

# <div id='id2'/>
# 2. Postgresql
PostgreSQL es una base de datos relacional de código abierto altamente robusta, orientada a objetos y compatible con estándares SQL.

**Ventajas clave:**
- Soporte nativo para tipos de datos complejos (JSONB, arrays, UUID, etc.).
- Transacciones ACID completas.
- Extensibilidad (soporte para funciones definidas por el usuario, procedimientos almacenados).
- Integración perfecta con R2DBC para aplicaciones reactivas.

## Seguridad y configuración
PostgreSQL está diseñado con un fuerte enfoque en la seguridad y la personalización avanzada.

- Autenticación mediante pg_hba.conf: PostgreSQL controla quién puede conectarse, desde dónde y con qué métodos (como md5, scram-sha-256, peer, cert, etc.).
- Cifrado en tránsito: Soporte completo para conexiones SSL/TLS entre el cliente y el servidor.
- Control de acceso detallado: Permite definir privilegios específicos por base de datos, tabla, columna o función.
- Roles y grupos: Soporta la creación de usuarios con diferentes niveles de acceso agrupados por roles.
- Extensiones de seguridad: Permite el uso de módulos externos como pgaudit para trazabilidad.

## Configuración destacada
- Archivos principales: postgresql.conf (configuración general) y pg_hba.conf (acceso).
- Parámetros ajustables como work_mem, max_connections, shared_buffers, etc.
- Soporte para configuración dinámica y reinicio parcial (reload) sin apagar el servicio.
- Registro de logs detallado configurable.

# <div id='id3'/>
# 3. Mysql
MySQL es un sistema de base de datos ampliamente adoptado en la industria, conocido por su rendimiento, simplicidad y soporte comunitario.

**Ventajas clave:**
- Rendimiento sólido en operaciones de lectura/escritura.
- Alta compatibilidad con múltiples frameworks Java.
- Buen soporte en ambientes cloud y contenedores.
- Ideal para aplicaciones que requieren baja latencia y consultas estructuradas.

## Seguridad y configuración
MySQL también proporciona mecanismos sólidos de seguridad, aunque algunos deben configurarse explícitamente.

- Autenticación de usuarios: Mediante credenciales y mecanismos como mysql_native_password o caching_sha2_password.
- Cifrado SSL: Permite habilitar conexiones seguras con certificados y claves privadas.
- Gestión de privilegios: Utiliza el sistema GRANT para definir qué usuarios pueden ejecutar qué operaciones y en qué contexto.
- Cifrado de datos en reposo: Disponible desde MySQL 5.7+ (opcional).
- Actualizaciones de seguridad frecuentes: especialmente en versiones administradas por proveedores cloud (como Aurora o Cloud SQL).

## Configuración destacada
- Archivo principal: my.cnf o my.ini, dependiendo del sistema operativo.
- Parámetros comunes: innodb_buffer_pool_size, max_connections, query_cache_size, etc.
- Soporte para logs de errores, logs generales y logs de consultas lentas.
- Configuración granular del motor de almacenamiento (InnoDB).

# <div id='id4'/>
# 4 Comparación práctica segun programación reactiva
| Característica       | PostgreSQL                  | MySQL                              |
| -------------------- | --------------------------- | ---------------------------------- |
| Rol en el proyecto   | Principal (dominio)         | Secundario (validaciones externas) |
| Integración reactiva | R2DBC (`spring-data-r2dbc`) | R2DBC (`mysql-r2dbc` driver)       |
| Tipos avanzados      | JSONB, ARRAY, UUID          | Menos flexible                     |
| Uso de transacciones | Completo                    | Completo                           |

## Comparativo nubes a (21-julio-2025)

| Base de Datos | Nube  | Tamaño Aprox.              | Costo Mensual Estimado |
| ------------- | ----- | -------------------------- | ---------------------- |
| PostgreSQL    | AWS   | 1 vCPU, 1 GiB RAM, 20 GB   | \~\$15 USD             |
| PostgreSQL    | Azure | 1 vCPU, 2 GiB RAM, 20 GB   | \~\$18 USD             |
| PostgreSQL    | GCP   | 1 vCPU, 0.6 GiB RAM, 20 GB | \~\$11 USD             |
| MySQL         | AWS   | 1 vCPU, 1 GiB RAM, 20 GB   | \~\$13 USD             |
| MySQL         | Azure | 1 vCPU, 2 GiB RAM, 20 GB   | \~\$15 USD             |
| MySQL         | GCP   | 1 vCPU, 0.6 GiB RAM, 20 GB | \~\$11 USD             |


