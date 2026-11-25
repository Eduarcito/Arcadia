# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

Arcadia es un proyecto de aprendizaje y portafolio. Sus jugadores son sobre todo el
propio autor, sus compañeros y quienes revisan el código; no hay distribución pública
ni métricas de negocio. Las decisiones de producto se toman para demostrar oficio en
Android nativo, no para crecer una base de usuarios.

## Product Purpose

Aplicación Android en Kotlin con cinco juegos de lógica y arcade (2048, Buscaminas,
Petdoku, Snake y KenKen) que funcionan sin conexión, con catálogo, ajustes y guardado
automático de partidas. El éxito es que cada pantalla demuestre craft: interacciones
cuidadas, accesibilidad real y una base de código que otra persona pueda continuar
siguiendo `documentacion/`.

## Positioning

Cinco juegos completos sin conexión, sin cuentas, sin anuncios y sin permisos
especiales, con reglas probadas en pruebas unitarias y un sistema de componentes
compartidos documentado en español. Un clon de arcade comercial no podría copiar
esa combinación sin mentir sobre su modelo de negocio.

## Operating Context

Uso en sesiones cortas, con una mano y sin conexión (transporte, esperas). El proyecto
se continúa a través de `documentacion/AGENTS.md` y sus guías; cualquier cambio debe
respetarlas o actualizarlas. Las partidas de 2048, Buscaminas, Petdoku y Snake se
guardan en JSON en preferencias privadas y su formato no puede cambiar sin migración.

## Capabilities and Constraints

- Sin Internet, sin cuentas, sin base de datos; un solo módulo `app`, Kotlin, vistas
  XML y View Binding. Sin Compose ni motores de juego.
- Los controles de cada juego son fijos por decisión de diseño: gestos en 2048, toque
  y pulsación larga en Buscaminas, toque simple/doble/arrastre en Petdoku,
  deslizamiento continuo y toque de pausa en Snake, modos de entrada en KenKen.
- El catálogo no crece por ahora: la ambición confirmada es pulir los cinco juegos
  (UX, accesibilidad, rendimiento) antes que añadir más.
- La documentación del proyecto en `documentacion/` es normativa y se mantiene
  alineada con la implementación.

## Brand Commitments

Nombre Arcadia, catálogo de tarjetas ilustradas dibujadas en código, melodía original
incluida, tono cercano y en español. Los textos visibles respetan la ortografía del
español; el código usa nombres en español sin tildes.

## Evidence on Hand

- Reglas de cada juego con cobertura de pruebas unitarias (`app/src/test/`).
- Guías normativas en `documentacion/docs/`.
- No existe contenido comercial, testimonios ni métricas: no fabricarlos.

## Product Principles

1. El craft se demuestra en los detalles: mejor pulir lo existente que ampliar el catálogo.
2. Nada requiere conexión ni cuenta: todo funciona en el dispositivo, siempre.
3. Las reglas de los juegos viven fuera de las vistas y se prueban sin ellas.
4. La documentación y el código avanzan juntos; lo que se aparta de las guías se acuerda y se documenta.

## Accessibility & Inclusion

La accesibilidad es un requisito, no un extra: TalkBack, contraste, escalado de fuente
y movimiento reducido deben funcionar bien en los cinco juegos. Los textos son en
español latinoamericano y los recursos de texto deben mantenerse estructurados para
permitir traducciones futuras.
