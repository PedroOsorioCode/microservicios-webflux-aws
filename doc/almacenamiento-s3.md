# Comprender conceptos de almacenamient S3 - bucket
> A continuación se explica qué es Amazon S3, sus características principales y el paso a paso necesario para interactuar con este servicio en un entorno controlado. Aunque S3 es un servicio gestionado por AWS, existen herramientas como LocalStack que permiten simular su comportamiento de forma local para pruebas y desarrollo.

## 📦 ¿Qué es Amazon S3?  

Amazon Simple Storage Service (S3) es un servicio de almacenamiento de objetos en la nube, diseñado para almacenar y recuperar cualquier cantidad de datos desde cualquier lugar. Es altamente escalable, duradero y seguro, lo que lo hace ideal para:

- Almacenamiento de archivos estáticos (imágenes, videos, documentos, backups).
- Hosting de sitios web estáticos.
- Integración con flujos de datos, IA, ML y Big Data.
- Almacenamiento de logs, exportaciones o contenido generado por usuarios.

Cada archivo que se almacena en S3 se denomina objeto, y está contenido dentro de un bucket (contenedor lógico que actúa como espacio de nombres).

## 🚀 Características principales

- **Almacenamiento de objetos**  
    A diferencia de sistemas de archivos tradicionales, S3 guarda los datos como objetos, que incluyen:

    - Datos binarios o texto (el archivo en sí).
    - Metadatos (tipo MIME, tamaño, etc.).
    - Identificador único (clave dentro del bucket).

- **Alta durabilidad (99.999999999%)**  
    Gracias a la replicación automática en múltiples zonas de disponibilidad, S3 garantiza la durabilidad a largo plazo de los datos.

- **Escalabilidad automática**  
    No necesitas aprovisionar capacidad. Puedes almacenar desde 1 byte hasta exabytes sin configuraciones adicionales.

- **Acceso controlado mediante políticas y roles IAM**  
    Puedes controlar el acceso a buckets y objetos mediante políticas de bucket, listas ACL y reglas de IAM. También permite configuraciones públicas, privadas o mixtas.

- **Versionado de objetos**  
    Permite mantener múltiples versiones de un mismo objeto, facilitando restauraciones o auditorías.

- **Clases de almacenamiento**  
    S3 ofrece diferentes clases según el uso y la frecuencia de acceso:

    - STANDARD: uso frecuente.
    - INTELLIGENT_TIERING: se adapta al uso automáticamente.
    - STANDARD_IA: acceso infrecuente.
    - GLACIER / GLACIER DEEP ARCHIVE: archivado de largo plazo.

- **Eventos y notificaciones**  
    Puedes configurar notificaciones que reaccionen a acciones sobre objetos (PUT, DELETE), integrándose con Lambda, SNS o SQS.

- **Integración nativa con otros servicios AWS**  
    Por ejemplo: CloudFront, Lambda, Athena, Redshift, SageMaker, etc.

## Montaje local con Podman y localstack

- Crear un bucket S3 local
    ```
    aws --endpoint-url=http://localhost:4566 s3 mb s3://local-s3-file-gateway-batch
    ```

- Subir un archivo al bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 cp name_file.txt s3://local-s3-file-gateway-batch/num_nit/name_file.txt
    ```

- Descargar un archivo desde el bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 cp s3://local-s3-file-gateway-batch/num_nit/name_file.txt archivo-descargado.txt
    ```

- Listar archivos dentro del bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 ls s3://local-s3-file-gateway-batch/num_nit/
    ```

- Eliminar un archivo del bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 rm s3://local-s3-file-gateway-batch/num_nit/name_file.txt
    ```
## 🧮 Costos estimados Amazon S3

- Para almacenamiento pequeño (ej: backups, archivos de app)

    | Clase de almacenamiento | Precio por GB / mes | Ejemplo (5 GB) | Duración estimada  | Costo mensual aprox. |
    | ----------------------- | ------------------- | -------------- | ------------------ | -------------------- |
    | S3 Standard             | \$0.023             | 5 GB           | Permanente         | \~\$0.115            |
    | S3 Standard-IA          | \$0.0125            | 5 GB           | Acceso infrecuente | \~\$0.0625           |
    | S3 Glacier              | \$0.004             | 5 GB           | Archivado          | \~\$0.02             |

    ⚠️ Estas cifras no incluyen costos por lectura/escritura, solo almacenamiento puro.

- Supuestos realistas:

    - Archivos: HTML, CSS, JS, imágenes → total: 1 GB
    - Tráfico mensual: 10,000 visitas → 500 MB de transferencia por visita → ~5 TB
    - Solicitudes (GET): ~500,000

    | Concepto                   | Cantidad aproximada | Tarifa estimada AWS                | Costo mensual aprox. |
    | -------------------------- | ------------------- | ---------------------------------- | -------------------- |
    | Almacenamiento (1 GB)      | 1 GB                | \$0.023 / GB                       | \~\$0.02             |
    | Transferencia saliente     | 5 TB                | \$0.09 / GB después de 1 GB gratis | \~\$450.00           |
    | Solicitudes GET            | 0.5 millones        | \$0.0004 por 1,000 solicitudes     | \~\$0.20             |
    | **Total estimado mensual** |                     |                                    | **\~\$450.22**       |


[< Volver al índice](../README.md)

---
💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)