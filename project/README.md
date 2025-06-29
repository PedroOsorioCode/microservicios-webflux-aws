## Iniciar la aplicación de forma local

1. Comandos ambiente local (si usas docker cambias *podman* por *docker*)
    ```
    podman machine start
    podman start localstack
    ```

   ![](./img/webflux-iniciar-ambiente-local.png)

2. Creación de la estructura de la tabla
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name local_worldregions --attribute-definitions AttributeName=region,AttributeType=S AttributeName=code,AttributeType=S --key-schema AttributeName=region,KeyType=HASH AttributeName=code,KeyType=RANGE --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
    ```
   
3. Ingreso items a la tabla (*Los comandos estan organizados para ejecutar en command line de windows*)
    ```
    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"REGION-LATAM\"},\"code\":{\"S\":\"d40b2031-2e14-4845-91cb-af0bf87a8ce3\"},\"name\":{\"S\":\"Colombia\"},\"codeRegion\":{\"S\":\"COUNTRY-COL\"},\"creationDate\":{\"S\":\"2025-06-24T20:15:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"REGION-LATAM\"},\"code\":{\"S\":\"ff50f4f8-2dd1-4466-a55f-47ce560e1f19\"},\"name\":{\"S\":\"argentina\"},\"codeRegion\":{\"S\":\"COUNTRY-ARG\"},\"creationDate\":{\"S\":\"2025-06-24T20:16:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"COUNTRY-COL\"},\"code\":{\"S\":\"0c3cbfbb-ef59-4e7e-a629-d64394f3dd77\"},\"name\":{\"S\":\"Antioquia\"},\"codeRegion\":{\"S\":\"DEPARTMENT-ANT\"},\"creationDate\":{\"S\":\"2025-06-24T20:17:00Z\"}}"

    aws --endpoint-url=http://localhost:4566 dynamodb put-item --table-name local_worldregions --item "{\"region\":{\"S\":\"DEPARTMENT-ANT\"},\"code\":{\"S\":\"f46a680a-5b1d-4d18-a01b-a07e90176e3c\"},\"name\":{\"S\":\"Medellin\"},\"codeRegion\":{\"S\":\"CITY-MED\"},\"creationDate\":{\"S\":\"2025-06-24T20:18:00Z\"}}"
    ```