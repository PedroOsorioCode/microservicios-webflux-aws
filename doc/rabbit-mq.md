# Conceptos de eventos RabbitMq
> A continuación se explica qué es RabbitMQ, sus características principales y el paso a paso necesario para interactuar con este servicio en un entorno controlado.

## ¿Qué es RabbitMQ? 

RabbitMQ es un broker de mensajes que actúa como intermediario para el intercambio de información entre aplicaciones o servicios de manera asíncrona y desacoplada. Es una herramienta fundamental en arquitecturas basadas en eventos y microservicios, ya que permite comunicar distintos componentes sin que estos dependan directamente entre sí.

Se basa en el protocolo AMQP (Advanced Message Queuing Protocol), diseñado para la mensajería fiable, segura y orientada a mensajes.

### Características principales

- **Soporte para múltiples protocolos:** Aunque RabbitMQ utiliza principalmente AMQP, también soporta MQTT, STOMP y HTTP con plugins adicionales.

- **Enrutamiento flexible de mensajes:** Usa exchanges (direct, topic, fanout, headers) para definir cómo se enrutan los mensajes hacia una o varias colas (queues).

- **Persistencia de mensajes:** Se pueden configurar mensajes y colas como persistentes para garantizar la entrega incluso ante fallos.

- **Confirmaciones de entrega (acknowledgments):** El consumidor puede confirmar la recepción de un mensaje para asegurar que no se pierde.

- **Escalabilidad:** Permite configuraciones en clúster, uso de colas distribuidas y soporte para alta disponibilidad.

- **Plugins y extensibilidad:** Ofrece una amplia gama de plugins para monitoreo (como Prometheus), autenticación personalizada, gestión de permisos, etc.

- **Panel de administración web:** Interfaz gráfica para visualizar el estado de colas, conexiones, mensajes pendientes, y más.

- **Seguridad:** Autenticación basada en usuarios, control de acceso granular y soporte para TLS/SSL.

### Protocolos de comunicación soportados

RabbitMQ fue diseñado con AMQP como su protocolo principal, pero su arquitectura flexible permite usar otros protocolos de mensajería mediante plugins oficiales o de la comunidad. Esto lo hace útil para integrarse con diferentes tipos de clientes o dispositivos.

1. Protocolo: AMQP

    - AMQP (Advanced Message Queuing Protocol) es un protocolo abierto y orientado a mensajes que define:

        - Productores (Producers): Componentes que publican mensajes en un exchange.

        - Exchanges: Enrutadores de mensajes que los direccionan hacia una o más colas según reglas configuradas.

        - Colas (Queues): Almacenes temporales de mensajes que esperan a ser consumidos.

        - Consumidores (Consumers): Componentes que reciben y procesan los mensajes desde las colas.

        - Bindings: Relaciones entre exchanges y colas con condiciones específicas de enrutamiento.

        - Acks y Nacks: Mecanismos de confirmación o rechazo de recepción de mensajes.

2. Protocolo MQTT (Message Queuing Telemetry Transport)

    - Protocolo de mensajería ligero y basado en el modelo publicador/suscriptor, ampliamente utilizado en IoT (Internet of Things) por su eficiencia en redes de baja latencia y ancho de banda limitado.

        - Diseñado para dispositivos con recursos limitados.

        - Usa el patrón publish/subscribe con topics.

        - Basado en TCP/IP.

        - Soporta calidad de servicio (QoS): niveles 0, 1 y 2 para garantizar entrega de mensajes.

        - RabbitMQ lo implementa mediante el plugin rabbitmq-mqtt.

    Ejemplo de uso:

    Un sensor de temperatura publica datos a un topic casa/temperatura, y una app móvil se suscribe para recibir lecturas en tiempo real.

3. STOMP (Simple Text Oriented Messaging Protocol)

    - Protocolo de mensajería basado en texto plano, fácil de usar y entender, compatible con aplicaciones que necesitan una interfaz simple para trabajar con brokers de mensajes.

        - Muy simple: cada mensaje es como una secuencia de líneas de texto (similar a HTTP).

        - Ideal para clientes web o móviles.

        - Facilita pruebas manuales (puedes usar telnet o netcat).

        - RabbitMQ lo implementa mediante el plugin rabbitmq-stomp.

    Ejemplo de uso:

    Un frontend en JavaScript puede conectarse a RabbitMQ usando STOMP a través de WebSockets para recibir notificaciones en tiempo real.

