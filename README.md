# Arcadia

## Antes de continuar el desarrollo

**Todo cambio debe seguir la documentación del proyecto.** Antes de modificar o
añadir funcionalidades, lee las [reglas obligatorias](documentacion/AGENTS.md) y
consulta las guías correspondientes:

- [Estructura](documentacion/docs/estructura.md): paquetes, clases y recursos.
- [Componentes](documentacion/docs/componentes.md): vistas compuestas y reutilización.
- [Diseño](documentacion/docs/diseno.md): estilos, controles y temas.
- [Juegos](documentacion/docs/juegos.md): reglas y alcance.
- [Sintaxis](documentacion/docs/sintaxis.md): convenciones y ejemplos.
- [Pruebas](documentacion/docs/pruebas.md): comprobaciones y criterios de entrega.

Si utilizas un agente de IA, indícale que lea `documentacion/AGENTS.md` antes de
trabajar. Acuerda con el equipo cualquier cambio que se aparte de estas guías y
actualiza la documentación para mantenerla alineada con la implementación.

## Descripción

Aplicación Android en Kotlin con seis juegos disponibles sin conexión y un catálogo
de tarjetas ilustradas. Los ajustes permiten cambiar el volumen, los efectos de
sonido, la música, la vibración y el modo oscuro. KenKen conserva sus reglas y
controles, y comparte el estilo de encabezado, tarjetas y ayuda con los otros juegos.

- **2048:** tablero 4 × 4 controlado con deslizamientos, fusiones sin encadenar la misma
  ficha, puntuación, récord, detección de fin y opción de continuar tras llegar a 2048.
- **Buscaminas:** tablero 8 × 8 con 10 minas, zona inicial segura, expansión de ceros,
  casillas mediante toque y banderas mediante pulsación larga, victoria y revelación al perder.
- **Petdoku:** 12 niveles de solución única. Coloca una mascota en cada fila, columna
  y región de color, sin contacto diagonal. Un toque marca o quita una ×; dos toques
  descubren una mascota. Un intento incorrecto deja una × roja fija y consume una
  de las 3 vidas; al agotarlas aparece un diálogo para reintentar o volver al menú.
  Al completar un nivel, el siguiente se abre automáticamente tras una breve pausa.
  Las reglas ilustradas y el encabezado compacto dejan visible el tablero completo.
  Arrastrar marca
  varias casillas, o borra notas si el trazo empieza en una ×, sin quitar mascotas
  ni errores. Las vidas y las marcas se guardan al salir.
- **Snake:** deslizamientos con dirección continua sin levantar el dedo, crecimiento
  al comer, velocidad progresiva, récord y colisiones con paredes o cuerpo. Tocar
  el tablero pausa o continúa; un deslizamiento inicia la partida. Se pausa al salir.
- **Number Connect:** tablero 5 × 7; arrastra sobre números iguales vecinos, también en diagonal, y suelta para combinarlos. Incluye puntuación, récord, guardado y diálogo de derrota.
- **KenKen:** cinco variantes 4 × 4 con jaulas, comprobación y reinicio.

Los cinco juegos nuevos guardan automáticamente su progreso en el dispositivo,
incluido el fin de la partida, incluso al cerrar la aplicación.
Al perder en 2048, Buscaminas o Snake, un diálogo
permite reintentar o volver al menú principal; los récords se conservan al reintentar.
El tema se aplica también a KenKen; los ajustes de
audio y vibración no modifican ese juego. Las ilustraciones
del catálogo son vectores dibujados en código; la melodía incluida es original.

2048, Buscaminas, Snake y Number Connect ajustan el tablero al ancho y alto disponibles, sin desplazamiento. La ayuda completa se abre con **?**. Buscaminas muestra casillas blancas, números de colores, racha, récord, selector para descubrir o marcar y una pista gratuita por partida.

El menú lateral se abre con las tres líneas de la esquina superior izquierda y
contiene Mis juegos y Ajustes. El icono adaptativo de Android usa la imagen generada
`drawable-nodpi/arcadia_app_icon.png`, con margen para el recorte del launcher.
Las cinco pantallas nuevas usan colores propios, marcadores separados y tableros
con relieve. Las fichas cambian con una animación breve y Snake se dibuja con
interpolación entre pasos. No hay botones direccionales; 2048 y Snake también
exponen las direcciones como acciones de accesibilidad del tablero.

## Dónde trabajar

