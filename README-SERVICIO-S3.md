# Instructivo paso a paso montaje ambiente local Secret Manager
> A continuación se explica que es amazon secret manager, características e indicaciones del paso a paso que se debe realizar para crear secretos y su finalidad

### Requisitos: 

Haber leido [Primeros pasos ambiente local](README-AMBIENTE-LOCAL.md)

## ¿Qué es Amazon S3 (Simple Storage Service)?

Amazon S3 (Simple Storage Service) es un servicio de almacenamiento en la nube proporcionado por AWS (Amazon Web Services), diseñado para almacenar y recuperar cualquier cantidad de datos en cualquier momento y desde cualquier lugar en la web.

## ¿Qué es un S3 Bucket?

Un bucket en Amazon S3 es un contenedor lógico donde se almacenan objetos (archivos). Puedes pensar en un bucket como una carpeta raíz en la que subes tus archivos, aunque internamente S3 no funciona exactamente como un sistema de archivos tradicional.

## Características principales de un bucket:
- **Nombre único a nivel global**: Cada bucket debe tener un nombre único en todo AWS.

- **Región**: Al crearlo, eliges una región (como us-east-1, eu-west-1, etc.), lo que afecta la latencia y el costo.

- **Objetos**: Dentro del bucket puedes subir archivos (llamados "objetos") que pueden tener metadatos y permisos.

- **Control de acceso**: Puedes definir quién tiene acceso al bucket o a objetos específicos (a través de políticas, ACLs o IAM).

- **Versionado**: Se puede habilitar para conservar versiones antiguas de objetos.

- **Cifrado**: Se puede activar para que los datos estén protegidos en reposo.

- **Eventos**: Se puede configurar para que, por ejemplo, al subir un archivo se dispare una función Lambda.

| Operación                | Descripción                                   | Comando AWS CLI                                       |
| ------------------------ | --------------------------------------------- | ----------------------------------------------------- |
| Crear bucket             | Crea un nuevo bucket en una región específica | `aws s3 mb s3://nombre-del-bucket --region us-east-1` |
| Listar buckets           | Muestra todos los buckets en tu cuenta AWS    | `aws s3 ls`                                           |
| Eliminar bucket          | Elimina un bucket vacío                       | `aws s3 rb s3://nombre-del-bucket`                    |
| Listar objetos en bucket | Lista todos los objetos dentro de un bucket   | `aws s3 ls s3://nombre-del-bucket`                    |
| Subir archivo            | Sube un archivo local al bucket               | `aws s3 cp archivo.txt s3://nombre-del-bucket/`       |
| Descargar archivo        | Descarga un archivo del bucket a local        | `aws s3 cp s3://nombre-del-bucket/archivo.txt ./`     |
| Eliminar archivo         | Elimina un archivo dentro del bucket          | `aws s3 rm s3://nombre-del-bucket/archivo.txt`        |

## Ejemplos de crear bucket y subir archivo

- Crear bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 mb s3://local-s3-file-batch
    ```

- Subir un archivo al bucket
    ```
    aws --endpoint-url=http://localhost:4566 s3 cp file-payment.txt s3://local-s3-file-batch/CC/912907722/file-payment.txt
    ```

