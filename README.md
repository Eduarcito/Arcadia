# Arcadia

Aplicación Android en Kotlin con cinco juegos disponibles sin conexión y un catálogo
de tarjetas ilustradas. Los ajustes permiten cambiar el volumen, los efectos de
sonido, la música, la vibración y el modo oscuro. KenKen conserva su implementación,
recursos, componentes compartidos y apariencia originales.

- **2048:** tablero 4 × 4 controlado con deslizamientos, fusiones sin encadenar la misma
  ficha, puntuación, récord, detección de fin y opción de continuar tras llegar a 2048.
- **Buscaminas:** tablero 8 × 8 con 10 minas, zona inicial segura, expansión de ceros,
  casillas mediante toque y banderas mediante pulsación larga, victoria y revelación al perder.
- **Petdoku:** 12 niveles de solución única. Coloca una mascota en cada fila, columna
  y región de color, sin contacto diagonal. Un toque marca o quita una ×; dos toques
  descubren una mascota. Un intento incorrecto deja una × roja fija y consume una
  de las 3 vidas; al agotarlas se bloquea el tablero hasta reiniciar. Arrastrar marca
  varias casillas, o borra notas si el trazo empieza en una ×, sin quitar mascotas
  ni errores. Las vidas y las marcas se guardan al salir.
- **Snake:** deslizamientos con dirección continua sin levantar el dedo, crecimiento
  al comer, velocidad progresiva, récord y colisiones con paredes o cuerpo. Tocar
  el tablero pausa o continúa; un deslizamiento inicia la partida. Se pausa al salir.
- **KenKen:** cinco variantes 4 × 4 con jaulas, comprobación y reinicio.

Los cuatro juegos nuevos guardan automáticamente su progreso en el dispositivo,
incluso al cerrar la aplicación. Los ajustes no se aplican a KenKen. Las ilustraciones
del catálogo son vectores dibujados en código; la melodía incluida es original.

El menú lateral se abre con las tres líneas de la esquina superior izquierda y
contiene Mis juegos y Ajustes. El icono adaptativo de Android usa la imagen generada
`drawable-nodpi/arcadia_app_icon.png`, con margen para el recorte del launcher.
Las cuatro pantallas nuevas usan colores propios, marcadores separados y tableros
con relieve. Las fichas cambian con una animación breve y Snake se dibuja con
interpolación entre pasos. No hay botones direccionales; 2048 y Snake también
exponen las direcciones como acciones de accesibilidad del tablero.

## Dónde trabajar

El código está en `app/src/main/java/com/example/arcadia/` y las vistas en `app/src/main/res/layout/`.

| Funcionalidad | Paquete o archivo Kotlin | Layout |
|---|---|---|
| Catálogo | `MainActivity.kt` | `activity_main.xml`, `tarjeta_arcade.xml` |
| Ajustes | `arcade/AjustesActivity.kt` | `activity_arcade_settings.xml` |
| 2048 | `juegos/juego2048/` | `activity_arcade_game.xml` |
| Buscaminas | `juegos/buscaminas/` | `activity_arcade_game.xml` |
| Petdoku | `juegos/petdoku/` | `activity_arcade_game.xml` |
| Snake | `juegos/snake/` | `activity_arcade_game.xml` |
| KenKen | `juegos/kenken/` | `activity_kenken.xml` |

KenKen usa los componentes `TableroKenKenView` y `ControlesKenKenView`.
Los modos **Incrementar**, **Decrementar** y **Borrar** se aplican al tocar una
casilla. **Comprobar** distingue entre tablero incompleto, incorrecto y correcto;
solo una comprobación correcta bloquea la partida hasta reiniciar.
Cada partida elige al azar una de cinco variantes predefinidas de solución única.
Reiniciar selecciona otra variante sin repetir la anterior de inmediato.
Rotar la pantalla conserva el rompecabezas y sus valores. No se utiliza un generador.

Los componentes originales en `compartido/ui/` se conservan para KenKen. Las nuevas
pantallas utilizan el tema local `Theme.Arcade` y los componentes en `arcade/`.
`TableroArcadeView` infla sus casillas una vez mediante View Binding y actualiza
solo las que cambian. Cada juego tiene reglas en Kotlin puro y un `ViewModel`;
`PartidaViewModel` conserva el estado durante la rotación y lo guarda como JSON
en preferencias privadas. Snake elimina sus callbacks al pausar la Activity.

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
Las pruebas de lógica verifican los umbrales de gesto, la cancelación y un único
movimiento por trazo en 2048, además de la solución
única y conectividad de las regiones de cada nivel de Petdoku. Las pruebas de KenKen
se mantienen intactas.