El código está en `app/src/main/java/com/example/arcadia/` y las vistas en `app/src/main/res/layout/`.

| Funcionalidad | Paquete o archivo Kotlin | Layout |
|---|---|---|
| Catálogo | `MainActivity.kt` | `activity_main.xml`, `tarjeta_arcade.xml` |
| Ajustes | `ajustes/AjustesActivity.kt` | `activity_arcade_settings.xml` |
| 2048 | `juegos/juego2048/` | `activity_juego_2048.xml` |
| Buscaminas | `juegos/buscaminas/` | `activity_buscaminas.xml` |
| Petdoku | `juegos/petdoku/` | `activity_petdoku.xml` |
| Snake | `juegos/snake/` | `activity_snake.xml` |
| Number Connect | `juegos/numberconnect/` | `activity_number_connect.xml` |
| KenKen | `juegos/kenken/` | `activity_kenken.xml` |

KenKen usa los componentes `TableroKenKenView` y `ControlesKenKenView`.
Los modos **Incrementar**, **Decrementar** y **Borrar** se aplican al tocar una
casilla. **Comprobar** distingue entre tablero incompleto, incorrecto y correcto;
solo una comprobación correcta bloquea la partida hasta reiniciar.
Cada partida elige al azar una de cinco variantes predefinidas de solución única.
Reiniciar selecciona otra variante sin repetir la anterior de inmediato.
Rotar la pantalla conserva el rompecabezas y sus valores. No se utiliza un generador.

Los componentes visuales compartidos están en `compartido/ui/`. El catálogo declara
las tarjetas en XML y los cinco juegos nuevos usan `PantallaPartidaView`, una vista
compuesta basada en `ConstraintLayout`, con contenido fijo configurado por atributos.
`activity_arcade_game.xml` es su plantilla interna. Todos los juegos reutilizan
`EncabezadoJuegoView`, `IndicadoresJuegoView` y `AyudaJuegoView`.
`TableroArcadeView` infla filas y casillas `LinearLayout` una vez mediante View Binding
y actualiza solo las que cambian, sin `GridLayout`. Cada juego tiene reglas en Kotlin
puro y un `ViewModel`. `PartidaViewModel`, en `compartido/estado/`, mantiene sin cambios
el guardado JSON en preferencias privadas y sus claves existentes.
Snake elimina sus callbacks al pausar la Activity. `TemaArcadeActivity` aplica el
tema global y las barras del sistema a todas las pantallas, incluido KenKen.
Sus controles usan tarjetas independientes con iconos y etiquetas, con colores
adaptados a ambos temas. KenKen no guarda partidas de forma persistente.

Para crear o ampliar un juego, sigue la [guía de componentes](documentacion/docs/componentes.md).
El patrón de separar reglas, estado y vistas también se aplica a los nuevos juegos.

Al implementar una partida, coloca su `ViewModel` y sus reglas en el paquete del juego.
La `Activity` conecta los controles mediante View Binding y muestra el estado;
el `ViewModel` conserva la partida al rotar la pantalla. Escribe las reglas en Kotlin
sin referencias a vistas para poder probarlas en `app/src/test/`.

## Abrir y comprobar la base

1. Abre la carpeta raíz en Android Studio y sincroniza el proyecto con Gradle.
2. Usa el Gradle Wrapper del repositorio y el SDK de compilación indicado en `app/build.gradle.kts`.
3. Selecciona `app`, elige un dispositivo o emulador con Android 8.0 (API 26) o posterior y pulsa **Run**.
4. Desde el catálogo, abre cada juego y comprueba que **Volver al catálogo** y el botón Atrás del sistema regresan a la lista.

## Comprobaciones

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La segunda orden requiere un emulador o dispositivo de prueba. Las pruebas de interfaz
restablecen el guardado de los cuatro juegos nuevos y los ajustes en ese dispositivo.
Comprueban navegación, fusiones por gesto, recuperación del estado, banderas, marcas,
pausa táctil de Snake, toques simples/dobles, tres vidas y trazos de Petdoku,
menú lateral, icono adaptativo y modo claro.
`EstructuraJuegosTest` verifica filas uniformes, recuperación del guardado al reabrir
y componentes compartidos, modos, rotación y reinicio de KenKen.
Las pruebas de lógica verifican los umbrales de gesto, la cancelación y un único
movimiento por trazo en 2048, además de la solución
única y conectividad de las regiones de cada nivel de Petdoku. Las pruebas de KenKen
se mantienen intactas.
