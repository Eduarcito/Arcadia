# Arcadia

Aplicación Android en Kotlin con un catálogo de tres juegos. KenKen permite jugar
un tablero de prueba 4 × 4 con jaulas, comprobar la respuesta y reiniciar.
2048 y Buscaminas mantienen sus pantallas de desarrollo.

## Dónde trabajar

El código está en `app/src/main/java/com/example/arcadia/` y las vistas en `app/src/main/res/layout/`.

| Funcionalidad | Paquete o archivo Kotlin | Layout |
|---|---|---|
| Catálogo | `MainActivity.kt` | `activity_main.xml` |
| 2048 | `juegos/juego2048/` | `activity_juego_2048.xml` |
| Buscaminas | `juegos/buscaminas/` | `activity_buscaminas.xml` |
| KenKen | `juegos/kenken/` | `activity_kenken.xml` |

KenKen usa los componentes `TableroKenKenView` y `ControlesKenKenView`.
Los modos **Incrementar**, **Decrementar** y **Borrar** se aplican al tocar una
casilla. **Comprobar** distingue entre tablero incompleto, incorrecto y correcto;
solo una comprobación correcta bloquea la partida hasta reiniciar.
El rompecabezas fijo es temporal: la generación aleatoria corresponde a la siguiente etapa.

Las tarjetas y los encabezados son **vistas compuestas** en `compartido/ui/`.
Para reutilizarlos en otro layout, declara la etiqueta de `TarjetaJuegoView` como en
`activity_main.xml` o la de `EncabezadoJuegoView` como en `activity_kenken.xml`.
Cada instancia recibe su contenido mediante atributos XML. La pantalla decide
qué hacer al pulsar el botón.

Para crear o ampliar un juego, sigue la [guía de componentes](documentacion/docs/componentes.md).
Este patrón se aplica a 2048, Buscaminas y KenKen.

Al implementar una partida, coloca su `ViewModel` y sus reglas en el paquete del juego.
La `Activity` conecta los controles mediante View Binding y muestra el estado;
el `ViewModel` conserva la partida al rotar la pantalla. Escribe las reglas en Kotlin
sin referencias a vistas para poder probarlas en `app/src/test/`.

## Abrir y comprobar la base

1. Abre la carpeta raíz en Android Studio y sincroniza el proyecto con Gradle.
2. Usa el Gradle Wrapper del repositorio y el SDK de compilación indicado en `app/build.gradle.kts`.
3. Selecciona `app`, elige un dispositivo o emulador con Android 8.0 (API 26) o posterior y pulsa **Run**.
4. Desde el catálogo, abre cada juego y comprueba que **Volver al catálogo** y el botón Atrás del sistema regresan a la lista.
