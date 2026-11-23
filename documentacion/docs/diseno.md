# Diseño visual

Adaptar al teléfono la estética de [Arcadia](https://arcade-games-eosin.vercel.app/): fondo crema, texto verde oscuro, acentos suaves y tarjetas redondeadas. La referencia es visual; implementar las pantallas con vistas y recursos XML, sin copiar la estructura web ni cargar la página en la aplicación.

Usar **Material Components para vistas XML** (`com.google.android.material:material`) con `Theme.Material3.DayNight.NoActionBar` como tema base. Aplicar la paleta del juego mediante recursos, sin usar Material para Compose.

## Catálogo

- Mostrar el encabezado **«Juegos que estimulan el pensamiento»** y tres tarjetas en una sola columna: 2048, Buscaminas y KenKen.
- Declarar tres instancias de `TarjetaJuegoView` en `activity_main.xml`. La vista compuesta contiene el título, la descripción y el `MaterialButton` **Jugar**. Cada instancia configura sus textos y su color en XML para que se vean en la vista previa.
- No incorporar búsqueda, filtros, favoritos ni secciones adicionales.

## Pantallas de juego

- Usar `EncabezadoJuegoView` en los tres layouts. Cada pantalla indica su título en XML y conecta la acción **Volver** en Kotlin. Al implementar **Reiniciar**, añadir el botón al encabezado compartido y conectar la acción desde cada pantalla.
- Mostrar una instrucción breve, el tablero, los controles propios del juego y un mensaje de estado. 2048 muestra su puntaje; Buscaminas indica el modo activo; KenKen muestra si aún faltan valores o si hay un error al comprobar.
- Mantener el tablero cuadrado con una proporción `1:1` en `ConstraintLayout`, dentro del espacio disponible y sin desplazamiento horizontal. No fijar el ancho en `dp`.
- Dar el mismo peso a todas las filas del `LinearLayout` vertical y a todas las casillas de cada fila horizontal. Así se reparte por igual el alto entre filas y el ancho entre casillas.
- En 2048, mantener visible el `MaterialButton` **Controles** en ambos modos. Abrir el selector con `MaterialAlertDialogBuilder` e indicar el modo activo en la pantalla. En modo **Deslizar**, mostrar una instrucción breve en lugar de los botones direccionales. Si estos botones usan solo flechas, dar a cada uno una descripción accesible con su dirección.

## Recursos de estilo

| Recurso | Qué guardar |
|---|---|
| `strings.xml` | Títulos, botones, instrucciones, mensajes y textos con formato, como el puntaje. |
| `colors.xml` | Fondo crema `#F7F6F0`, texto verde oscuro `#173E31` y acento lima `#D6EE8C`. Usar ocre para 2048, verde azulado para Buscaminas y lavanda para KenKen. |
| `dimens.xml` | Márgenes, separaciones, esquinas y tamaños repetidos. Usar `dp` para dimensiones y `sp` para texto. |
| `styles.xml` y `themes.xml` | Tipografía del sistema, jerarquía de títulos y estilos comunes para tarjetas, botones y textos. |
| `drawable/` | Fondos redondeados y bordes de selección o de jaula. |

Usar contraste suficiente y distinguir los estados por algo más que el color. Por ejemplo, una bandera debe tener un símbolo y una descripción; una casilla seleccionada de KenKen debe mostrar un borde. Dar nombres accesibles a los controles que solo muestran un icono y dejar espacio suficiente para tocarlos.
