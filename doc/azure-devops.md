# Azure Devops

> A continuación se describe las configuraciones base para politicas de branches, creación de pipelines CI / CD

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

### Requisitos

- ⚠️ Tener una licencia gratuita de Azure Devops
- ⚠️ Crear un repositorio proyecto JAVA

### Sitio web
[Azure devops](https://dev.azure.com/)

### Indice

* [1. Agregar políticas a la rama main](#id1)
* [2. Crear la aplicación](#id2)
* [3. Postgresql: Crear la conexión](#id3)

# <div id='id1'/>
# Agregar políticas a la rama principal (main)

¿Qué puedes configurar en la rama principal? Desde el menú Repos > Branches, haz clic en los tres puntos junto a main y selecciona Branch policies. Ahí puedes activar:
- **Requerir pull request para hacer cambios:** bloquea los commits directos a la rama.
- **Requerir work items vinculados:** obliga a que cada PR esté asociado a una tarea, historia o bug (puede ser requerido u opcional)
- **Revisión mínima:** puedes exigir que al menos 1 persona apruebe el PR, si eres el único developer, entonces no actives esta opción.
- **Validación de compilación:** opcional, para que el PR solo se fusione si pasa el build.
- **Limit merge types:** sirve para controlar cómo se integran los cambios una vez que un pull request es aprobado y completado. Esto impacta directamente el historial de tu rama principal.  

## ¿Qué tipos de merge puedes limitar?

- Basic merge (no fast-forward): Conserva la historia exactamente como sucedió durante el desarrollo.
- Merge commit: Fusiona todos los commits del PR en un único commit de merge.
- Squash merge: Combina todos los commits del PR en un único commit limpio.
- Rebase and fast-forward: Reescribe el historial como si los cambios hubieran sido aplicados directamente.

## Recomendación: 

Habilita solo "squash merge" y desactiva los demás. Así tendrás un historial más claro y fácil de revisar cuando empieces a colaborar con más personas.

👉 Al activar cualquier política obligatoria en la rama Azure DevOps automáticamente bloquea los pushes directos a esa rama. Es decir, solo se puede modificar mediante Pull Requests que cumplan esas políticas.

![](../img/modules/azure/1_policies_branch.png)

# <div id='id2'/>
# Definición de otras políticas

## Build Validation

Esta opción asegura que los cambios propuestos en un Pull Request compilen correctamente antes de poder hacer merge.
- Puedes vincular un pipeline de CI (por ejemplo, uno en YAML) que se ejecute automáticamente cuando se crea o actualiza un PR.
- Si el build falla, el PR no podrá completarse.
- Ideal para prevenir errores en main y mantener la calidad del código.
- Puedes configurar múltiples validaciones si tienes distintos pipelines (tests, linting, etc.).

Ejemplo: Validar que el proyecto compile y pase pruebas unitarias antes de permitir el merge.

## Status Checks

Permite que servicios externos (como SonarQube, GitHub Actions, etc.) publiquen un estado en el PR que debe cumplirse para permitir el merge.
- Puedes exigir que ciertos checks externos estén en estado “success”.
- Útil para integraciones como análisis de calidad de código, escaneo de seguridad, despliegues, etc.
- Puedes definir si el check es obligatorio o solo informativo.

Ejemplo: Requerir que SonarQube publique un análisis exitoso antes de permitir el merge.

## Automatically Included Reviewers

Agrega automáticamente revisores a los Pull Requests que afecten ciertas rutas o archivos.
- Puedes incluir usuarios o grupos (como “Backend Team”).
- Puedes definir si su aprobación es obligatoria u opcional.
- Puedes usar filtros de ruta para que solo se incluyan si se modifican ciertos archivos.

Ejemplo: Si se modifica /infra/terraform/*, incluir automáticamente al equipo de DevOps como revisores obligatorios.

# <div id='id3'/>
# Agregar políticas build con SonarQube

## ¿Puedo usar SonarQube en la versión gratis de azure?

SonarQube Cloud Free Tier: Ideal para desarrolladores o equipos pequeños que quieren mejorar la calidad del código sin costo.

### Lo que puedes hacer:
- Analizar proyectos privados de hasta 50,000 líneas de código.
- Analizar Pull Requests (solo si apuntan a la rama principal).
- Soporte para 30 lenguajes de programación y plataformas IaC (como Terraform, Docker, etc.).
- Integración con plataformas DevOps como GitHub, GitLab, Bitbucket y Azure DevOps.
- Decoración automática de PRs con métricas, bugs, vulnerabilidades y cobertura.
- Análisis automático del código en la rama principal (sin configurar CI).
- Hasta 5 usuarios por organización.

🔗 👉 [📘 Ir al sitio SonarQube Cloud](https://sonarcloud.io/login)

- Loguearse con la cuenta de Azure Devops
- Elegir: import an organization
- Escribir el nombre de la organización
- Al escribir el token... Dar click en el link user settings y crear un nuevo token
![](../img/modules/azure/1_sonar_qube_link_token_azure.png)

![](../img/modules/azure/1_sonar_qube_create_token_azure.png.png)
- Elegir plan gratis
- Presionar boton: create organization
- Elegir todos los proyectos importados
- Presionar el boton "setup up"
- Seleccionar la opción "previous version"
- Presionar el boton "create projects"

![](../img/modules/azure/1_sonar_qube_created_projects.png)









---

[< Volver al índice](README.md)

💡 Esta documentación fue elaborada con ayuda de Copilot, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](LICENSE.md)

