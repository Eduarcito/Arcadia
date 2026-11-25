# Crear juegos con vistas compuestas

Cada pantalla combina controles de Android y vistas compuestas declaradas por su nombre de clase en XML. Los atributos configuran el contenido fijo; las funciones públicas comunican acciones y muestran el estado.

## Componentes compartidos

| Componente en `compartido/ui/` | Configuración y acciones |
|---|---|
| `TarjetaJuegoView` | `tituloJuego`, `tintaTarjeta`, `indicePortada` y `cardBackgroundColor`. `alJugar` comunica la selección. |
| `EncabezadoJuegoView` | `mostrarReiniciar`. `alVolver` y `alReiniciar` ocultan los botones internos a la actividad. |
| `PantallaPartidaView` | Hereda de `ConstraintLayout`. Configura `tituloPartida`, `instruccionesPartida`, `lemaPartida`, `gestoPartida` y `acentoPartida` en el XML de cada juego nuevo. |
| `IndicadoresJuegoView` | Dos tarjetas de estado centradas con `acentoBloque` y `tamanoIndicador`; la etiqueta ocupa el 70 % del tamaño del valor para no bajar de 11 sp. `mostrar` actualiza sus valores y `describirDerecha` aporta una descripción accesible. |
| `AyudaJuegoView` | Tarjeta de instrucciones con `textoAyuda` y `acentoBloque`. |
| `TableroArcadeView` | Infla filas y casillas con View Binding y comunica toques, pulsaciones largas, dobles toques, trazos y direcciones. `mostrar` actualiza las casillas existentes. |

`PantallaPartidaView` compone encabezado, indicadores, tablero y ayuda sin duplicarlos por juego. Su propiedad `tablero` permite conectar las acciones del componente; no expone el View Binding de la pantalla ni sus botones internos. Petdoku avanza de nivel automáticamente al ganar; el botón de siguiente nivel sigue declarado en XML pero oculto.

KenKen usa el mismo encabezado ilustrado, los indicadores y la ayuda. Mantiene `TableroKenKenView` y `ControlesKenKenView` en su propio paquete: el tablero representa las jaulas y el control es un teclado numérico que escribe en la casilla seleccionada.

El catálogo declara seis instancias de `TarjetaJuegoView` con atributos y recursos fijos; no crea tarjetas desde Kotlin ni necesita un adaptador. Las variantes anteriores de tarjeta y encabezado se conservan para no romper otros layouts.

Los seis juegos usan la misma distribución: `PantallaPartidaView.usarPantallaCompleta` (`contenido_partida_compacta.xml`), con marcadores en el encabezado junto a volver y reiniciar, nombre y gesto centrados, tablero ajustado a la altura disponible, herramientas inferiores y ayuda en diálogo desde el botón **?** del pie. Petdoku coloca su leyenda ilustrada en la previsualización y su selector de nivel en las herramientas; KenKen conecta `TableroKenKenView` como tablero alternativo y su teclado más **Comprobar** como herramientas.

`ControlesBuscaminasView`, declarado en `herramientas_buscaminas.xml`, encapsula contador, selector y pista. `TableroNumberConnectView`, declarado en `tablero_number_connect.xml`, infla filas y casillas XML; dibuja únicamente la línea del trazo en Canvas y consulta la validez al modelo.


## Al crear o ampliar un juego

1. Usar `ConstraintLayout` directamente o una vista compuesta que herede de él.
2. Declarar los bloques por su nombre completo de clase, sin `<include>`. Inflar el XML interno con View Binding.
3. Configurar textos y colores fijos con recursos y atributos XML. La actividad conecta acciones mediante funciones públicas, sin conocer los botones internos.
4. Pasar las jugadas al `ViewModel`. Las vistas no deciden victorias, fusiones, minas ni validez de una mascota.
5. Inflar filas y casillas una vez y actualizar solo su estado. Las filas son `LinearLayout` horizontales dentro de otro vertical; no usar `GridLayout`.
6. Mantener las particularidades del renderizado actual de Snake en `Canvas`, incluido el movimiento interpolado.
7. Conservar el contrato de guardado descrito en [Estructura](estructura.md) al reorganizar componentes.
8. Las acciones principales en la zona de herramientas usan `botonAccionArcade` (relleno redondeado con el acento del juego, texto en contraste por luminancia), el mismo estilo del botón **Comprobar** de KenKen.

`inflate()` pertenece a las clases generadas por View Binding; los controles y `Canvas` son del SDK de Android. `ConstraintLayout`, los auxiliares de AndroidX Core y Material Components son las dependencias existentes. No se añaden bibliotecas para estos componentes.
