# Montaje ambiente local DYNAMODB
> A continuación se explica que es dynamodb, características e indicaciones del paso a paso que se debe realizar para crear un contenedor con el servicio de dynamoDB

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