# Instructivo paso a paso comprender conceptos Programación reactiva con Java Webflux
> A continuación se explica que es programación reactiva, webflux, características y explicaciones de los metodos mas usados.

### Indice

* [1. ¿Qué es programación reactiva?](#id1)
* [2. ¿Qué es webflux?](#id2)
* [3. Hilos en Java vs Modelo Reactivo con Spring WebFlux](#id3)
* [4. Construcción de hilos en JAVA](#id4)
* [5. Hilos con anotaciones de Spring](#id5)
* [6. Comparativo Webflux vs Async](#id6)
* [7. Hilos virtuales](#id7)
* [8. Hilos vs Infraestructura](#id8)
* [9. Cuando usar o no usar webflux](#id9)
* [10. Webflux: Métodos mas usados en Mono](#id10)
* [11. Webflux: Métodos mas usados en Flux](#id11)
* [12. Webflux: Ejemplos Mono y Flux](#id12)

# <div id='id1'/>
## 1. ¿Qué es programación reactiva?
> La programación reactiva es un paradigma de programación que se enfoca en manejar flujos de datos asíncronos y la propagación de cambios. En lugar de escribir código que ejecuta instrucciones paso a paso (como en la programación imperativa), en la programación reactiva defines cómo deben reaccionar tus aplicaciones a ciertos eventos o cambios de estado.

### Características principales

1. **Asincronía:** permite manejar tareas que toman tiempo (como peticiones a bases de datos o servicios web) sin bloquear el hilo principal.

2. **Flujos de datos (Streams):** los datos se modelan como flujos que pueden ser observados y transformados (como si fueran una secuencia de eventos en el tiempo)

### Ventajas
- Escalabilidad y eficiencia en aplicaciones con alta concurrencia.
- Código más declarativo y expresivo para manejar eventos, errores y asincronía.
- Mejor rendimiento en aplicaciones de tiempo real o intensivas en I/O.

# <div id='id2'/>
## 2. ¿Qué es Webflux?
> Spring WebFlux es un módulo del ecosistema Spring Framework diseñado para construir aplicaciones web reactivas, es decir, aplicaciones que manejan peticiones de forma asíncrona y no bloqueante.

### Características principales de WebFlux:

1. **No bloqueante y reactivo:** basado en el paradigma de programación reactiva usando Reactor, una implementación de la especificación Reactive Streams.

2. **Escalable:** al no bloquear los hilos, puede manejar muchas peticiones concurrentes con menos recursos, ideal para aplicaciones con alta carga o servicios que dependen de muchas llamadas a servicios externos.

3. **Soporte para R2DBC:** para acceso reactivo a bases de datos relacionales (como MySQL, PostgreSQL, H2, MariaDB, Microsoft SQL Server, Oracle Database, IBM Db2, CockroachDB, Apache Derby) permite conexión no bloqueante

### Conceptos generales

1. **¿Qué es un Mono?:** Mono<T> representa 0 o 1 elemento que será emitido en el futuro (de forma asíncrona y no bloqueante); Mono es ideal para flujos reactivos que devuelven un solo valor; Puedes encadenar todos estos métodos para construir pipelines potentes y controlados.
2. **¿Qué es un Flux?:** Flux<T> representa una secuencia reactiva de 0 a N elementos (puede ser vacía o infinita), se usa ampliamente en Spring WebFlux para manejar múltiples elementos de forma asíncrona y no bloqueante; Flux se usa cuando tienes varios elementos en tu flujo reactivo; Es común en streams de base de datos, respuestas de APIs, WebSockets, etc.

# <div id='id3'/>
## 3. Hilos en Java vs Modelo Reactivo con Spring WebFlux

Un hilo (Thread) es una unidad de ejecución independiente dentro de un proceso. En Java, cada solicitud o tarea puede ejecutarse en su propio hilo. Los hilos tradicionales son parte del modelo de concurrencia imperativo y bloqueante, donde:

Cada operación que involucra IO (acceso a red, base de datos, disco, etc.) bloquea el hilo hasta que termine.

Para manejar múltiples peticiones concurrentes, se crean múltiples hilos, cada uno esperando su turno.

### ⚙️ Características de los hilos tradicionales en Java

| Característica         | Hilos en Java (servlets clásicos)        |
| ---------------------- | ---------------------------------------- |
| Modelo de ejecución    | Por cada solicitud, un hilo              |
| Manejo de concurrencia | Pool de hilos (por ejemplo, Tomcat)      |
| Consumo de recursos    | Alto (cada hilo ocupa memoria y CPU)     |
| Escalabilidad          | Limitada (cuello de botella con IO)      |
| Ejemplo clásico        | Spring MVC, servlets, ThreadPoolExecutor |
| Latencia frente a IO   | Alta (bloqueo mientras espera respuesta) |

### ⚡¿Qué propone el modelo reactivo de Spring WebFlux?

Spring WebFlux se basa en el modelo reactivo no bloqueante, usando internamente Reactor (Mono y Flux) y el estándar Reactive Streams. No asigna un hilo por solicitud, sino que:

Usa pocos hilos (event loop) para manejar muchas conexiones simultáneas.

No bloquea hilos en operaciones IO. En lugar de esperar, registra una función callback que se ejecuta cuando la respuesta esté lista.

Ideal para aplicaciones con altas cargas concurrentes y uso intensivo de IO.

### ⚙️ Características del modelo reactivo con WebFlux

| Característica       | Spring WebFlux (reactivo, no bloqueante)      |
| -------------------- | --------------------------------------------- |
| Modelo de ejecución  | Basado en eventos, sin bloqueo                |
| Hilos utilizados     | Pocos hilos (Netty event loops)               |
| Consumo de recursos  | Bajo, eficiente en memoria y CPU              |
| Escalabilidad        | Alta (decenas de miles de conexiones activas) |
| Ejemplo clásico      | Mono, Flux, WebClient, RouterFunction         |
| Latencia frente a IO | Baja (no bloquea, se suspende la operación)   |

### 🧪 Comparativo práctico

| Aspecto                     | Hilos tradicionales (Spring MVC) | Reactivo (Spring WebFlux)        |
| --------------------------- | -------------------------------- | -------------------------------- |
| Modelo                      | Imperativo, bloqueante           | Declarativo, no bloqueante       |
| Hilo por petición           | Sí                               | No                               |
| Escalabilidad               | Media                            | Alta                             |
| Manejo de IO lento (DB/API) | Bloquea el hilo                  | Libera el hilo (callback)        |
| Pool de hilos               | Requiere gran tamaño             | Tamaño pequeño, controlado       |
| Flujo de datos              | Paso a paso                      | Flujo reactivo (`Mono` / `Flux`) |
| Ideal para                  | CPU intensivo, lógica simple     | IO intensivo, muchas peticiones  |

### 🎯 Comparación visual (simplificada):

| Petición | Spring MVC (bloqueante)       | WebFlux (reactivo, no bloqueante)     |
| -------- | ----------------------------- | ------------------------------------- |
| 1        | Usa Hilo-1 → espera IO        | Usa Hilo-1 → libera → retoma al final |
| 2        | Usa Hilo-2 → espera DB        | Usa Hilo-2 → libera → retoma al final |
| 3        | Usa Hilo-3 → espera otra cosa | Usa Hilo-1 (reutilizado) → sigue      |

# <div id='id4'/>
## 4. Construcción de hilos en JAVA

- Extendiendo la clase Thread
    ```
    public class MiHilo extends Thread {
        public void run() {
            System.out.println("Ejecutando en: " + Thread.currentThread().getName());
        }
    }

    // Uso
    new MiHilo().start();
    ```

- Implementando Runnable
    ```
    public class MiTarea implements Runnable {
        public void run() {
            System.out.println("Desde hilo: " + Thread.currentThread().getName());
        }
    }

    // Uso
    Thread hilo = new Thread(new MiTarea());
    hilo.start();
    ```

- Usando expresiones lambda
    ```
    Thread hilo = new Thread(() -> System.out.println("Hola desde un hilo"));
    hilo.start();
    ```

### ¿Qué se puede configurar en un hilo?

| Configuración             | Descripción                                                         |
| ------------------------- | ------------------------------------------------------------------- |
| **Nombre del hilo**       | Ayuda al seguimiento y logging.                                     |
| **Prioridad**             | De 1 a 10, puede influir en el orden de ejecución.                  |
| **Daemon**                | Si el hilo es de fondo (no bloquea la terminación del programa).    |
| **Timeouts/Interrupción** | Control de cuándo parar el hilo en ejecuciones largas o colgadas.   |
| **Pool size**             | En `ExecutorService`, cuántos hilos simultáneos se pueden ejecutar. |
| **Queue capacity**        | Cuántas tareas se pueden poner en espera antes de rechazar nuevas.  |


- Ejemplo con ExecutorService

    ```
    ExecutorService pool = Executors.newFixedThreadPool(10);
    pool.submit(() -> {
        System.out.println("Tarea ejecutada en hilo: " + Thread.currentThread().getName());
    });
    ```

- Ejemplo Comparativo JAVA vs Webflux, en un proyecto java con webflux puede crearse la siguiente clase para validar respuestas y comparar rendimiento. vamos a simular 100 peticiones, se usarán 3 hilos

    - Resumen:
    - Con 3 hilos procesa 100 peticiones con espera de 0.3 segundos por cada hilo en aproximadamente 11 segundos
    - Con 10 hilos procesa 100 peticiones con espera de 0.3 segundos por cada hilo en aproximadamente 4 segundos
    - Webflux procesa 100 peticiones con espera de 0.3 segundos en 0.5 segundos

- Código de prueba

    ```
    package co.com.microservice.aws;

    import java.util.concurrent.*;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;
    import java.time.Duration;

    public class RequestComparison {

        public static void main(String[] args) throws InterruptedException {
            runBlockingExample();
            runReactiveExample();
        }

        static void runBlockingExample() throws InterruptedException {
            System.out.println("---- Starting blocking execution ----");

            int totalRequests = 100;
            int threadPoolSize = 3;
            ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);

            long start = System.currentTimeMillis();
            CountDownLatch latch = new CountDownLatch(totalRequests);

            for (int i = 0; i < totalRequests; i++) {
                pool.submit(() -> {
                    try {
                        Thread.sleep(300); // Simulates blocking I/O
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    latch.countDown();
                });
            }

            latch.await();
            long end = System.currentTimeMillis();
            System.out.println("Blocking total time: " + (end - start) + " ms");

            pool.shutdown();
            System.out.println("---- Finished blocking execution ----\n");
        }

        static void runReactiveExample() {
            System.out.println("---- Starting reactive execution ----");

            int totalRequests = 100;
            long start = System.currentTimeMillis();

            Flux.range(1, totalRequests)
                    .flatMap(i -> Mono.delay(Duration.ofMillis(300)))
                    .doOnComplete(() -> {
                        long end = System.currentTimeMillis();
                        System.out.println("Reactive total time: " + (end - start) + " ms");
                        System.out.println("---- Finished reactive execution ----");
                    })
                    .blockLast(); // Wait for all reactive flows to complete
        }
    }
    ```

- Resultado de ejecutar el código anterior:
    ```
    ---- Starting blocking execution ----
    Blocking total time: 10595 ms
    ---- Finished blocking execution ----

    ---- Starting reactive execution ----
    Reactive total time: 492 ms
    ---- Finished reactive execution ----
    ```

# <div id='id5'/>
## 5. Hilos con anotaciones de Spring

### Anotación @Async
Spring proporciona la anotación @Async para ejecutar métodos de forma asincrónica en segundo plano:

- Configuración mínima
    Primero debes habilitar la ejecución asincrónica:
    ```
    @SpringBootApplication
    @EnableAsync
    public class TuAplicacion {
        public static void main(String[] args) {
            SpringApplication.run(TuAplicacion.class, args);
        }
    }
    ```
- Ejemplo de clase de servicio con @Async
    ```
    @Service
    public class TareaAsincronaService {

        @Async
        public CompletableFuture<String> ejecutarLento() throws InterruptedException {
            Thread.sleep(5000); // Simula una tarea lenta
            return CompletableFuture.completedFuture("Tarea completada");
        }
    }
    ```

- Llamada desde un controlador
    ```
    @RestController
    @RequestMapping("/tarea")
    public class TareaController {

        private final TareaAsincronaService tareaService;

        public TareaController(TareaAsincronaService tareaService) {
            this.tareaService = tareaService;
        }

        @GetMapping
        public ResponseEntity<String> ejecutar() {
            tareaService.ejecutarLento(); // Se ejecuta en segundo plano
            return ResponseEntity.ok("Ejecutando...");
        }
    }
    ```

- Aplicar configuraciones personalizadas a los hilos
    ```
    @Configuration
    public class AsyncConfig implements AsyncConfigurer {

        @Override
        @Bean(name = "tareaExecutor")
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);                    // Hilos mínimos activos
            executor.setMaxPoolSize(10);                    // Hilos máximos
            executor.setQueueCapacity(100);                 // Capacidad de la cola
            executor.setThreadNamePrefix("HiloAsync-");     // Nombre para seguimiento
            executor.setWaitForTasksToCompleteOnShutdown(true); // Espera al apagar
            executor.setAwaitTerminationSeconds(30);             // Máximo tiempo de espera
            executor.initialize();
            return executor;
        }

        // Manejo de excepciones no capturadas
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> {
                System.err.println("Excepción en método async: " + method.getName());
                ex.printStackTrace();
            };
        }
    }
    ```

- Aplicar Async con el Executor creado
    ```
    @Service
    public class TareaAsincronaService {

        @Async("tareaExecutor")
        public CompletableFuture<String> ejecutar() throws InterruptedException {
            Thread.sleep(3000); // Simulación
            return CompletableFuture.completedFuture("Ejecutado con éxito");
        }
    }
    ```

### ¿Qué es ThreadPoolTaskExecutor?
Es un pool (grupo) de hilos reutilizables gestionado por Spring. En lugar de crear un hilo nuevo cada vez, los hilos se reutilizan para ejecutar múltiples tareas de forma eficiente.

- Tabla de configuración de ThreadPoolTaskExecutor
    | Configuración| Descripción         | Ejemplo/Comportamiento|
    | ------------ | ------------------- | --------------------- |
    | `setCorePoolSize(int)`                         | Número **mínimo** de hilos que el pool mantiene activos, aunque estén inactivos.            | Siempre habrá al menos estos hilos listos. Si llegan tareas, se usan primero.     |
    | `setMaxPoolSize(int)`                          | Número **máximo** de hilos que se pueden crear (incluye los core).                          | Se usan si la cola se llena y aún hay tareas en espera.                           |
    | `setQueueCapacity(int)`                        | Número de tareas que se pueden **esperar en cola** mientras los hilos están ocupados.       | Si los core están llenos, las tareas se encolan aquí antes de crear nuevos hilos. |
    | `setThreadNamePrefix(String)`                  | Prefijo para los nombres de hilos, útil para depuración y logs.                             | Ej.: `HiloAsync-1`, `HiloAsync-2`, etc.                                           |
    | `setWaitForTasksToCompleteOnShutdown(boolean)` | Si `true`, espera que se terminen las tareas en ejecución al apagar el contexto de Spring.  | Evita que se corten tareas pendientes al cerrar la app.                           |
    | `setAwaitTerminationSeconds(int)`              | Tiempo máximo (en segundos) para esperar la finalización de tareas al apagar la aplicación. | Si pasa este tiempo, los hilos se fuerzan a cerrar.                               |

- ¿Qué pasa con los hilos hijos? ¿Se reutilizan?

    Sí. Los hilos no se crean y destruyen por cada tarea. El flujo es así:

    1. Entran tareas nuevas.
    2. Se ejecutan en los core threads (hasta 5).
    3. Si todos están ocupados, se encolan (hasta 100 tareas en espera).
    4. Si la cola también se llena, se crean hilos extra hasta el máximo (maxPoolSize).
    5. Una vez un hilo termina una tarea, no se destruye: queda listo para la siguiente.

- Ejemplo práctico visual

    Supón esta configuración:
    - corePoolSize = 2
    - maxPoolSize = 4
    - queueCapacity = 3

    Y llegan 8 tareas asincrónicas al mismo tiempo:
    | Tarea | ¿Qué pasa?                                 |
    | ----- | ------------------------------------------ |
    | 1     | Se ejecuta en hilo 1 (core)                |
    | 2     | Se ejecuta en hilo 2 (core)                |
    | 3     | Se encola                                  |
    | 4     | Se encola                                  |
    | 5     | Se encola                                  |
    | 6     | No hay más capacidad en cola → crea hilo 3 |
    | 7     | Se ejecuta en hilo 4 (max reached)         |
    | 8     | Excepción (RejectedExecutionException)     |

- Y si una tarea falla?
    Si un método @Async lanza una excepción no capturada:

    - No rompe el hilo.
    - El hilo queda reutilizable para otra tarea.
    - Pero debes capturar errores (por eso puedes implementar AsyncUncaughtExceptionHandler).

# <div id='id6'/>
## 6. Comparativo Webflux vs Async

### ¿@Async es igual a WebFlux?
No, no son iguales. Aunque ambos permiten procesamiento asincrónico y no bloqueante en cierta medida, la diferencia fundamental está en el modelo de concurrencia y escalabilidad.

### 🧵 @Async (ThreadPoolTaskExecutor)
Usa hilos reales (del sistema operativo) para ejecutar tareas en segundo plano. Los hilos son reutilizables, sí, pero limitados en número y pueden bloquearse (por ejemplo, si haces una llamada HTTP o accedes a una base de datos).

### ⚛️ Spring WebFlux
Usa un modelo reactivo basado en un loop de eventos, donde no se bloquea el hilo mientras espera I/O. Todo se ejecuta de forma no bloqueante y cooperativa usando Project Reactor (Mono, Flux). Por eso puede escalar muchísimo mejor con menos recursos.

### ¿Cuál es mejor?
| Escenario                                      | Recomendación                   |
| ---------------------------------------------- | ------------------------------- |
| Procesamiento simple o tareas en segundo plano | `@Async`                        |
| Alta concurrencia, muchos usuarios simultáneos | WebFlux                         |
| Llamadas que bloquean (base de datos, APIs)    | `@Async` con cuidado            |
| Comunicación con APIs reactivas o NoSQL        | WebFlux                         |
| Sistemas que ya están en Spring MVC            | `@Async` puede integrarse fácil |
| Aplicaciones reactivas de extremo a extremo    | WebFlux                         |

### Tabla comparativa
| Característica                  | `Thread` (puro)                      | `@Async` (Spring)                         | Spring WebFlux                              |
| ------------------------------- | ------------------------------------ | ----------------------------------------- | ------------------------------------------- |
| Tipo de programación            | Imperativa                           | Imperativa con asincronía controlada      | Reactiva, basada en flujos (`Mono`, `Flux`) |
| Requiere pool de hilos          | No (crea uno por tarea)              | Sí (gestión con `ThreadPoolTaskExecutor`) | Sí, pero usa pocos hilos (event-loop)       |
| Reutilización de hilos          | ❌ No                                 | ✅ Sí                                      | ✅ Sí (modelo no bloqueante)                 |
| Escalabilidad                   | ❌ Baja                               | ⚠️ Moderada                               | ✅ Muy alta                                  |
| Uso de memoria                  | ❌ Alto (muchos hilos = mucha RAM)    | ⚠️ Moderado                               | ✅ Eficiente                                 |
| Manejo de errores               | Manual                               | Spring lo gestiona con `AsyncHandler`     | Reactor lo gestiona (`onError...`)          |
| Integración con contexto Spring | ❌ Difícil                            | ✅ Total                                   | ✅ Total                                     |
| Bloqueo de hilos                | ✅ Sí (por ejemplo, `Thread.sleep()`) | ✅ Sí (si haces blocking I/O)              | ❌ No (todo debe ser no bloqueante)          |
| Curva de aprendizaje            | Fácil                                | Fácil                                     | Más compleja                                |
| Mejor para                      | Tareas sueltas, pruebas rápidas      | Tareas paralelas simples                  | Backends reactivos, sistemas escalables     |

### Tabla de usuarios concurrentes por modelo de hilos
| Modelo de concurrencia                 | Tipo de hilos                    | Soporta tareas bloqueantes     | Escalabilidad aproximada                     | Usuarios concurrentes (estimado) |
| -------------------------------------- | -------------------------------- | ------------------------------ | -------------------------------------------- | -------------------------------- |
| `Thread` (Java puro)                   | Un hilo por solicitud            | ✅ Sí                           | ❌ Muy baja (crea muchos hilos)               | 🔻 \~100–200                     |
| `@Async` con `ThreadPoolTaskExecutor`  | Hilos gestionados (pool)         | ✅ Sí                           | ⚠️ Media (pool limitado)                     | 🟡 \~300–800                     |
| Spring MVC (Servlet)                   | Un hilo por solicitud            | ✅ Sí                           | ⚠️ Media (depende del pool del servidor web) | 🟡 \~500–1000                    |
| Spring WebFlux                         | Event-loop (reactor, no bloquea) | ❌ No (requiere stack reactivo) | ✅ Alta (usa menos hilos)                     | 🟢 \~3000–10000+                 |
| WebFlux + stack completamente reactivo | Event-loop 100% no bloqueante    | ❌ No                           | ✅✅ Muy alta                                  | 🟢🟢 \~10000–100000+             |


# <div id='id7'/>
## 7. Hilos Virtuales

### ¿Qué son los hilos virtuales?

Los hilos virtuales son una nueva característica introducida oficialmente en Java 21 (estable) que permite crear miles o millones de hilos ligeros gestionados por la JVM (y no directamente por el sistema operativo).

- Son mapeados sobre un pequeño número de hilos del sistema operativo.
- La JVM los suspende y reanuda automáticamente cuando hay operaciones bloqueantes (como IO).
- No necesitas cambiar tu código a programación reactiva o usar @Async, ni gestionar pools.

### ¿Qué ventajas ofrecen?
| Ventaja                          | Descripción                                                 |
| -------------------------------- | ----------------------------------------------------------- |
| ✅ Creados casi instantáneamente  | No hay límite práctico de miles de hilos.                   |
| ✅ Consumen poca memoria          | Alrededor de **kilobytes**, no megabytes como los `Thread`. |
| ✅ Código sigue siendo bloqueante | Puedes seguir usando JDBC, `HttpClient`, etc.               |
| ✅ Más fáciles que WebFlux        | No necesitas aprender programación reactiva.                |

# <div id='id8'/>
## 8. Hilos vs Infraestructura

### Supuesto base de infraestructura
| Recurso                        | Valor aproximado                           |
| ------------------------------ | ------------------------------------------ |
| CPU                            | 2 vCPU                                     |
| RAM                            | 4 GB                                       |
| SO                             | Linux Ubuntu 22.04                         |
| Límite típico de hilos nativos | \~500–1000 (depende del SO y uso de stack) |

### Tabla de gasto estimado por modelo de hilos
| Modelo                    | RAM por hilo aprox. | Uso de CPU por hilo                      | Límite práctico de hilos | Observaciones clave                                                                |
| ------------------------- | ------------------- | ---------------------------------------- | ------------------------ | ---------------------------------------------------------------------------------- |
| `Thread` (Java puro)      | 🟥 1 MB             | 🟨 Alta si hay trabajo activo            | 🔻 200–500               | Cada hilo es un recurso del SO; stack fijo grande.                                 |
| `@Async` con pool         | 🟨 \~512 KB         | 🟨 Similar al anterior                   | 🟡 300–800               | Menor RAM si se gestiona bien el pool, pero sigue usando hilos nativos.            |
| Spring MVC (servlet)      | 🟨 \~512 KB         | 🟨 Alta si bloquea (I/O)                 | 🟡 500–1000              | Un hilo por request, igual que `@Async` pero controlado por el contenedor servlet. |
| WebFlux (reactivo)        | 🟢 10–100 KB        | 🟩 Muy baja por solicitud                | 🟢 3000–10000+           | Usa pocos hilos para miles de conexiones (event loop).                             |
| Hilos virtuales (Java 21) | 🟢 10–50 KB         | 🟨 Baja si hay bloqueo (gestión interna) | 🟢🟢 100000+             | JVM suspende/reanuda el hilo virtual al detectar bloqueo. Muy eficientes.          |

### Conclusión final
| ¿Qué tan eficiente es?    | RAM         | CPU         | Escalable para muchas tareas | Comentario clave                                               |
| ------------------------- | ----------- | ----------- | ---------------------------- | -------------------------------------------------------------- |
| `Thread` (Java puro)      | ❌ Muy mala  | ❌ Alta      | ❌ No                         | No recomendable salvo para pruebas simples.                    |
| `@Async`                  | ⚠️ Moderada | ⚠️ Alta     | ⚠️ Limitado                  | Mejor que Thread, pero sigue limitado por recursos.            |
| Spring MVC (servlet)      | ⚠️ Moderada | ⚠️ Alta     | ⚠️ Regular                   | Bien para apps normales, pero escala limitado.                 |
| WebFlux                   | ✅ Muy buena | ✅ Muy baja  | ✅✅ Excelente                 | Ideal para I/O intensivo, APIs REST, servicios web escalables. |
| Hilos virtuales (Java 21) | ✅ Muy buena | ⚠️ Variable | ✅✅ Excelente                 | Ideal para lógica imperativa y escalar sin reactividad.        |

# <div id='id9'/>
## 9. Cuando usar o no usar webflux

### ✅ ¿Cuándo SÍ usar WebFlux?
| Escenario                                                               | Justificación                                          |
| ----------------------------------------------------------------------- | ------------------------------------------------------ |
| ✅ Alta concurrencia (miles de usuarios concurrentes)                    | WebFlux escala mejor con menos recursos.               |
| ✅ Todo tu stack es **no bloqueante** (ej. WebClient, R2DBC, Mongo, etc) | WebFlux brilla cuando nada bloquea los hilos.          |
| ✅ APIs reactivas o comunicación entre servicios asincrónica             | Puedes usar `Mono`/`Flux` de extremo a extremo.        |
| ✅ Necesitas streaming (SSE, WebSocket, backpressure, etc.)              | WebFlux lo gestiona nativamente.                       |
| ✅ Infraestructura con recursos limitados (RAM/CPU)                      | WebFlux consume menos memoria que MVC bajo alta carga. |


### 🚫 ¿Cuándo NO usar WebFlux?
| Escenario                                                            | Por qué evitarlo                                            |
| -------------------------------------------------------------------- | ----------------------------------------------------------- |
| ❌ Usas muchas librerías **bloqueantes** (JDBC, `RestTemplate`, etc.) | Bloqueas hilos del event-loop, pierdes ventajas del modelo. |
| ❌ Proyecto pequeño, pocos usuarios, y no hay problema de rendimiento | Spring MVC es más simple, fácil de mantener.                |
| ❌ Tu equipo no tiene experiencia con programación reactiva           | WebFlux puede introducir bugs difíciles de rastrear.        |
| ❌ Integración con herramientas que esperan `ServletRequest/Response` | WebFlux no usa servlet container clásico.                   |
| ❌ Tienes que mantener código legado en MVC                           | Mezclar WebFlux con MVC es muy complejo y no recomendado.   |

# <div id='id10'/>
## 10. Webflux: Métodos mas usados en Mono

1. **map** – Transformación síncrona
    ```
    Mono<String> mono = Mono.just("mundo");
    mono.map(valor -> "Hola " + valor) // retorno un String
        .subscribe(System.out::println); // imprime: Hola mundo
    ```
    - ¿Qué hace? Transforma el valor emitido de forma síncrona.
    - Se espera: que el valor original cambie pero sin generar un nuevo flujo reactivo.

2. **flatMap** – Transformación asíncrona
    ```
    Mono<String> mono = Mono.just("usuario");
    mono.flatMap(nombre -> buscarPerfilPorNombre(nombre)) // retorna Mono<Perfil>
        .subscribe(System.out::println);

    ```
    - ¿Qué hace? Transforma el valor usando otro Mono.
    - Se espera: que el valor se use para generar un nuevo flujo y se encadene correctamente.

3. **doOnNext** – Efectos secundarios
    ```
    Mono<String> mono = Mono.just("hola");
    mono.doOnNext(valor -> System.out.println("Recibí: " + valor)) //imprime el valor en consola, sigue emitiendo un String
        .map(valor -> valor.toUpperCase()) // transforma a mayusculas
        .subscribe(System.out::println); // imprime: Recibí: hola, luego HOLA
    ```
    - ¿Qué hace? Ejecuta una acción (como escribir en logs) sin modificar el valor.
    - Se espera: que se use para efectos secundarios seguros (sin romper el flujo).

4. **onErrorResume** – Manejo de errores
    ```
    Mono<String> mono = Mono.error(new RuntimeException("Algo falló"));
    mono.onErrorResume(ex -> Mono.just("valor alternativo")) //Captura una exception, pero Emite un string
        .subscribe(System.out::println); // imprime: valor alternativo
    ```
    - ¿Qué hace? Intercepta el error y continúa con un nuevo Mono.
    - Se espera: evitar que el flujo termine por error.

5. **switchIfEmpty** – Valor por defecto si no se emite nada
    ```
    Mono<String> mono = Mono.empty();
    mono.switchIfEmpty(Mono.just("sin datos")) //como está vacío, emite un nuevo flujo con un String
        .subscribe(System.out::println); // imprime: sin datos
    ```
    - ¿Qué hace? Proporciona un valor alternativo si el flujo no emite nada.
    - Se espera: asegurar que siempre haya una emisión, aunque sea por defecto.
    - Se usa para devolver otro Mono si el anterior está vacío.
    
6. **thenReturn** – Ignorar anterior y emitir otro valor
    ```
    Mono<String> mono = Mono.just("valor");
    mono.thenReturn("nuevo valor") // omite el valor en el flujo y retorna otro valor
        .subscribe(System.out::println); // imprime: nuevo valor
    ```
    - ¿Qué hace? Descarta el resultado anterior y emite otro valor.
    - Se espera: continuar con otro valor sin depender del anterior.

7. **doOnError** – Loguear o actuar ante error
    ```
    Mono<String> mono = Mono.error(new RuntimeException("Error"));
    mono.doOnError(err -> System.out.println("Se produjo un error: " + err.getMessage()))
        .onErrorResume(e -> Mono.just("recuperado"))
        .subscribe(System.out::println); // imprime: Se produjo un error..., luego: recuperado
    ```
    - ¿Qué hace? Ejecuta una acción si ocurre un error.
    - Se espera: observar errores sin detener el flujo.

8. **filter** – Condicionar el valor
    ```
    Mono<Integer> mono = Mono.just(5);
    mono.filter(numero -> numero > 10) // emite en el mismo flujo un valor si cumple la condición
        .switchIfEmpty(Mono.just(999))
        .subscribe(System.out::println); // imprime: 999
    ```
    - ¿Qué hace? Deja pasar el valor solo si cumple una condición.
    - Se espera: que el flujo continúe solo si la condición es verdadera.

9. **zipWith** – Combinar dos Monos
    ```
    Mono<String> nombre = Mono.just("Juan");
    Mono<String> apellido = Mono.just("Pérez");

    nombre.zipWith(apellido, (n, a) -> n + " " + a)
        .subscribe(System.out::println); // imprime: Juan Pérez
    ```
    - ¿Qué hace? Combina dos monos y aplica una función sobre ambos.
    - Se espera: obtener un resultado que depende de los dos valores.

10. **delayElement** – Simula retrasos (útil en pruebas)
    ```
    Mono.just("con retraso")
    .delayElement(Duration.ofSeconds(1))
    .subscribe(System.out::println);
    ```
    - ¿Qué hace? Introduce una pausa antes de emitir.
    - Se espera: útil para simular latencias.

11. **defer** – retrasa la creación del Mono hasta el momento de la suscripción
    ```
    Mono<String> mono = Mono.defer(() -> Mono.just("Hora actual: " + LocalTime.now()));
    ```
    - ¿Qué hace? Cada vez que te suscribas, generará un nuevo valor con la hora actual. Si no usas defer, el valor se captura en el momento en que defines el Mono, y todas las suscripciones verán el mismo valor.

12. **doOnDiscard** – ejecutar una acción cuando un elemento del flujo es descartado
    ```
    Mono<String> mono = Mono.just("Ana")
        .filter(nombre -> nombre.startsWith("Z"))
        .doOnDiscard(String.class, descartado -> System.out.println("Descartado: " + descartado));
    ```
    - ¿Qué hace? Se usa para realizar limpieza de recursos, logs de auditoría o simplemente para entender qué datos no fueron procesados.

13. **defaultIfEmpty** - se usa para devolver un valor directo si el Mono está vacío.
    ```
    Mono<String> mono = Mono.empty()
    mono.defaultIfEmpty("Nuevo valor");
    ```

# <div id='id11'/>
## 11. Webflux: Métodos mas usados en Flux

1. **map** – Transformación síncrona
    ```
    Flux<Integer> numeros = Flux.just(1, 2, 3);
    numeros.map(n -> n * 10)
        .subscribe(System.out::println); // imprime: 10, 20, 30
    ```
    - ¿Qué hace? Aplica una transformación síncrona a cada elemento emitido.
    - Se espera: modificar cada valor de la secuencia sin romper el flujo

2. **flatMap** – Transformación asíncrona de cada valor
    ```
    Flux<String> nombres = Flux.just("Ana", "Luis");
    nombres.flatMap(nombre -> buscarInfo(nombre)) // cada llamada devuelve Flux<String>
       .subscribe(System.out::println);
    ```
    - ¿Qué hace? Transforma cada valor a otro Publisher (como Flux o Mono).
    - Se espera: que cada transformación sea asíncrona y se aplane en un solo Flux.

3. **filter** – Filtrado de elementos
    ```
    Flux<Integer> numeros = Flux.range(1, 10);
    numeros.filter(n -> n % 2 == 0)
        .subscribe(System.out::println); // imprime: 2, 4, 6, 8, 10
    ```
    - ¿Qué hace? Deja pasar solo los elementos que cumplen una condición.
    - Se espera: reducir la cantidad de elementos emitidos.

4. **doOnNext** – Efectos secundarios por elemento
    ```
    Flux<String> datos = Flux.just("uno", "dos");
    datos.doOnNext(valor -> System.out.println("Procesando: " + valor))
        .map(String::toUpperCase)
        .subscribe(System.out::println); // imprime: Procesando UNO Procesando DOS y luego UNO DOS
    ```
    - ¿Qué hace? Ejecuta una acción con cada elemento emitido, sin modificarlo.
    - Se espera: usarlo para logging, métricas o acciones externas.

5. **onErrorResume** – Recuperar de errores
    ```
    Flux<Integer> flujo = Flux.just(1, 2)
        .concatWith(Flux.error(new RuntimeException("fallo")))
        .onErrorResume(ex -> Flux.just(999)); // no interrumpe el flujo y continúan emitiendo los valores
    flujo.subscribe(System.out::println); // imprime: 1, 2, 999
    ```
    - ¿Qué hace? Captura errores y permite continuar con otro Flux.
    - Se espera: no detener el flujo por fallos inesperados.

6. **collectList** – Convertir Flux en Mono<List>
    ```
    Flux<String> palabras = Flux.just("uno", "dos", "tres");
    palabras.collectList()
        .subscribe(System.out::println); // imprime: [uno, dos, tres]
    ```
    - ¿Qué hace? Recoge todos los elementos en una List emitida como Mono<List<T>>.
    - Se espera: transformar el flujo en un solo resultado acumulado.
    
6. **take(n)** – Tomar solo los primeros N elementos
    ```
    Flux.range(1, 10)
        .take(3)
        .subscribe(System.out::println); // imprime: 1, 2, 3
    ```
    - ¿Qué hace? Limita el número de emisiones.
    - Se espera: reducir el volumen procesado.

7. **delayElement** – Introducir pausa entre elementos
    ```
    Flux.range(1, 3)
        .delayElements(Duration.ofSeconds(1))
        .subscribe(System.out::println);
    ```
    - ¿Qué hace? Introduce un retraso entre cada emisión.
    - Se espera: simular latencias o trabajar con eventos espaciados en el tiempo.

8. **zipWith** – Combinar dos flujos
    ```
    Flux<String> nombres = Flux.just("Juan", "Laura");
    Flux<String> apellidos = Flux.just("Pérez", "López");

    nombres.zipWith(apellidos, (n, a) -> n + " " + a)
        .subscribe(System.out::println); // imprime: Juan Pérez, Laura López
    ```
    - ¿Qué hace? Combina los elementos de dos flujos por posición.
    - Se espera: generar un flujo nuevo a partir de dos fuentes.

9. **concatWith** – Encadenar flujos
    ```
    Flux<String> parte1 = Flux.just("A", "B");
    Flux<String> parte2 = Flux.just("C", "D");

    parte1.concatWith(parte2)
        .subscribe(System.out::println); // imprime: A, B, C, D
    ```
    - ¿Qué hace? Agrega elementos de otro Flux después del primero.
    - Se espera: crear flujos secuenciales.

10. **flatMapSequential** – Similar a flatMap pero respeta el orden
    ```
    Flux<String> items = Flux.just("1", "2", "3");
    items.flatMapSequential(id -> buscarDatos(id))
        .subscribe(System.out::println);
    ```
    - ¿Qué hace? Como flatMap, pero preserva el orden original.
    - Se espera: operaciones asíncronas sin perder el orden de entrada.

11. **buffer(n)** – Agrupar en listas
    ```
    Flux.range(1, 10)
        .buffer(3)
        .subscribe(System.out::println); // imprime: [1,2,3], [4,5,6], [7,8,9], [10]
    ```
    - ¿Qué hace? Agrupa los elementos en listas de tamaño n.
    - Se espera: dividir el flujo en lotes.

12. **defer** – retrasa la creación del Mono hasta el momento de la suscripción
    ```
    Flux<String> flux = Flux.defer(() -> Mono.just("Hora actual: " + LocalTime.now()));
    ```
    - ¿Qué hace? Cada vez que te suscribas, generará un nuevo valor con la hora actual. Si no usas defer, el valor se captura en el momento en que defines el Mono, y todas las suscripciones verán el mismo valor.

13. **doOnDiscard** – ejecutar una acción cuando un elemento del flujo es descartado
    ```
    Flux<String> nombres = Flux.just("Ana", "Luis", "Carlos", "Sofía")
        .filter(nombre -> nombre.startsWith("Z"))
        .doOnDiscard(String.class, descartado -> System.out.println("Descartado: " + descartado));
    ```
    - ¿Qué hace? Se usa para realizar limpieza de recursos, logs de auditoría o simplemente para entender qué datos no fueron procesados.

# <div id='id12'/>
## 12. Webflux: Ejemplo combinando funciones con un flujo Mono
    ```
    public Mono<ServerResponse> handleRequest(ServerRequest request) {
        return request.bodyToMono(String.class) // 1. Recibe el nombre del usuario
            .filter(nombre -> !nombre.trim().isEmpty()) // 2. Validación
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Nombre vacío")))
            .flatMap(this::buscarPerfilPorNombre) // 3. Simulación de búsqueda
            .doOnNext(perfil -> log.info("Perfil encontrado: {}", perfil)) // Logging
            .flatMap(perfil -> emitirEventoExito(perfil).thenReturn(perfil)) // 4. Emitir evento
            .map(this::mapearADto) // Mapeo a DTO
            .flatMap(dto ->
                ServerResponse.ok() // 5. Respuesta
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
            )
            .onErrorResume(e -> emitirEventoError(e).then(
                ServerResponse.status(HttpStatus.BAD_REQUEST) // 6. Error
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", e.getMessage()))
            ));
    }
    ```

## Ejemplo combinando funciones con un flujo Flux
    ```
    public Mono<ServerResponse> handleProducts(ServerRequest request) {
        return request.bodyToFlux(String.class) // 1. Lista de IDs
            .filter(id -> !id.isBlank()) // 2. Filtro de IDs vacíos
            .flatMap(this::consultarProductoPorId) // 3. Consulta de productos
            .map(this::aplicarDescuento) // 4. Transformación
            .buffer(3) // 5. Agrupación en lotes
            .collectList() // Convertir a Mono<List<List<ProductoDTO>>>
            .flatMap(lotes ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(lotes) // 6. Respuesta
            )
            .onErrorResume(e ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", e.getMessage()))
            );
    }
    ```
---

🔗 👉 [📘 Ver instructivo paso a paso JAVA-REACTIVO – STACK TECNOLÓGICO](../PRINCIPAL.md)

--- 

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)