
### Indice

* [1. BD no relacionales](#id1)
* [2. Tecnologías NoSQL destacadas](#id2)
* [3. Ambiente Local Dynamo DB](#id3)

# <div id='id1'/>
# 1. Bases de Datos No Relacionales
> A diferencia de las bases de datos relacionales, las bases de datos no relacionales (NoSQL) permiten almacenar información sin necesidad de un esquema fijo, ofreciendo flexibilidad en el modelado de datos, alto rendimiento y facilidad de escalabilidad horizontal. Son ampliamente utilizadas en arquitecturas modernas que requieren respuestas rápidas y alta disponibilidad.

## Características principales

- Esquema flexible: No es necesario definir una estructura fija para los datos.
- Escalabilidad horizontal: Permiten crecer distribuyendo los datos entre múltiples nodos.
- Altamente disponibles: Diseñadas para tolerar fallos y mantener disponibilidad.
- Optimización para tipos de acceso específicos: Algunos motores están diseñados para lectura rápida, otros para escritura masiva.
- Alto rendimiento: Especialmente útiles para datos en tiempo real, caché o grandes volúmenes de información.

## Tecnologías comunes NoSQL

| Tipo            | Ejemplos                | Uso típico                           |
| --------------- | ----------------------- | ------------------------------------ |
| **Clave-Valor** | Redis, Amazon DynamoDB  | Cache, sesiones, configuraciones     |
| **Documentos**  | MongoDB, Couchbase      | Datos semiestructurados (JSON)       |
| **Columnas**    | Apache Cassandra, HBase | Analítica, grandes volúmenes         |
| **Grafos**      | Neo4j, Amazon Neptune   | Relaciones complejas, redes sociales |

## Optimización por tipo de acceso

| Tipo de acceso                        | Tecnologías destacadas                | Descripción breve                                                                                      |
| ------------------------------------- | ------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| **Lectura rápida**                    | **Redis**, MongoDB (con índices)      | Diseñados para responder en milisegundos, ideales para cache, sesiones, respuestas inmediatas.         |
| **Escritura masiva**                  | **Apache Cassandra**, Amazon DynamoDB | Soportan altos volúmenes de escritura distribuida con baja latencia, ideales para eventos, logs o IoT. |
| **Acceso mixto equilibrado**          | **MongoDB**, Couchbase                | Buen rendimiento tanto en lecturas como escrituras con gran flexibilidad.                              |
| **Consultas de relaciones complejas** | **Neo4j**, Amazon Neptune             | Optimizados para búsquedas profundas y relaciones (por ejemplo: redes sociales, rutas, permisos).      |


# <div id='id2'/>
# 2. Tecnologías NoSQL destacadas

- 🔑 Clave-Valor
    - Redis: Motor en memoria extremadamente rápido, ideal para cache, sesiones, contadores y operaciones atómicas.

        ### Ejemplo
        ```
        SET user:123:name "Pedro"
        GET user:123:name
        ```

    - Amazon DynamoDB: Base de datos clave-valor totalmente gestionada por AWS, con alta disponibilidad, escalabilidad automática y baja latencia.

        ### Ejemplo
        ```
        {
            "userId": "123",
            "timestamp": 1721020800000,
            "activity": "LOGIN"
        }
        ```

- 📄 Documentos
    - MongoDB: Almacena datos en formato JSON/BSON. Muy flexible, ideal para aplicaciones con estructuras de datos variables o semiestructuradas.

        ### Ejemplo
        ```
        {
            "_id": "user123",
            "name": "Pedro",
            "email": "pedro@example.com",
            "roles": ["admin", "user"]
        }
        ```

    - Couchbase: Base de datos distribuida orientada a documentos, con capacidades de cache, búsquedas y sincronización offline.

        ### Ejemplo
        ```
        {
            "type": "product",
            "id": "abc123",
            "name": "Laptop",
            "price": 999.99,
            "inventory": 14
        }
        ```

- 🧱 Columnas
    - Apache Cassandra: Diseñada para grandes volúmenes de datos distribuidos, ofrece alta disponibilidad sin un único punto de fallo.

        ### Ejemplo
        ```
        CREATE TABLE user_events (
            user_id UUID,
            event_time timestamp,
            event_type text,
            PRIMARY KEY (user_id, event_time)
        );
        ```

    - HBase: Basada en Hadoop, ideal para aplicaciones analíticas que requieren almacenamiento en columnas a gran escala.

        ### Ejemplo
        ```
        Row Key: sensor_001#2025-07-19T18:00:00
        Column Family: metrics
            - temperature: 26.3
            - humidity: 62
            - battery: 93
        ```

- 🕸️ Grafos
    - Neo4j: Base de datos orientada a grafos, permite modelar y consultar relaciones complejas de forma eficiente.

        ### Ejemplo: Relaciones entre personas
        ```
        CREATE (a:Person {name: "Pedro"})
        CREATE (b:Person {name: "Laura"})
        CREATE (a)-[:FRIEND]->(b)
        ```

    - Amazon Neptune: Servicio gestionado para grafos compatible con los modelos Property Graph y RDF, optimizado para análisis de relaciones.

        ### Ejemplo
        ```
        <http://example.com/person/Pedro> 
        <http://xmlns.com/foaf/0.1/knows>
        <http://example.com/person/Laura> .
        ```


# <div id='id3'/>
# 3. Ambiente Local Dynamo DB

### Requisitos: 

- ⚠️ Haber leido [Primeros pasos ambiente local](./podman-localstack-aws.md)

## ¿Qué es Amazon DynamoDB?

Amazon **DynamoDB** es un servicio de base de datos **NoSQL** totalmente gestionado por AWS, diseñado para aplicaciones que requieren rendimiento rápido, alta disponibilidad y escalabilidad automática.

## Características principales

- **NoSQL**: Almacena datos en forma de tablas con una estructura flexible, permitiendo que cada ítem (registro) pueda tener diferentes atributos. No se requiere un esquema fijo.

- **Formato tipo JSON**: Aunque internamente DynamoDB no guarda literalmente archivos JSON, los datos se modelan de forma muy similar a un objeto JSON, usando estructuras clave-valor. Esto facilita su integración con aplicaciones modernas que ya trabajan con JSON.

- **Baja latencia**: Ofrece operaciones de lectura y escritura en milisegundos de un solo dígito, lo que significa que responde en menos de 10 ms, ideal para aplicaciones en tiempo real.

- **Escalabilidad automática**: Ajusta la capacidad de manera dinámica según la carga de trabajo, sin necesidad de aprovisionar recursos manualmente.

- **Replicación global**: Permite replicar datos en varias regiones con **Global Tables**, mejorando la disponibilidad y la cercanía de los datos para usuarios distribuidos globalmente.

- **Seguridad integrada**: Soporta cifrado de datos en reposo y en tránsito, control de acceso granular mediante IAM, y auditoría con CloudTrail.

- **Streams**: Captura los cambios realizados en los ítems de una tabla, permitiendo integrar eventos en tiempo real con otros servicios como AWS Lambda.

- **Time To Live (TTL)**: Posibilidad de eliminar automáticamente datos antiguos o expirados mediante una marca de tiempo definida por el usuario.

## Índices en DynamoDB

DynamoDB usa **índices** para permitir consultas eficientes sobre atributos diferentes de la clave principal.

- **Índice primario (Primary Key)**:
  - Puede ser una **clave de partición** sola, o una combinación de **clave de partición + clave de ordenamiento (sort key)**.
  - Determina cómo se almacenan y acceden los ítems.

- **Índice Secundario Global (GSI)**:
  - Permite consultar los datos usando diferentes atributos como clave, sin modificar el esquema principal de la tabla.
  - Puede incluir o proyectar solo ciertos atributos para mejorar el rendimiento.
  - Puedes añadir GSIs después de crear la tabla

- **Índice Secundario Local (LSI)**:
  - Comparte la misma clave de partición que la tabla principal, pero permite definir una clave de ordenamiento alternativa.
  - Debe definirse al crear la tabla, y usa el mismo HASH que la tabla, pero diferente RANGE.
  - Útil cuando quieres consultar por distintos criterios sobre los mismos ítems agrupados.

> Los índices secundarios no duplican todos los datos, sino los necesarios para las consultas que vas a hacer. Esto ayuda a mantener eficiencia y bajo costo.

| Tipo de índice             | Clave de partición           | Clave de ordenamiento       | ¿Se define al crear la tabla? | ¿Cantidad permitida por tabla? | ¿Se puede agregar después? |
|----------------------------|------------------------------|------------------------------|-------------------------------|-------------------------------|-----------------------------|
| Clave primaria             | ✅ Obligatoria                | ⚠️ Opcional                   | ✅ Sí                          | 1                             | ❌ No                       |
| GSI (Global Secondary Index) | ✅ Obligatoria              | ⚠️ Opcional                   | ❌ No                          | Hasta 20                      | ✅ Sí                       |
| LSI (Local Secondary Index)  | ✅ Igual que la tabla       | ✅ Obligatoria                | ✅ Sí                          | Hasta 5                       | ❌ No                       |


## Tipos de `ProjectionType` en índices de DynamoDB

| Valor de `ProjectionType` | ¿Qué incluye el índice?                                      | ¿Cuándo usarlo?                                                                 |
|---------------------------|---------------------------------------------------------------|----------------------------------------------------------------------------------|
| `ALL`                     | Todos los atributos del ítem                                  | Cuando necesitas acceder a todos los datos sin consultar la tabla principal     |
| `KEYS_ONLY`               | Solo claves primarias del ítem (HASH y RANGE, si existen)     | Para ahorrar almacenamiento si solo necesitas identificar ítems                 |
| `INCLUDE`                 | Claves + atributos específicos definidos en `NonKeyAttributes`| Cuando solo necesitas algunos campos adicionales específicos en el índice, ejemplo: `Projection={ProjectionType=INCLUDE, NonKeyAttributes=["clienteId", "monto"]}`|

## Modos de pago en DynamoDB

1. **PAY_PER_REQUEST**
   - También llamado "on-demand".
   - No necesitas definir capacidad de lectura o escritura.
   - Escala automáticamente.
   - Pagas solo por lo que consumes.
   - Ideal para tráfico variable o impredecible.

2. **PROVISIONED**
   - Defines manualmente la cantidad de capacidad de lectura (`Read Capacity Units`) y escritura (`Write Capacity Units`).
   - Más barato si el tráfico es constante y predecible.
   - Permite habilitar **auto-scaling** para ajustar capacidad automáticamente.
   - Necesario si quieres usar **reservas** de capacidad.

## Casos de uso comunes

- Aplicaciones móviles y web con alta concurrencia
- Juegos en línea en tiempo real
- Gestión de sesiones de usuario
- Comercio electrónico (carritos, productos, historial)
- Sistemas IoT que generan grandes volúmenes de eventos
- Aplicaciones que requieren datos distribuidos globalmente

## Comandos AWC CLI complementarios

| Operación               | Comando AWS CLI               |
|------------------------|------------------------------|
| Crear tabla            | `aws dynamodb create-table`   |
| Listar tablas          | `aws dynamodb list-tables`    |
| Describir tabla        | `aws dynamodb describe-table` |
| Eliminar tabla         | `aws dynamodb delete-table`   |
| Insertar ítem (Put)    | `aws dynamodb put-item`        |
| Obtener ítem (Get)     | `aws dynamodb get-item`        |
| Actualizar ítem        | `aws dynamodb update-item`     |
| Eliminar ítem          | `aws dynamodb delete-item`     |
| Escanear tabla         | `aws dynamodb scan`            |
| Consultar tabla (Query)| `aws dynamodb query`           |
| Actualizar tabla       | `aws dynamodb update-table`    |

## Comandos AWC CLI para crear tablas en DynamoDB
En esta oportunidad vamos a crear una tabla para almacenar información sobre tiquetes de vuelo

```    
aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name local-flight-tickets --attribute-definitions AttributeName=documentNumber,AttributeType=S AttributeName=ticket,AttributeType=S AttributeName=status,AttributeType=S --key-schema AttributeName=documentNumber,KeyType=HASH AttributeName=ticket,KeyType=RANGE --billing-mode PAY_PER_REQUEST --global-secondary-indexes "IndexName=statusIndex,KeySchema=[{AttributeName=status,KeyType=HASH}],Projection={ProjectionType=ALL}" --region us-east-1
```

| Parte del comando                                               | Explicación                                                                                  |
|----------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| `aws`                                                          | Ejecuta la AWS CLI (herramienta de línea de comandos oficial de AWS).                       |
| `--endpoint-url=http://localhost:4566`                         | Usa el endpoint local (LocalStack) para simular AWS.                                       |
| `dynamodb create-table`                                        | Comando para crear una tabla en DynamoDB.                                                  |
| `--table-name local-flight-tickets`                            | Nombre de la tabla a crear: `local-flight-tickets`.                                         |
| `--attribute-definitions AttributeName=documentNumber,AttributeType=S AttributeName=ticket,AttributeType=S AttributeName=status,AttributeType=S` | Define los atributos `documentNumber`, `ticket` y `status`, todos tipo String (`S`). en este comando se indican todos los campos que se definirán como hash o range o secundary-indexes       |
| `--key-schema AttributeName=documentNumber,KeyType=HASH AttributeName=ticket,KeyType=RANGE` | Clave primaria compuesta por `documentNumber` (HASH) y `ticket` (RANGE).                   |
| `--billing-mode PAY_PER_REQUEST`                              | Modo de facturación: pago por solicitud (sin capacidad predefinida).                       |
| `--global-secondary-indexes "IndexName=statusIndex,KeySchema=[{AttributeName=status,KeyType=HASH}],Projection={ProjectionType=ALL}"` | Índice secundario global `statusIndex` usando `status` como clave HASH, proyecta todos los atributos. |
| `--region us-east-1`                                           | Región AWS usada para la tabla (necesaria aunque sea local).                               |

- Describir la tabla
    ```
    aws dynamodb describe-table --table-name local-flight-tickets --endpoint-url=http://localhost:4566 --region us-east-1
    ```

- Listar las tablas creadas
    ```
    aws dynamodb list-tables --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Eliminar una tabla en concreto
    ```
    aws dynamodb delete-table --table-name local-flight-tickets --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Actualizar la configuración de la tabla a modo pago provisioned
    ```
    aws dynamodb update-table --table-name local-flight-tickets --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=10 --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Actualizar la configuración de la tabla a modo pago PAY_PER_REQUEST
    ```
    aws dynamodb update-table --table-name local-flight-tickets --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Agregar items a la tabla
    ```
    aws dynamodb put-item --table-name local-flight-tickets --item "{\"documentNumber\":{\"S\":\"ABC123456\"},\"ticket\":{\"S\":\"465efd52-68fe-44df-ad30-3322f57a768f\"},\"status\":{\"S\":\"CREATED\"},\"flightNumber\":{\"S\":\"AV123\"},\"origin\":{\"S\":\"BOG\"},\"destination\":{\"S\":\"MIA\"},\"price\":{\"N\":\"280.50\"},\"date\":{\"S\":\"2025-07-01\"}}" --endpoint-url=http://localhost:4566 --region us-east-1


    aws dynamodb put-item --table-name local-flight-tickets --item "{\"documentNumber\":{\"S\":\"XYZ789012\"},\"ticket\":{\"S\":\"f20a63f0-6a0e-4d2e-9ef4-2dc43bd9d6bc\"},\"status\":{\"S\":\"PAID\"},\"flightNumber\":{\"S\":\"AV456\"},\"origin\":{\"S\":\"MDE\"},\"destination\":{\"S\":\"JFK\"},\"price\":{\"N\":\"500.00\"},\"date\":{\"S\":\"2025-08-15\"}}" --endpoint-url=http://localhost:4566 --region us-east-1


    aws dynamodb put-item --table-name local-flight-tickets --item "{\"documentNumber\":{\"S\":\"LMN456789\"},\"ticket\":{\"S\":\"68f1f89a-7a1c-4e15-8af3-d40f45721452\"},\"status\":{\"S\":\"CANCELLED\"},\"flightNumber\":{\"S\":\"AV789\"},\"origin\":{\"S\":\"CLO\"},\"destination\":{\"S\":\"LIM\"},\"price\":{\"N\":\"350.75\"},\"date\":{\"S\":\"2025-09-10\"}}" --endpoint-url=http://localhost:4566 --region us-east-1


    aws dynamodb put-item --table-name local-flight-tickets --item "{\"documentNumber\":{\"S\":\"QWE654321\"},\"ticket\":{\"S\":\"1542e5f1-fb89-4975-880e-4d262f8df30d\"},\"status\":{\"S\":\"CHECKED_IN\"},\"flightNumber\":{\"S\":\"AV321\"},\"origin\":{\"S\":\"PEI\"},\"destination\":{\"S\":\"PTY\"},\"price\":{\"N\":\"260.00\"},\"date\":{\"S\":\"2025-07-20\"}}" --endpoint-url=http://localhost:4566 --region us-east-1


    aws dynamodb put-item --table-name local-flight-tickets --item "{\"documentNumber\":{\"S\":\"ZXC987654\"},\"ticket\":{\"S\":\"ae80c42b-0d5d-4e80-a38f-b2ac292837c7\"},\"status\":{\"S\":\"BOARDED\"},\"flightNumber\":{\"S\":\"AV654\"},\"origin\":{\"S\":\"BAQ\"},\"destination\":{\"S\":\"EZE\"},\"price\":{\"N\":\"400.20\"},\"date\":{\"S\":\"2025-10-05\"}}" --endpoint-url=http://localhost:4566 --region us-east-1
    ```

    **Nota**: Si escribes el mismo comando dos veces el segundo sobre-escribe el primero, dynamoDB no genera error al repetir la clave y rango primarios y si se repite la primary key, como en este caso tiene la llave compuesta con un rango entonces se creará otro registro siempre y cuando el ticket sea diferente.

- Obtener un elemento de la tabla
    ```
    aws dynamodb get-item --table-name local-flight-tickets --key "{\"documentNumber\":{\"S\":\"ABC123456\"},\"ticket\":{\"S\":\"465efd52-68fe-44df-ad30-3322f57a768f\"}}" --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Actualizar un atributo de un registro de la tabla **
    ```
    aws dynamodb update-item --table-name local-flight-tickets --key "{\"documentNumber\":{\"S\":\"ABC123456\"},\"ticket\":{\"S\":\"465efd52-68fe-44df-ad30-3322f57a768f\"}}" --update-expression "SET #s = :newStatus" --expression-attribute-names '{"#s":"status"}' --expression-attribute-values '{":newStatus":{"S":"CONFIRMED"}}' --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Eliminar un registro de la tabla
    ```
    aws dynamodb delete-item --table-name local-flight-tickets --key "{\"documentNumber\":{\"S\":\"ABC123456\"},\"ticket\":{\"S\":\"465efd52-68fe-44df-ad30-3322f57a768f\"}}" --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Listar todos los datos de la tabla
    ```
    aws dynamodb scan --table-name local-flight-tickets --endpoint-url http://localhost:4566 --region us-east-1
    ```

- Consultar por una condición de un atributo de la tabla **
    ```
    aws dynamodb query --table-name local-flight-tickets --key-condition-expression "documentNumber = :docNum" --expression-attribute-values '{":docNum":{"S":"ABC123456"}}' --endpoint-url http://localhost:4566 --region us-east-1
    ```

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)