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



## Métodos mas usados