# Estructura del proyecto

Organizar el código por funcionalidad dentro de un único módulo `app`. Las rutas Kotlin son relativas a `app/src/main/java/com/example/arcadia/`.

## Código Kotlin

| Ruta | Responsabilidad |
|---|---|
| `MainActivity.kt` | Catálogo: conecta las tarjetas XML con cada juego y controla el menú lateral. |
| `ajustes/AjustesActivity.kt` | Ajustes de volumen, sonido, música, vibración y modo oscuro. |
| `juegos/juego2048/` | `Juego2048Activity`, `Juego2048ViewModel` y `Reglas2048`. |
| `juegos/buscaminas/` | `BuscaminasActivity`, `BuscaminasViewModel` y `ReglasBuscaminas`. |
| `juegos/petdoku/` | `PetdokuActivity`, `PetdokuViewModel` y `ReglasPetdoku`, con sus niveles. |
| `juegos/numberconnect/` | `NumberConnectActivity`, `NumberConnectViewModel`, `ReglasNumberConnect` y tablero táctil. |
| `juegos/snake/` | `SnakeActivity`, `SnakeViewModel` y `ReglasSnake`. |
| `juegos/kenken/` | Pantalla, estado, reglas, rompecabezas y componentes exclusivos de KenKen. |
| `compartido/ui/` | Tarjetas, encabezado, indicadores, ayuda, pantalla de partida, tableros y detector de gestos compartidos. |
| `compartido/estado/` | `Direccion` y `PartidaViewModel`, con la serialización y el guardado existentes. |

Cada juego mantiene su `Activity`, su `ViewModel` y sus reglas en su paquete. Los juegos no importan reglas de otros juegos: `Direccion` es un tipo compartido.

`TemaArcadeActivity` aplica el tema global, los márgenes y las barras del sistema a todas las pantallas. `ArcadeActivity` hereda de ella y agrega el audio y la vibración existentes del catálogo y de los cinco juegos nuevos. `PartidaArcadeActivity` conecta las acciones comunes de esas partidas. KenKen hereda directamente de `TemaArcadeActivity`, comparte el modo claro u oscuro y conserva sus reglas sin añadir audio ni vibración.

Registrar las ocho actividades en `AndroidManifest.xml`. Solo el catálogo es la actividad de inicio. Volver con Atrás o `finish()` sin abrir otro catálogo encima.

## Recursos Android

| Ruta bajo `res/` | Contenido |
|---|---|
| `layout/activity_main.xml` | Catálogo con seis `TarjetaJuegoView` fijas en dos columnas y menú lateral. |
| `layout/activity_juego_2048.xml`, `activity_buscaminas.xml`, `activity_petdoku.xml`, `activity_snake.xml`, `activity_number_connect.xml` | Instancias de `PantallaPartidaView`, subclase de `ConstraintLayout`, con título, instrucciones, lema, gesto y acento configurados por atributos. |
| `layout/activity_arcade_game.xml` | Plantilla interna de `PantallaPartidaView`, no una actividad adicional. |
| `layout/activity_kenken.xml` | Pantalla de KenKen con encabezado, indicadores y ayuda compartidos, más tablero y controles propios. |
| `layout/activity_arcade_settings.xml` | Pantalla de ajustes. |
| `layout/fila_tablero.xml` | Fila horizontal compartida, con peso para repartir el alto. |
| `layout/tablero_arcade.xml`, `celda_arcade.xml` | Contenedor vertical y casillas de 2048, Buscaminas y Petdoku. |
| `layout/tablero_kenken.xml`, `celda_kenken.xml` | Contenedor vertical y casillas con pistas y bordes de jaula. |
| `layout/encabezado_juego.xml`, `encabezado_arcade.xml` | Variantes internas de `EncabezadoJuegoView`. |
| `layout/contenido_tarjeta_juego.xml`, `tarjeta_arcade.xml` | Variantes internas de `TarjetaJuegoView`. |
| `layout/indicadores_juego.xml`, `ayuda_juego.xml` | Bloques compartidos de indicadores y ayuda. |
| `values/` | Textos, atributos, colores, tamaños, estilos y temas. |
| `drawable/` | Fondos, bordes y estados visuales. |

Declarar los componentes por su nombre completo de clase, sin `<include>`. Inflar sus plantillas con View Binding. Construir las filas y casillas una vez y actualizar las existentes después de cada jugada. Snake conserva su renderizado en `Canvas` dentro del tablero compuesto.

## Responsabilidades y guardado

- La `Activity` recibe acciones, llama al `ViewModel` y representa el estado; no aplica reglas directamente sobre el tablero.
- El `ViewModel` conserva la partida al rotar y delega las reglas a funciones Kotlin sin vistas.
- `PartidaViewModel.cargar()` restaura una vez por instancia. `guardar()` escribe el estado como JSON en preferencias privadas, sin cuentas, red ni base de datos.
- Las preferencias `arcade_games` conservan las claves `2048`, `mines`, `petdoku`, `snake` y `number_connect`. No cambiar nombres de archivo, claves JSON, orden de `Direccion` ni formato durante una refactorización.
- Salir y volver a abrir recupera la partida de esos cinco juegos. Snake se pausa al salir y elimina sus callbacks; no avanza en segundo plano.
- Reiniciar comienza otra partida y conserva los récords. Petdoku conserva nivel, marcas y vidas al salir.
- KenKen no usa `arcade_games`: rotar conserva su estado y volver a abrirlo elige otra partida de su catálogo predefinido.
- Los ajustes usan `arcade_settings`, separados de las partidas. El tema se aplica también a KenKen y se actualiza al volver a la pantalla; sus ajustes de audio y vibración siguen sin aplicarse a ese juego.