4. HTTP (y WebSockets)

    - RabbitMQ puede ser accedido mediante HTTP API para tareas administrativas, y también se puede usar para mensajería a través de WebSockets en combinación con STOMP.

        - HTTP API: permite gestionar colas, exchanges, bindings, usuarios, etc.

        - WebSockets + STOMP: para clientes que no pueden usar TCP directamente.

        - Facilita la integración con aplicaciones web modernas.

    Ejemplo de uso:

    Una aplicación web en tiempo real que usa WebSockets + STOMP para mostrar actualizaciones de estado sin recargar la página.

### Exchanges en RabbitMQ

Un **Exchange** es un componente central en RabbitMQ encargado de recibir mensajes de los productores y enrutarlos hacia una o varias colas (queues)según una lógica de enrutamiento específica.

Los exchanges no almacenan mensajes, solo los redireccionan según su tipo y las reglas de vinculación (bindings) definidas.

### Tipos de Exchanges

RabbitMQ ofrece **cuatro tipos de exchanges** principales, cada uno con un comportamiento de enrutamiento distinto:

### 1. 🔁 Direct Exchange

- Enruta los mensajes según una coincidencia **exacta** entre la `routing key` del mensaje y la especificada en el `binding` de la cola.
- Ideal para mensajes dirigidos a un único consumidor o tipo específico.

**Ejemplo:**  
routing key del mensaje: "email.notificacion"  
binding key de la cola: "email.notificacion"

### 2. 🧩 Topic Exchange

- Enruta los mensajes según **patrones de `routing key` con comodines**.
- Útil para enrutamiento jerárquico y flexible.
- Comodines:
  - `*` reemplaza **una palabra**.
  - `#` reemplaza **cero o más palabras**.

**Ejemplo:**

routing key del mensaje: "sensor.temp.livingroom"

binding key: "sensor.temp.*" ✔ Coincide  
binding key: "sensor.#" ✔ También coincide

### 3. 📢 Fanout Exchange

- Ignora la `routing key` y **envía el mensaje a todas las colas enlazadas al exchange**.
- Útil para difundir mensajes a múltiples consumidores (broadcast).

**Ejemplo:**

✔ Un mensaje publicado en un fanout exchange se entrega a todas las colas conectadas a él, sin importar la routing key.

### 4. 🧾 Headers Exchange

- Enruta los mensajes según los **encabezados (headers)** del mensaje, en lugar de la routing key.
- Permite condiciones más complejas de enrutamiento.
- Usa el argumento `x-match`:
  - `all` (por defecto): todos los headers deben coincidir.
  - `any`: basta con que uno coincida.

**Ejemplo:**

headers del mensaje: {format: "pdf", type: "report"}  
binding headers: {format: "pdf", type: "report", x-match: "all"}  
✔ Coincide y se enruta a la cola

### RabbitMQ vs Amazon SQS

| Característica            | **RabbitMQ**                                | **Amazon SQS**                              |
| ------------------------- | ------------------------------------------- | ------------------------------------------- |
| 🛠 Tipo de servicio       | Software de mensajería (broker open-source) | Servicio gestionado por AWS                 |
| 📦 Modelo de enrutamiento | Exchange → Cola → Consumidor                | Cola → Consumidor (sin exchanges)           |
| 🧩 Tipos de exchange      | Sí (direct, topic, fanout, headers)         | No, es más simple                           |
| ☁️ Gestión                | Autogestionado o en contenedor              | Completamente gestionado por AWS            |
| 📤 Push vs Pull           | Soporta ambos (push/pull)                   | Principalmente pull                         |
| 🧮 Retención              | Mientras el mensaje esté en la cola         | Hasta 14 días (configurable)                |
| 📡 Protocolos soportados  | AMQP, MQTT, STOMP, WebSocket                | HTTP/HTTPS                                  |
| 📈 Rendimiento            | Muy alto, configurable                      | Alta escalabilidad automática               |
| 🔒 Seguridad              | TLS, autenticación, vhosts, usuarios        | IAM, políticas, roles                       |
| ⛓ Integraciones           | Se integra bien con microservicios, IoT     | Alta integración con otros servicios de AWS |


---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](../PRINCIPAL.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)