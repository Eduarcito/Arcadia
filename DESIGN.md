---
name: Arcadia
description: Cinco juegos de lógica y arcade sin conexión, en un sistema cálido de papel, tinta suave y tableros pastel.
colors:
  papel-claro: "#F3EEE8"
  superficie-clara: "#FFFFFF"
  tinta-clara: "#28232D"
  apagada-clara: "#665E6B"
  papel-oscuro: "#19191C"
  superficie-oscura: "#252428"
  tinta-oscura: "#FFF9F3"
  apagada-oscura: "#B8AFBC"
  acento-morado: "#8E529F"
  acento-contenedor-claro: "#EBD4ED"
  acento-contenedor-oscuro: "#482353"
  acento-tinta-contenedor: "#482353"
  noche-icono: "#211829"
typography:
  display:
    fontFamily: "sans-serif-black"
    fontSize: "38sp"
  titulo:
    fontFamily: "sans-serif-black"
    fontSize: "23sp"
  subtitulo:
    fontFamily: "sans-serif-medium"
    fontSize: "16sp"
  cuerpo:
    fontFamily: "sans-serif"
    fontSize: "14sp"
  ayuda:
    fontFamily: "sans-serif"
    fontSize: "13sp"
  pista:
    fontFamily: "sans-serif"
    fontSize: "13sp"
  leyenda:
    fontFamily: "sans-serif"
    fontSize: "11sp"
    letterSpacing: "0.08em"
rounded:
  tarjeta: "24dp"
  celda: "7dp"
  tablero: "14dp"
spacing:
  s: "12dp"
  m: "18dp"
  l: "24dp"
components:
  tarjeta-juego:
    backgroundColor: "{colors.superficie-clara}"
    textColor: "{colors.tinta-clara}"
    rounded: "{rounded.tarjeta}"
    padding: "18dp"
  indicador-estadistica:
    backgroundColor: "{colors.superficie-clara}"
    textColor: "{colors.tinta-clara}"
    rounded: "{rounded.tarjeta}"
    padding: "18dp"
---

# Sistema de diseño: Arcadia

## Overview

**Estrella creativa: "La mesa de juegos de la abuela"**

Arcadia es una mesa de madera cubierta de papel crema donde los juegos de siempre
esperan su turno. Las tarjetas del catálogo son pequeñas ilustraciones pintadas a
mano; los tableros son piezas de fieltro y cartulina en colores pastel, con bordes
redondeados y trazos finos. Nada brilla ni parpadea: la calidez viene del papel, de la
tinta ciruela y de las paletas suaves de cada juego. El modo oscuro es la misma mesa
de noche, con la lámpara baja: los fondos se hunden a grises cálidos y las tintas se
aclaran sin perder el carácter.

Los componentes se sienten **táctiles y seguros**: fichas con peso, toques con
respuesta inmediata, casillas que parecen piezas de un juego de mesa. La expresión vive
en las ilustraciones de portada y en las paletas de cada tablero; la estructura
(cabeceras, tarjetas de estadísticas, ayuda) es precisa y silenciosa.

**Características clave:**
- Papel crema de día, gris cálido de noche; nunca negro puro ni blanco frío.
- Un solo acento morado (`#8E529F`) reservado para controles y marca.
- Cada juego posee su propia paleta de tablero y de portada; no se mezclan.
- Esquinas generosas (24dp en tarjetas), trazos finos (1–3dp), elevación cero.
- Textos en español, en mayúsculas pequeñas para leyendas y en negro redondeado para títulos.

## Colors

La paleta es de papel y tinta: neutrales cálidos dominan cada pantalla y el color de
cada juego aparece solo dentro de su tablero y su portada.

### Primary
- **Morado ciruela** (`#8E529F`): único acento de marca y control — botones, slider,
  interruptores, ripple de tarjetas (`#338E529F`). Su rareza es intencional.
- **Contenedor morado claro** (`#EBD4ED`) y **contenedor oscuro** (`#482353`): fondos
  de las tarjetas de estadísticas, con tinta `#482353` de día y clara de noche.

### Neutral
- **Papel crema** (`#F3EEE8`): fondo de día en todas las pantallas.
- **Blanco hueso** (`#FFFFFF`): superficies de tarjetas de día.
- **Tinta ciruela oscura** (`#28232D`): texto principal de día.
- **Tinta apagada** (`#665E6B`): texto secundario, pistas y leyendas de día.
- **Papel noche** (`#19191C`): fondo de noche — nunca negro puro.
- **Superficie noche** (`#252428`): tarjetas de noche.
- **Tinta cálida** (`#FFF9F3`): texto principal de noche.
- **Tinta apagada noche** (`#B8AFBC`): texto secundario de noche.

### Reglas nombradas
**La regla de la única voz.** El morado `#8E529F` cubre menos del 10% de cualquier
pantalla. Habla una vez por pantalla: el control principal o la marca.

