## ¿Qué es Terraform?

Terraform es una herramienta de Infraestructura como Código (IaC) creada por HashiCorp, que permite definir, aprovisionar y administrar infraestructura en forma declarativa, mediante archivos .tf (en formato HCL, HashiCorp Configuration Language).

En resumen: automatiza la creación y gestión de infraestructura (servidores, bases de datos, redes, contenedores, etc.), tanto en la nube como en entornos locales.

## ¿Es libre?

Sí, Terraform es de código abierto y tiene licencia MPL 2.0 (Mozilla Public License).
Sin embargo, HashiCorp cambió su modelo de licencia en 2023 a BUSL (Business Source License) para las versiones más recientes, lo que significa:

- Sigue siendo gratuito para uso personal y educativo.
- No puedes comercializar Terraform como un servicio (SaaS) sin licencia empresarial.
- Puedes usar versiones anteriores (por ejemplo, v1.5) bajo licencias más permisivas.  Para uso local y proyectos propios

## Características clave

| Característica          | Descripción                                                                  |
| ----------------------- | ---------------------------------------------------------------------------- |
| 🧾 Declarativo          | Se define **qué** quieres (infraestructura deseada), no **cómo** construirla |
| 🔁 Idempotente          | Si ejecutas `terraform apply` varias veces, solo se aplican los cambios      |
| 🔄 Planificación        | Usa `terraform plan` para previsualizar cambios sin aplicarlos               |
| 🔌 Multiplataforma      | Compatible con AWS, Azure, GCP, Kubernetes, Docker, Podman, VMWare, etc.     |
| 🧩 Modularidad          | Puedes reutilizar código con **módulos**                                     |
| 💾 Estado persistente   | Guarda un archivo `terraform.tfstate` que refleja el estado real             |
| 🛡️ Gestión de secretos | Compatible con Vault, AWS Secrets, archivos `.env`, etc.                     |
| 📜 Lenguaje propio      | Usa **HCL**, que es legible y fácil de versionar                             |

## ¿Qué puedo crear con Terraform?

- Máquinas virtuales (EC2, Azure VM, etc.)
- Bases de datos (RDS, MySQL, PostgreSQL)
- Buckets S3
- Redes (VPC, subredes, firewalls)
- Contenedores (Docker, Podman)
- Clústeres de Kubernetes
- Balanceadores de carga
- Cloud Functions / Lambdas
- Y mucho más


## Comandos básicos
```
terraform init      # Inicializa el proyecto
terraform plan      # Muestra lo que se va a aplicar
terraform apply     # Aplica la infraestructura
terraform destroy   # Elimina la infraestructura
```

## ¿Qué otras herramientas existen (competencia)?
| Herramienta        | Lenguaje               | Estilo      | Proveedor principal | Comentario breve                                 |
| ------------------ | ---------------------- | ----------- | ------------------- | ------------------------------------------------ |
| **Pulumi**         | JS, TS, Go, Python     | Imperativo  | Multi-cloud         | Más flexible (programación real)                 |
| **Ansible**        | YAML                   | Imperativo  | Agnóstico           | Más para configuración que para provisión        |
| **CloudFormation** | JSON/YAML              | Declarativo | AWS                 | Solo para AWS, más verboso                       |
| **CDK (AWS CDK)**  | TS, Python, Java, etc. | Imperativo  | AWS                 | Infraestructura con código real, orientado a AWS |
| **Chef/Puppet**    | Ruby/YAML              | Imperativo  | Multi               | Más orientados a configuración de servidores     |

## ¿Por qué es tan usada?

- Es fácil de aprender y legible
- Permite modularizar y reutilizar
- Tiene gran comunidad y documentación
- Funciona con casi todos los proveedores
- Muy buena para automatización CI/CD

## Instalar Terraform (windows)

1. Instalar chocolatey
    - [ver sitio web](https://chocolatey.org/install)
    - Abrir Windows powershell como **administrador**
    - Pegar el siguiente comando
    ```
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    ```
    - Si se equivocan al elegir el modo, deben eliminar la carpeta chocolatey de la ruta: C:\ProgramData y volver a colocar el código
    - Validar la versión con el script: **choco -v**  
    ejemplo:
        ```
        C:\WINDOWS\system32> choco -v
        2.5.0
        ```

2. Instalar Terraform
    - [Ver sitio web](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)
    - Ejecutar en powershell como **administrador** el siguiente comando:
    ```
    choco install terraform
    ```
    Indique (**y**) cuando se le pregunte si desea ejecutar el script
    - Validar la instalación de terraform
    ```
    terraform -v

    -- Resultado esperado o con una versión superior
    PS C:\WINDOWS\system32> terraform -v
    Terraform v1.12.2
    on windows_amd64
    ```    

---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](../PRINCIPAL.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)