# Montaje ambiente local
> A continuación se indica el paso a paso que se debe realizar para crear localmente los recursos necesarios para ejecución de los microservicios con servicios de la nube AWS

### Requisitos: 

- Debe tenerse instalado Podman o Docker [Ver guía podman](https://podman.io/docs/installation)  
  **Nota**: La guía se realiza con podman dado que este tiene una licencia libre.
- Debe tener instalado AWS CLI [ver guía](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

### Primeros pasos con podman

> Podman requiere de una máquina virtual con linux generalmente se crea con Fedora CoreOS y se usa para correr los contenedores; esto es cuando se trata de mac/windows OS, en linux no se requiere; en la máquina se puede definir CPU, DiskSize o Memory; es un espacio para contenedores, volúmenes, imágenes, etc.


- Abrir consola de comandos (windows OS)
    ```
    Techa windows + cmd + enter
    ```
- Crear la máquina (Se requiere solo la primera vez): 
    ```
    podman machine init
    ```
- Iniciar la máquina (Siempre que se detenga la máquina):
    ```
    podman machine start
    ```
- Detener la máquina:
    ```
    podman machine stop
    ```
- Listar máquinas creadas:
    ```
    podman machine list
    ```
- Inspeccionar la máquina:
    ```
    podman machine inspect
    ```
- Crear nuevas máquinas con otros propósitos por ejemplo asignar CPU, Disksize o Memory predeterminada
    ```
    podman machine init maquina-dev --cpus 2 --disk-size 20 --memory 4096 
    ```
- Iniciar la máquina creada:
    ```
    podman machine maquina-dev start
    ```
- Inspeccionar la máquina:
    ```
    podman machine inspect
- Eliminar la máquina creada (debe estar detenida):
    ```
    podman machine rm maquina-dev
    ```

## Descargar la imagen y correr el contenedor de localstack
    ```
    podman run --name localstack -d -p 4566-4599:4566-4599 -p 9000:9000 docker.io/localstack/localstack:0.12.7
    ```

|Parte del comando|Explicación|
|----------------|-----------|
|`podman run`|Ejecuta un nuevo contenedor a partir de una imagen.|
|`--name localstack`|Asigna el nombre `localstack` al contenedor para identificarlo fácilmente.|
|`-d`|Ejecuta el contenedor en segundo plano (modo daemon).|
|`-p 4566-4599:4566-4599`|Expone el rango de puertos 4566 a 4599 del contenedor al host, usados por servicios simulados de AWS.|
|`-p 9000:9000`|Expone el puerto 9000, utilizado antiguamente para el panel web de LocalStack (no siempre activo).|
|`docker.io/localstack/localstack:0.12.7`|Imagen del contenedor a usar, en este caso `localstack/localstack` versión `0.12.7` de Docker Hub.|

## Explorar las imagenes y los contenedores
- Ver imagenes
    ```
    podman images
    ```
- Ver contenedores activos e inactivos
    ```
    podman ps -a
    ```
- Iniciar el contenedor (La imagen ya queda descargada)
    ```
    podman start localstack
    ```

## Servicios mas comunes ejecutados en localstack

| Servicio AWS | Descripción |
|---------------------|----------------------------------------------------------|
| S3                  | Almacenamiento de objetos (buckets, archivos)            |
| DynamoDB            | Base de datos NoSQL totalmente gestionada                 |
| Lambda              | Funciones serverless para ejecutar código en respuesta a eventos |
| SQS                 | Servicio de colas para mensajes                           |
| SNS                 | Servicio de notificaciones por temas                      |
| API Gateway         | Puerta de enlace para crear APIs REST y WebSocket        |
| CloudWatch Logs     | Servicio para monitoreo y registro de logs                |
| IAM                 | Gestión de identidades y accesos (limitado)               |
| Kinesis             | Servicio para procesamiento de streams de datos           |
| Secrets Manager     | Gestión de secretos y claves (limitado)                   |
| EventBridge         | Bus de eventos para conectar aplicaciones                  |
| Step Functions      | Orquestación de flujos de trabajo serverless               |
| CloudFormation      | Gestión y despliegue de infraestructura como código (limitado) |
| ECR                 | Registro de imágenes de contenedores                       |
| ElastiCache (Redis) | Caché en memoria (experimental en LocalStack)              |


[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)
