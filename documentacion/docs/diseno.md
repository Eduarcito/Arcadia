# Diseño visual

La referencia actual es el catálogo ilustrado y las pantallas de 2048, Buscaminas, Petdoku y Snake. Mantener su apariencia durante las adaptaciones estructurales. KenKen adopta el mismo lenguaje visual sin cambiar sus reglas; sus controles inferiores usan iconos y etiquetas en tarjetas independientes.

## Tema y catálogo

- Usar Material Components para vistas XML, con tema base `Theme.Material3.DayNight.NoActionBar`, sin Compose.
- El catálogo y los cuatro juegos nuevos usan `Theme.Arcade`, colores claros y oscuros definidos en recursos y los ajustes locales existentes.
- KenKen usa el mismo `Theme.Arcade` y el modo claro u oscuro global mediante `TemaArcadeActivity`, sin añadir audio ni vibración. Sus colores de casillas, texto, selección y controles tienen variantes nocturnas.
- Mantener las cinco tarjetas ilustradas en dos columnas, esquinas redondeadas, contorno y textos configurados desde XML mediante `TarjetaJuegoView`.
- Conservar el menú lateral con **Mis juegos** y **Ajustes** y el icono adaptativo.

## Pantallas de juego

- Usar `ConstraintLayout`, directamente o mediante `PantallaPartidaView`. El encabezado compartido se declara con `estiloArcade="true"`: volver, marca y reinicio.
- Mostrar lema, título, tarjetas de indicadores, instrucción de interacción, tablero y mensaje de estado. Reutilizar `IndicadoresJuegoView` y `AyudaJuegoView`.
- En los cuatro juegos nuevos, configurar título, instrucciones, lema, gesto y acento desde atributos XML de `PantallaPartidaView`.
- Mantener los tamaños, márgenes, tipografía y colores existentes de esos cuatro juegos. No añadir botones direccionales ni selectores al refactorizar.
- Construir los tableros de casillas con filas horizontales en un contenedor vertical. Repartir las filas y casillas con pesos; mantener el tablero cuadrado, centrado y sin desplazamiento horizontal.
- Snake conserva el dibujo interpolado en `Canvas`. Los gestos y las acciones accesibles siguen funcionando.
- Admitir desplazamiento vertical cuando el contenido no quepa en teléfonos pequeños.

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
