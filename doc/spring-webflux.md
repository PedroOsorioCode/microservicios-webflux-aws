# Instructivo paso a paso comprender conceptos Programación reactiva con Java Webflux
> A continuación se explica que es programación reactiva, webflux, características y explicaciones de los metodos mas usados.

### Indice

* [1. ¿Qué es programación reactiva?](#id1)
* [2. ¿Qué es webflux?](#id2)
* [3. Hilos en Java vs Modelo Reactivo con Spring WebFlux](#id3)
* [4. Construcción de hilos en JAVA](#id4)
* [5. Métodos mas usados en Mono](#id5)
* [6. Métodos mas usados en Flux](#id6)
* [7. Ejemplos Mono y Flux](#id7)

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

## Conceptos generales

1. **¿Qué es un Mono?:** Mono<T> representa 0 o 1 elemento que será emitido en el futuro (de forma asíncrona y no bloqueante); Mono es ideal para flujos reactivos que devuelven un solo valor; Puedes encadenar todos estos métodos para construir pipelines potentes y controlados.
2. **¿Qué es un Flux?:** Flux<T> representa una secuencia reactiva de 0 a N elementos (puede ser vacía o infinita), se usa ampliamente en Spring WebFlux para manejar múltiples elementos de forma asíncrona y no bloqueante; Flux se usa cuando tienes varios elementos en tu flujo reactivo; Es común en streams de base de datos, respuestas de APIs, WebSockets, etc.

# <div id='id3'/>
## 3. Hilos en Java vs Modelo Reactivo con Spring WebFlux

Un hilo (Thread) es una unidad de ejecución independiente dentro de un proceso. En Java, cada solicitud o tarea puede ejecutarse en su propio hilo. Los hilos tradicionales son parte del modelo de concurrencia imperativo y bloqueante, donde:

Cada operación que involucra IO (acceso a red, base de datos, disco, etc.) bloquea el hilo hasta que termine.

Para manejar múltiples peticiones concurrentes, se crean múltiples hilos, cada uno esperando su turno.

## ⚙️ Características de los hilos tradicionales en Java

| Característica         | Hilos en Java (servlets clásicos)        |
| ---------------------- | ---------------------------------------- |
| Modelo de ejecución    | Por cada solicitud, un hilo              |
| Manejo de concurrencia | Pool de hilos (por ejemplo, Tomcat)      |
| Consumo de recursos    | Alto (cada hilo ocupa memoria y CPU)     |
| Escalabilidad          | Limitada (cuello de botella con IO)      |
| Ejemplo clásico        | Spring MVC, servlets, ThreadPoolExecutor |
| Latencia frente a IO   | Alta (bloqueo mientras espera respuesta) |

## ⚡¿Qué propone el modelo reactivo de Spring WebFlux?

Spring WebFlux se basa en el modelo reactivo no bloqueante, usando internamente Reactor (Mono y Flux) y el estándar Reactive Streams. No asigna un hilo por solicitud, sino que:

Usa pocos hilos (event loop) para manejar muchas conexiones simultáneas.

No bloquea hilos en operaciones IO. En lugar de esperar, registra una función callback que se ejecuta cuando la respuesta esté lista.

Ideal para aplicaciones con altas cargas concurrentes y uso intensivo de IO.

## ⚙️ Características del modelo reactivo con WebFlux

| Característica       | Spring WebFlux (reactivo, no bloqueante)      |
| -------------------- | --------------------------------------------- |
| Modelo de ejecución  | Basado en eventos, sin bloqueo                |
| Hilos utilizados     | Pocos hilos (Netty event loops)               |
| Consumo de recursos  | Bajo, eficiente en memoria y CPU              |
| Escalabilidad        | Alta (decenas de miles de conexiones activas) |
| Ejemplo clásico      | Mono, Flux, WebClient, RouterFunction         |
| Latencia frente a IO | Baja (no bloquea, se suspende la operación)   |

## 🧪 Comparativo práctico

| Aspecto                     | Hilos tradicionales (Spring MVC) | Reactivo (Spring WebFlux)        |
| --------------------------- | -------------------------------- | -------------------------------- |
| Modelo                      | Imperativo, bloqueante           | Declarativo, no bloqueante       |
| Hilo por petición           | Sí                               | No                               |
| Escalabilidad               | Media                            | Alta                             |
| Manejo de IO lento (DB/API) | Bloquea el hilo                  | Libera el hilo (callback)        |
| Pool de hilos               | Requiere gran tamaño             | Tamaño pequeño, controlado       |
| Flujo de datos              | Paso a paso                      | Flujo reactivo (`Mono` / `Flux`) |
| Ideal para                  | CPU intensivo, lógica simple     | IO intensivo, muchas peticiones  |

## 🎯 Comparación visual (simplificada):

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

## ¿Qué se puede configurar en un hilo?

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

- Ejemplo Comparativo JAVA vrs Webflux, en un proyecto java con webflux puede crearse la siguiente clasa para validar respuestas y comparar rendimiento. vamos a simular 100 peticiones, en java se usarán 3 hilos

    - Resumen:
    - Java con 3 hilos procesa 100 peticiones con espera de 0.3 segundos cada hilo en aproximadamente 11 segundos
    - Java con 10 hilos procesa 100 peticiones con espera de 0.3 segundos cada hilo en aproximadamente 4 segundos
    - Webflux procesa 100 peticiones con espera de 0.3 segundos cada hilo en 0.5 segundos

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
## 5. Métodos mas usados en Mono

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

# <div id='id6'/>
## 6. Métodos mas usados en Flux

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

# <div id='id7'/>
## 7. Ejemplo combinando funciones con un flujo Mono
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

[< Volver al índice](../README.md)

---

💡 Esta documentación fue elaborada con ayuda de ChatGPT, basado en mis consultas técnicas

⚠️ Este contenido no puede ser usado con fines comerciales. Ver [LICENSE.md](../LICENSE.md)