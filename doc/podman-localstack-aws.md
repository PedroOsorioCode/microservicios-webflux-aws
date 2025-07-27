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

## ¿Por qué se recomienda usar Podman antes de usar Kubernetes.?
Kubernetes se ha establecido como el estándar de facto para la gestión de cargas de trabajo a gran escala. Podman es una herramienta poderosa y versátil que ofrece una curva de aprendizaje más suave y una serie de ventajas que lo convierten en el trampolín ideal para el universo de la orquestación de contenedores.

- Simplicidad y Menos Complejidad Inicial:

    Kubernetes es un sistema vasto y complejo, diseñado para manejar infraestructuras distribuidas y entornos de producción a gran escala.
    
    Podman, por otro lado, es mucho más sencillo y directo. Se enfoca en la gestión de contenedores individuales y pods (grupos de contenedores relacionados) en una única máquina.

- Entorno sin Demonio (Daemonless):

    Podman opera sin demonio, lo que significa que los contenedores se ejecutan como procesos de usuario normales. Esto tiene varias ventajas:

- Mayor Seguridad: 

    Los usuarios no necesitan permisos de root para ejecutar o construir contenedores.

- Mayor Estabilidad: 

    - La ausencia de un demonio elimina un posible punto de fallo. Si un contenedor falla, no afecta al proceso del demonio ni a otros contenedores.

    - Facilidad de Integración con Systemd: Podman se integra de forma nativa con systemd, lo que permite gestionar contenedores y pods como servicios de sistema, facilitando la automatización y el reinicio automático.

- Compatibilidad con OCI y Sintaxis Familiar (Docker-like):

    Podman es compatible con los estándares de la Open Container Initiative (OCI), lo que asegura que las imágenes y contenedores creados con Podman sean interoperables con otras herramientas que sigan estos estándares, incluyendo Kubernetes.

- Construcción de Imágenes sin Dockerfile (Buildah y Skopeo):

    - Buildah: Permite construir imágenes de contenedores sin necesidad de un Dockerfile. Ofrece un control más granular sobre el proceso de construcción, lo que es útil para crear imágenes mínimas y seguras.

    - Skopeo: Facilita la copia, el movimiento y la inspección de imágenes entre diferentes registros de contenedores, sin necesidad de descargarlas localmente.

    - Estas herramientas, combinadas con Podman, ofrecen un ecosistema robusto para la gestión completa del ciclo de vida de las imágenes de contenedor.

- Preparación para Kubernetes (Generación de YAML):

    Una de las características más destacadas de Podman es su capacidad para generar archivos YAML compatibles con Kubernetes.

    Con un simple comando (podman generate kube), puedes convertir un pod o un conjunto de contenedores en ejecución en un manifiesto YAML que puede ser desplegado directamente en un clúster de Kubernetes.

    Al generar los manifiestos YAML, los usuarios pueden ver cómo sus contenedores y pods se traducen a los objetos de Kubernetes (Deployment, Service, etc.), lo que ayuda a comprender la estructura y los conceptos de Kubernetes de manera práctica.

---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](../PRINCIPAL.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)
