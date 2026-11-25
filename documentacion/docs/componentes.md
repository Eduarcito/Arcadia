# Crear juegos con vistas compuestas

En Arcadia, cada pantalla de juego se construye en XML combinando controles de Android y **vistas compuestas**. Una vista compuesta reúne varias vistas en un componente que cualquier layout puede declarar con una etiqueta propia. Sus atributos XML cumplen un papel parecido al de las *props* de React: el diseño se define una vez y cada instancia recibe su contenido.

## Componentes de referencia

| Componente | Qué configura el XML | Qué conecta la `Activity` |
|---|---|---|
| `TarjetaJuegoView` | `app:tituloJuego`, `app:descripcionJuego` y `app:cardBackgroundColor` | `alJugar { … }` abre el juego correspondiente. |
| `EncabezadoJuegoView` | `app:tituloEncabezado`, `app:mostrarReiniciar` y `app:mostrarTitulo` (opcionales) | `alVolver { finish() }` cierra la pantalla; `alReiniciar { … }` reinicia la partida. |
| `TableroKenKenView` | Su plantilla y las plantillas de fila y casilla | `alSeleccionarCasilla { fila, columna -> … }` comunica el toque; `mostrarJaulas()` y `mostrarEstado()` representan los datos. |
| `ControlesKenKenView` | Tres botones de modo con selección única | `alCambiarModo { … }` comunica el modo; `mostrarModo()` y `habilitar()` actualizan los controles. |

El [catálogo](../../app/src/main/res/layout/activity_main.xml) muestra tres instancias de la misma tarjeta. Los tres juegos comparten el mismo encabezado: en el [layout de KenKen](../../app/src/main/res/layout/activity_kenken.xml), por ejemplo, el título se declara como `app:tituloEncabezado="@string/juego_kenken"`. Para cambiar el diseño de **Volver** en los tres juegos, se modifica una sola vez [encabezado_juego.xml](../../app/src/main/res/layout/encabezado_juego.xml).

## Al crear o ampliar un juego

1. **Compón la pantalla.** Usa `ConstraintLayout` como raíz, declara `EncabezadoJuegoView` por su nombre completo de clase y añade el tablero y los controles del juego. Usa el mismo componente cuando necesites el mismo bloque visual. Para un botón o un texto aislado basta con el control estándar.
2. **Define cada bloque reutilizable una sola vez.** Declara su contenido en `res/layout/` y crea una clase Kotlin que lo infle con View Binding. Si el bloque cambia entre instancias, declara sus atributos en un `declare-styleable` de `res/values/`. Coloca la clase en el paquete del juego si solo la usa ese juego, o en `compartido/ui/` si se comparte. No copies su XML interno en cada pantalla ni uses `<include>`.
3. **Separa las acciones de las reglas.** El componente expone acciones mediante funciones como `alJugar` y `alVolver`; la `Activity` las conecta sin acceder a sus botones internos. En una jugada, la `Activity` pasa la acción al `ViewModel` y muestra el estado resultante. Por ejemplo, en Buscaminas la casilla comunica la pulsación; determinar si hay una mina y qué casillas se abren corresponde a la lógica de la partida.
4. **Distingue contenido fijo de estado de juego.** Títulos, instrucciones y colores iniciales van en recursos y atributos XML para que se vean en **Design** tras compilar el módulo. El puntaje, las fichas y las banderas se actualizan durante la partida. Para construir el tablero, infla las plantillas XML de fila y casilla con View Binding una vez y actualiza las casillas existentes después de cada jugada. El [ejemplo de KenKen](sintaxis.md#2-construir-un-tablero-con-constraintlayout-y-linearlayout) muestra el patrón.

Una vista compuesta se puede reutilizar sin un adaptador. Un `RecyclerView.Adapter` sirve para conectar una lista de datos con filas reciclables; no es necesario para las tres tarjetas fijas del catálogo ni para los tableros definidos en estas guías.
