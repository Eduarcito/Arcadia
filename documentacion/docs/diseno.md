# Diseño visual

La referencia actual es el catálogo ilustrado y las pantallas de 2048, Buscaminas, Petdoku y Snake. Mantener su apariencia durante las adaptaciones estructurales. KenKen adopta el mismo lenguaje visual sin cambiar sus reglas; sus controles inferiores usan iconos y etiquetas en tarjetas independientes.

## Tema y catálogo

- Usar Material Components para vistas XML, con tema base `Theme.Material3.DayNight.NoActionBar`, sin Compose.
- El catálogo y los cinco juegos nuevos usan `Theme.Arcade`, colores claros y oscuros definidos en recursos y los ajustes locales existentes.
- KenKen usa el mismo `Theme.Arcade` y el modo claro u oscuro global mediante `TemaArcadeActivity`, sin añadir audio ni vibración. Sus colores de casillas, texto, selección y controles tienen variantes nocturnas.
- Mantener las seis tarjetas ilustradas en dos columnas, esquinas redondeadas, contorno y textos configurados desde XML mediante `TarjetaJuegoView`.
- Conservar el menú lateral con **Mis juegos** y **Ajustes** y el icono adaptativo.

## Pantallas de juego

- Usar `ConstraintLayout`, directamente o mediante `PantallaPartidaView`. El encabezado compartido muestra volver, la marca y el reinicio opcional.
- Mostrar lema, título, tarjetas de indicadores, instrucción de interacción, tablero y mensaje de estado. Reutilizar `IndicadoresJuegoView` y `AyudaJuegoView`.
- En los cinco juegos nuevos, configurar título, instrucciones, lema, gesto y acento desde atributos XML de `PantallaPartidaView`.
- 2048, Buscaminas, Snake y Number Connect usan encabezado compacto, tablero ajustado al espacio disponible y ayuda desplegable mediante **?**, sin botones direccionales. Buscaminas incluye selector de descubrir/bandera, contador y una pista por partida, con casillas cerradas blancas y abiertas transparentes.
- Construir los tableros de casillas con filas horizontales en un contenedor vertical. Repartir las filas y casillas con pesos; mantener el tablero centrado y sin desplazamiento horizontal. Number Connect conserva la proporción 5:7; los demás tableros son cuadrados.
- Snake conserva el dibujo interpolado en `Canvas`. Los gestos y las acciones accesibles siguen funcionando.
- En 2048, Buscaminas, Snake y Number Connect, reducir el tablero para que quepa completo en el alto disponible, sin desplazamiento vertical.

## KenKen

- Compartir encabezado, tarjetas informativas y ayuda con los otros juegos. Usar acentos lavanda, tablero con marco redondeado y casillas de fondo suave con relieve.
- Los indicadores muestran casillas completadas y modo activo; no añaden reglas ni condiciones de victoria.
- Conservar las pistas y las fronteras continuas de las jaulas: líneas internas finas y bordes de jaula más gruesos. No separar casillas con márgenes que rompan las jaulas.
- Mantener selección visible con fondo y contorno. Los controles son tres tarjetas redondeadas de 88 dp, con iconos vectoriales de trazo redondeado y las etiquetas **Sumar**, **Restar** y **Borrar**. Solo una permanece activa; conserva también su estado accesible de selección.
- **Comprobar** es la acción principal, con icono de escudo y verificación, colores adaptados al tema y estado deshabilitado al resolver el tablero.
- Mantener el reinicio inmediato y la elección de variantes existentes, sin añadir un diálogo de confirmación.
- Conservar las microinteracciones nativas existentes: 160 ms al cambiar modo o valor, 180 ms en el mensaje de comprobación y 260 ms al confirmar victoria. Respetar las animaciones desactivadas del sistema.

## Recursos y accesibilidad

Los textos fijos van en `strings.xml` o archivos `strings_*`, colores en recursos `values`, tamaños compartidos en `dimens_*` y formas en `drawable`. Los atributos de las vistas compuestas configuran el contenido fijo y permiten previsualizarlo después de compilar.

Mantener descripciones accesibles para iconos, casillas y controles. Comunicar errores y selección con algo más que el color. Las vistas no contienen reglas ni modifican la partida por sí mismas.
- Los títulos de pantalla se marcan como encabezados de accesibilidad con `ViewCompat` (cubre API 26–27) y los botones de icono responden al tacto con ripple y los mensajes de estado de partida se anuncian solos a TalkBack. En KenKen, cada cambio de valor en una casilla se anuncia con su fila y columna.
- Las marcas de los juegos (mascota, bandera, mina) son vectores propios (`ic_mascota`, `ic_bandera`, `ic_mina`), no emojis, para que se vean igual en todos los dispositivos.
- El tema se elige entre sistema, claro y oscuro; el mínimo de texto es 11sp.
- Snake entra en modo paso a paso cuando TalkBack está activo: no hay temporizador, cada deslizamiento avanza una casilla y tocar el tablero sigue recto; tras cada paso se anuncia la dirección y distancia de la fruta y el peligro inmediato.
- En pantallas anchas el contenido se centra con un tope de 600dp (`ancho_contenido_max`); no se estiran tarjetas ni textos en tabletas, horizontal o multiventana.

## Mascota y suma en vivo

Petdoku tiene siete ilustraciones transparentes `drawable-nodpi/petdoku_*.png`, que se alternan entre los doce niveles. `MascotasPetdoku` asigna perrito, gatito, conejito, zorrito, panda, tigre y osito, en ese orden. El tablero y las reglas muestran la misma mascota; reiniciar y recuperar la partida conservan la del nivel guardado. El catálogo mantiene el perrito. `CasillaIlustradaView` escala la imagen sin cambiar las descripciones accesibles ni los gestos.

Number Connect reserva una tarjeta de 64 dp encima del tablero para mostrar la suma del trazo y la ficha final. Se actualiza al arrastrar y retroceder; al cancelar o soltar vuelve a su estado inicial. El espacio permanece reservado para evitar que el tablero se mueva durante el gesto.

