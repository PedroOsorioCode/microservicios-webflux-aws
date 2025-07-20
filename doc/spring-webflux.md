# Instructivo paso a paso comprender conceptos Programación reactiva con Java Webflux
> A continuación se explica que es programación reactiva, webflux, características y explicaciones de los metodos mas usados.

## ¿Qué es programación reactiva?
> La programación reactiva es un paradigma de programación que se enfoca en manejar flujos de datos asíncronos y la propagación de cambios. En lugar de escribir código que ejecuta instrucciones paso a paso (como en la programación imperativa), en la programación reactiva defines cómo deben reaccionar tus aplicaciones a ciertos eventos o cambios de estado.

### Características principales

1. **Asincronía:** permite manejar tareas que toman tiempo (como peticiones a bases de datos o servicios web) sin bloquear el hilo principal.

2. **Flujos de datos (Streams):** los datos se modelan como flujos que pueden ser observados y transformados (como si fueran una secuencia de eventos en el tiempo)

### Ventajas
- Escalabilidad y eficiencia en aplicaciones con alta concurrencia.
- Código más declarativo y expresivo para manejar eventos, errores y asincronía.
- Mejor rendimiento en aplicaciones de tiempo real o intensivas en I/O.

## ¿Qué es Webflux?
> Spring WebFlux es un módulo del ecosistema Spring Framework diseñado para construir aplicaciones web reactivas, es decir, aplicaciones que manejan peticiones de forma asíncrona y no bloqueante.

### Características principales de WebFlux:

1. **No bloqueante y reactivo:** basado en el paradigma de programación reactiva usando Reactor, una implementación de la especificación Reactive Streams.

2. **Escalable:** al no bloquear los hilos, puede manejar muchas peticiones concurrentes con menos recursos, ideal para aplicaciones con alta carga o servicios que dependen de muchas llamadas a servicios externos.

3. **Soporte para R2DBC:** para acceso reactivo a bases de datos relacionales (como MySQL, PostgreSQL, H2, MariaDB, Microsoft SQL Server, Oracle Database, IBM Db2, CockroachDB, Apache Derby) permite conexión no bloqueante

## Conceptos generales

1. **¿Qué es un Mono?:** Mono<T> representa 0 o 1 elemento que será emitido en el futuro (de forma asíncrona y no bloqueante); Mono es ideal para flujos reactivos que devuelven un solo valor; Puedes encadenar todos estos métodos para construir pipelines potentes y controlados.
2. **¿Qué es un Flux?:** Flux<T> representa una secuencia reactiva de 0 a N elementos (puede ser vacía o infinita), se usa ampliamente en Spring WebFlux para manejar múltiples elementos de forma asíncrona y no bloqueante; Flux se usa cuando tienes varios elementos en tu flujo reactivo; Es común en streams de base de datos, respuestas de APIs, WebSockets, etc.

## Métodos mas usados en Mono

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

## Métodos mas usados en Flux

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

## Ejemplo combinando funciones con un flujo Mono
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