**La regla del tablero soberano.** Dentro de cada juego mandan sus colores de tablero
(`colores_tableros.xml`): 2048 cálidos, buscaminas verdosos, petdoku pastel, snake
verde bosque, kenken lavanda. Los neutrales de la app no invaden el tablero, y los
colores del tablero no salen de él salvo en su portada del catálogo.

## Typography

**Títulos:** sans-serif-black (con sistema como respaldo)
**Cuerpo:** sans-serif
**Énfasis medio:** sans-serif-medium

**Carácter:** la redondez del negro del sistema evoca letra de cartel de juego de
mesa; el cuerpo regular mantiene las instrucciones legibles en sesiones cortas.

### Jerarquía
- **Display** (black, 38sp): nombre del juego en su cabecera.
- **Título** (black, 21–27sp): títulos de pantalla (ajustes) y marca ARCADIA (25sp).
- **Subtítulo** (medium, 16sp): colección y mensajes de estado de partida.
- **Cuerpo** (regular, 14sp): instrucciones de ayuda y notas.
- **Ayuda** (regular, 13sp): textos de gestos y apoyo.
- **Pista** (regular, 13sp): frases de ambiente bajo el tablero.
- **Leyenda** (regular, 11sp, versalitas con tracking): rótulos de estadísticas
  (PUNTOS, RÉCORD) y eslóganes de tarjeta, siempre en mayúsculas.

## Layout

Pantallas con ConstraintLayout y márgenes de sistema aplicados por insets. El catálogo
es una cuadrícula de dos columnas de tarjetas. Las partidas comparten una armadura:
cabecera de juego arriba (volver, marca de puntos, reinicio, ajustes), fila de
tarjetas de estadísticas, tablero cuadrado centrado (tope 460dp) y pista al pie.
Ritmo de espaciado en múltiplos de 6dp: 12/18/24dp dominan. El tablero se mide a lo
ancho disponible y recorta su altura; nunca se estira en horizontal.

## Elevation & Depth

Sistema plano por capas tonales, sin sombras (`cardElevation` 0dp). La jerarquía se
construye con papel sobre papel: fondo, superficie, contenedor morado. Los trazos
finos (1dp en casillas, 3dp en tarjetas del catálogo `#343237`) dibujan los límites.

## Shapes

- **Tarjetas del catálogo y de ayuda:** radio 24dp, trazo 3dp en catálogo.
- **Casillas de tablero:** radio 7dp (celdas), trazo 1dp.
- **Tablero:** radio 14dp, trazo semitransparente claro `#33FFFFFF`.
- Las ilustraciones de portada usan formas redondeadas y orgánicas; nada puntiagudo
  salvo la bandera de buscaminas.

## Components

- **Tarjeta de juego** (`TarjetaJuegoView`): portada ilustrada en código, título
  black, leyenda en versalitas, tinta y fondo propios de cada juego.
- **Indicador de estadística** (`IndicadoresJuegoView`): tarjeta de superficie con
  trazo fino teñido del acento de cada juego (`#EDBB63` 2048, `#66D2C6` buscaminas,
  `#EE94BD` petdoku, `#B4EB7C` snake), radio 20dp; rótulo en versalitas apagadas sobre
  valor numérico grande, que se aclara con el acento de noche y se oscurece de día.
- **Cabecera de juego** (`EncabezadoJuegoView`): botones redondos de retroceso,
  reinicio y ajustes flanqueando el nombre del juego.
- **Ayuda** (`AyudaJuegoView`): tarjeta "CÓMO JUGAR" con instrucciones en cuerpo.
- **Tableros** (`TableroArcadeView`, `SnakeCanvasView`, tablero KenKen): dibujados en
  canvas con las paletas de `colores_tableros.xml`; animaciones sujetas al ajuste del
  sistema de movimiento reducido.
- **Controles de ajustes:** slider y switches Material con acento morado.

## Do's and Don'ts

- Sí: mantener las paletas de tablero tal como están; son la identidad de cada juego.
- Sí: texto en español correcto, con tildes, en recursos.
- Sí: respetar modo oscuro, movimiento reducido y escalado de fuente en todo.
- No: mezclar colores de un juego en la pantalla de otro.
- No: añadir sombras, degradados fríos, negros puros o acentos nuevos fuera del morado.
- No: reducir textos por debajo de 11sp ni fijar tamaños en unidades distintas de sp.
- No: emojis ni glifos tipográficos como iconos de juego; se usan los vectores
  `ic_mascota`, `ic_bandera` e `ic_mina`, consistentes en todos los dispositivos.
- Sí: el tema ofrece Sistema/Claro/Oscuro; la preferencia `theme` manda y la clave
  `dark` heredada solo se consulta para migrar.
- No: iconos de cupertino ni patrones de iOS; la estructura sigue Material 3.
