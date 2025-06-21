# Montaje ambiente local Secret Manager
> A continuación se explica que es amazon secret manager, características e indicaciones del paso a paso que se debe realizar para crear secretos y su finalidad

### Requisitos: 

Haber leido [Primeros pasos ambiente local](1-1-podman-localstack-aws.md)

## ¿Qué es Amazon Secrets Manager?

AWS Secrets Manager es un servicio de Amazon Web Services que te permite almacenar, administrar y recuperar secretos de forma segura. Un "secreto" puede ser cualquier tipo de información sensible, como: Contraseñas de bases de datos, Claves API, Credenciales para servicios externos, Certificados, Tokens de autenticación

## Características principales:
- **Almacenamiento seguro**: Los secretos se cifran automáticamente usando AWS KMS (Key Management Service).

- **Rotación automática de secretos**: Puedes configurar la rotación automática de credenciales sin necesidad de actualizar manualmente tu aplicación.

- **Gestión centralizada**: Tienes una ubicación única para administrar y acceder a secretos desde múltiples servicios o aplicaciones.

- **Acceso mediante políticas de IAM**: Puedes controlar quién tiene acceso a qué secretos.

- **Auditoría con CloudTrail**: Puedes rastrear quién accede a tus secretos y cuándo.

## Reglas y limitaciones de los secretos
- **Tamaño máximo**: 
    - El tamaño total de un secreto no debe superar los 64 KB (kilobytes).
    - Esto incluye tanto el contenido del secreto como los metadatos (nombre, etiquetas, etc.).

- **Formato del secreto**
    - El secreto puede ser una cadena de texto o un JSON.
    - Si se usa JSON, puedes almacenar múltiples pares clave-valor en un solo secreto.

- **Nombre del secreto**
    - Puede tener hasta 512 caracteres.
    - Puede incluir letras, números, guiones, barras (/) y guiones bajos.
    - Debe ser único dentro de una cuenta de AWS y una región.

- **Etiquetas (tags)**
    - Puedes añadir hasta 50 etiquetas por secreto para organizar y controlar el acceso.

- **Versiones del secreto**
    - Cada vez que actualizas un secreto, se crea una nueva versión.
    - Puedes acceder a versiones específicas mediante etiquetas de versión (AWSCURRENT, AWSPREVIOUS, etc.).

- **Rotación automática**
    - Puedes habilitar la rotación automática de secretos.
    - Esto requiere una función de AWS Lambda que defina cómo generar y actualizar el secreto.

- **Cifrado**
    - AWS Key Management Service (KMS) es un servicio de AWS que permite crear y administrar claves de cifrado para proteger datos. Secrets Manager lo utiliza por defecto para cifrar y descifrar secretos.

## Comandos

| Operación                              | Descripción                                                   | Comando AWS CLI                                                                                       |
|----------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| Crear un secreto                       | Crea un nuevo secreto con una cadena JSON                     | aws secretsmanager create-secret --name NOMBRE --secret-string '{"clave":"valor"}'                   |
| Obtener un secreto                     | Recupera el valor del secreto                                 | aws secretsmanager get-secret-value --secret-id NOMBRE                                                |
| Listar todos los secretos              | Muestra todos los secretos almacenados                        | aws secretsmanager list-secrets                                                                       |
| Actualizar un secreto                  | Reemplaza el valor de un secreto existente                    | aws secretsmanager update-secret --secret-id NOMBRE --secret-string '{"clave":"nuevo_valor"}'        |
| Eliminar un secreto (con retención)    | Marca el secreto para eliminación en 30 días (por defecto)    | aws secretsmanager delete-secret --secret-id NOMBRE                                                   |
| Eliminar un secreto (inmediato)        | Elimina un secreto sin período de retención                   | aws secretsmanager delete-secret --secret-id NOMBRE --force-delete-without-recovery                  |
| Restaurar un secreto eliminado         | Cancela la eliminación de un secreto                          | aws secretsmanager restore-secret --secret-id NOMBRE                                                  |
| Rotar automáticamente un secreto       | Habilita la rotación automática del secreto                   | aws secretsmanager rotate-secret --secret-id NOMBRE                                                   |
| Deshabilitar rotación automática       | Desactiva la rotación automática del secreto                  | aws secretsmanager cancel-rotate-secret --secret-id NOMBRE                                            |
| Agregar una etiqueta a un secreto      | Asocia una etiqueta clave-valor al secreto                    | aws secretsmanager tag-resource --secret-id NOMBRE --tags Key=CLAVE,Value=VALOR                      |
| Ver etiquetas de un secreto            | Muestra todas las etiquetas asociadas a un secreto            | aws secretsmanager list-secret-version-ids --secret-id NOMBRE                                         |
| Quitar una etiqueta de un secreto      | Elimina una o más etiquetas del secreto                       | aws secretsmanager untag-resource --secret-id NOMBRE --tag-keys CLAVE                                 |
| Ver versiones de un secreto            | Lista las versiones disponibles de un secreto                 | aws secretsmanager list-secret-version-ids --secret-id NOMBRE                                         |

## Ejemplos de secretos para otros servicios

- Conexión RabbitMQ
    ```
    aws secretsmanager create-secret --name local-rabbitmq --description "Connection to RabbitMQ" --secret-string "{\"virtualhost\":\"/\",\"hostname\":\"localhost\",\"username\":\"guest\",\"password\":\"guest\",\"port\":5672}" --endpoint-url=http://localhost:4566
    ```

- Conexión Redis
    ```
    aws secretsmanager create-secret --name local-redis --description "Connection to Redis" --secret-string "{\"username\":\"admin\",\"password\":\"password123\",\"host\":\"localhost\",\"port\":\"6379\"}" --endpoint-url=http://localhost:4566
    ```

- Otros ejemplos
    ```
    aws secretsmanager create-secret --name local-mysql --secret-string '{"username":"admin","password":"S3cretPass!","host":"db-prod.abc123.us-east-1.rds.amazonaws.com","port":3306,"dbname":"app_db"}'
    
    aws secretsmanager create-secret --name local-rabbitmq --description "Connection to RabbitMQ" --secret-string "{\"virtualhost\":\"/\",\"hostname\":\"localhost\",\"username\":\"guest\",\"password\":\"guest\",\"port\":5672}" --endpoint-url=http://localhost:4566
    
    aws secretsmanager create-secret --name local-redis --description "Connection to Redis" --secret-string "{\"username\":\"admin\",\"password\":\"password123\",\"host\":\"localhost\",\"port\":\"6379\"}" --endpoint-url=http://localhost:4566
    
    aws secretsmanager create-secret --name local-sftp-credentials --secret-string '{"username":"sftp_user","password":"SftpP@ssw0rd","host":"sftp.partner.com","port":22}'
    
    aws secretsmanager create-secret --name github-access-token --secret-string '{"token":"ghp_abcdefghijklmnopqrstuvwx123456"}'
    ```

[< Volver al índice](README.md)

---

**Author**: Pedro Luis Osorio Pavas [Linkedin](www.linkedin.com/in/pedro-luis-osorio-pavas-68b3a7106)  
**Start Date**: 01-06-2025  
**Update Date**: 01-06-2025.README-AMBIENTE-LOCAL.md)

