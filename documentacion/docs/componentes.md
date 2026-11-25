# Crear juegos con vistas compuestas

Cada pantalla combina controles de Android y vistas compuestas declaradas por su nombre de clase en XML. Los atributos configuran el contenido fijo; las funciones públicas comunican acciones y muestran el estado.

## Componentes compartidos

| Componente en `compartido/ui/` | Configuración y acciones |
|---|---|
| `TarjetaJuegoView` | `tituloJuego`, `tintaTarjeta`, `indicePortada` y `cardBackgroundColor`. `alJugar` comunica la selección. |
| `EncabezadoJuegoView` | `mostrarReiniciar`. `alVolver` y `alReiniciar` ocultan los botones internos a la actividad. |
| `PantallaPartidaView` | Hereda de `ConstraintLayout`. Configura `tituloPartida`, `instruccionesPartida`, `lemaPartida`, `gestoPartida` y `acentoPartida` en el XML de cada juego nuevo. |
| `IndicadoresJuegoView` | Dos tarjetas de estado con `acentoBloque` y `tamanoIndicador`. `mostrar` actualiza sus valores y `describirDerecha` aporta una descripción accesible. |
| `AyudaJuegoView` | Tarjeta de instrucciones con `textoAyuda` y `acentoBloque`. |
| `TableroArcadeView` | Infla filas y casillas con View Binding y comunica toques, pulsaciones largas, dobles toques, trazos y direcciones. `mostrar` actualiza las casillas existentes. |

`PantallaPartidaView` compone encabezado, indicadores, tablero y ayuda sin duplicarlos por juego. Su propiedad `tablero` permite conectar las acciones del componente; no expone el View Binding de la pantalla ni sus botones internos. El botón de siguiente nivel se declara en XML y solo se muestra cuando corresponde en Petdoku.

KenKen usa el mismo encabezado ilustrado, los indicadores y la ayuda. Mantiene `TableroKenKenView` y `ControlesKenKenView` en su propio paquete para representar las jaulas y los tres modos de entrada.

El catálogo declara cinco instancias de `TarjetaJuegoView` con atributos y recursos fijos; no crea tarjetas desde Kotlin ni necesita un adaptador.

## Al crear o ampliar un juego

1. Usar `ConstraintLayout` directamente o una vista compuesta que herede de él.
2. Declarar los bloques por su nombre completo de clase, sin `<include>`. Inflar el XML interno con View Binding.
3. Configurar textos y colores fijos con recursos y atributos XML. La actividad conecta acciones mediante funciones públicas, sin conocer los botones internos.
4. Pasar las jugadas al `ViewModel`. Las vistas no deciden victorias, fusiones, minas ni validez de una mascota.
5. Inflar filas y casillas una vez y actualizar solo su estado. Las filas son `LinearLayout` horizontales dentro de otro vertical; no usar `GridLayout`.
6. Mantener las particularidades del renderizado actual de Snake en `Canvas`, incluido el movimiento interpolado.
7. Conservar el contrato de guardado descrito en [Estructura](estructura.md) al reorganizar componentes.

`inflate()` pertenece a las clases generadas por View Binding; los controles y `Canvas` son del SDK de Android. `ConstraintLayout`, los auxiliares de AndroidX Core y Material Components son las dependencias existentes. No se añaden bibliotecas para estos componentes.
