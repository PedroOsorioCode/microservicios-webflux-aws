# Montaje ambiente local Redis cache
> A continuación se explica qué es Redis Cache, sus características principales y el paso a paso necesario para montar un contenedor local con este servicio. Esta guía está orientada a entornos de desarrollo y pruebas, y también incluye comandos complementarios usando AWS CLI en caso de integración con servicios administrados como ElastiCache.

### Requisitos: 

- ⚠️ Haber leido [Primeros pasos ambiente local](./podman-localstack-aws.md)

## ¿Qué es Redis cache?

Redis (Remote Dictionary Server) es una base de datos en memoria de estructura clave-valor, de código abierto y alto rendimiento, utilizada comúnmente como caché, cola de mensajes, almacenamiento temporal y base de datos NoSQL. Redis es especialmente útil para aplicaciones que requieren respuestas rápidas, almacenamiento temporal de datos o desacoplamiento de servicios.

## 🚀 Características principales

- **Almacenamiento en memoria con persistencia opcional**  
    Redis almacena todos sus datos en la memoria RAM, lo que permite acceder a ellos con gran rapidez (latencias por debajo de 1 ms). Sin embargo, también puede configurarse para guardar esos datos en disco mediante dos modos de persistencia:

    RDB (Redis Database Backup): realiza snapshots periódicos del estado de Redis.

    AOF (Append Only File): registra cada operación de escritura en un archivo de log, permitiendo una recuperación más precisa.

- **Soporte para estructuras de datos avanzadas**  
    Redis no solo almacena cadenas de texto. Soporta múltiples estructuras de datos que lo hacen muy flexible:

    **Strings:** valores de texto o binarios.

    **Lists:** listas enlazadas, útiles para colas.

    **Sets:** colecciones de valores únicos, sin orden.

    **Sorted Sets:** sets con orden definido por un puntaje (score).

    **Hashes:** estructuras tipo diccionario (clave-valor anidadas).

    **Streams, HyperLogLogs, Bitmaps, Geo:** estructuras para casos más avanzados.

- **Operaciones atómicas y transacciones**  
    Cada comando en Redis es atómico, lo que significa que se ejecuta completamente sin interferencia de otras operaciones. Además, se pueden agrupar varios comandos dentro de una transacción usando MULTI, EXEC, DISCARD, y WATCH.

- **Sistema de publicación y suscripción (Pub/Sub)**  
    Redis permite que clientes se suscriban a canales para recibir mensajes en tiempo real. Es ideal para aplicaciones de notificaciones, chats o sistemas de eventos distribuidos.

- **Alta disponibilidad y replicación**  
    Redis soporta replicación maestro-esclavo, permitiendo que una instancia principal replique sus datos a múltiples réplicas. Además, con Redis Sentinel se puede detectar fallos y hacer failover automático. En entornos más grandes, Redis también soporta Cluster Mode, permitiendo la fragmentación de los datos (sharding) entre múltiples nodos.

- **Patrones de uso de caché** 
    Redis es ampliamente usado como capa de caché. Algunos patrones comunes:

    **Cache Aside:** la aplicación consulta primero el caché, y si no hay datos, los recupera de la base de datos y los almacena en Redis.

    **Write-Through:** los datos se escriben primero en Redis y luego en la base de datos.

    **Write-Behind:** los datos se escriben en Redis y luego, en segundo plano, se persisten en la base de datos.

- **Escalabilidad y rendimiento**  
    Redis puede manejar cientos de miles de operaciones por segundo con una sola instancia. Gracias al clustering y la replicación, puede escalar horizontalmente para manejar grandes volúmenes de tráfico sin afectar la latencia.

- **Compatibilidad con servicios cloud (ej: Amazon ElastiCache)**  
    Redis está disponible como servicio administrado en la nube, lo que elimina la necesidad de gestionar manualmente la infraestructura. Amazon ElastiCache, por ejemplo, ofrece Redis con monitoreo, backups automáticos, actualizaciones y alta disponibilidad integradas.

- **¿Cómo usa Redis el almacenamiento en memoria RAM?**  
    Redis es una base de datos in-memory, lo que significa que todos los datos se almacenan y manipulan directamente en la RAM del servidor (no en disco como las bases de datos tradicionales). Esto permite tiempos de acceso extremadamente rápidos (sub-milisegundo), ideal para:

    - Cachés temporales
    - Almacenamiento de sesiones
    - Ranking o conteos en tiempo real
    - Procesamiento de colas y mensajes

    Redis guarda todos sus datos en memoria, y si se desea, puede hacer persistencia en disco mediante snapshots (RDB) o logs (AOF), pero siempre trabaja con datos cargados en RAM.

- 🛠️ **¿Es autogestionado por AWS en ElastiCache?**  
Sí. Cuando usas Amazon ElastiCache for Redis, AWS se encarga de gestionar automáticamente:

    - Asignación de memoria RAM adecuada al tipo de nodo que elijas.
    - Monitoreo del uso de memoria y métricas de rendimiento.
    - Evicción automática si excedes el límite de memoria, según la política de caché que configures (por ejemplo: LRU – Least Recently Used).
    - Persistencia opcional, si habilitas backups o snapshots automáticos.
    - Escalabilidad horizontal, mediante clústeres y particionamiento (sharding).
    - Alta disponibilidad, mediante réplicas y Redis Sentinel.

    En resumen: tú decides cuánto quieres provisionar, pero AWS se encarga de administrar el hardware y software necesarios para que Redis funcione correctamente.

## 🧮 Costos estimados de Redis Cache (ElastiCache)  

| Tipo de nodo      | Memoria (GiB) | Costo por hora (USD) | Costo mensual (USD) aprox. | Ideal para                  |
| ----------------- | ------------- | -------------------- | -------------------------- | --------------------------- |
| `cache.t4g.micro` | 0.5           | \$0.0067             | \~\$4.99                   | Desarrollo, pruebas         |
| `cache.t4g.small` | 1.0           | \$0.0134             | \~\$10.00                  | Caché pequeño en producción |
| `cache.t3.medium` | 2.05          | \$0.0326             | \~\$24.30                  | Apps ligeras en producción  |
| `cache.m6g.large` | 6.38          | \$0.072              | \~\$54.00                  | Producción con alta demanda |

## Montaje local con Podman  

- Comandos  

    ```
    -- Descargar la imagen
    podman run -d --name redis-container -p 6379:6379 docker.io/library/redis:latest

    -- Listar los contenedores en ejecución
    podman ps

    -- Elegimos el nombre que le hemos dado al contendor, en este caso: redis-container

    -- Ingresar al CLI del contenedor
    podman exec -it redis-container redis-cli

    -- Guardar, obtener, eliminar, ver todas las claves
    SET KEY VALUE
    GET KEY
    DEL KEY
    KEYS *
    ```

## Recomendaciones de buenas prácticas  

- Utiliza TTL (tiempo de vida) para evitar saturación de memoria.

- Monitorea el uso de memoria y el número de claves almacenadas.

- No almacenes datos sensibles sin cifrado.

- Considera usar Redis Sentinel o Clustering para producción.  

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

[< Volver al índice](../README.md)

---

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)