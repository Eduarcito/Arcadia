# Pruebas y criterios de entrega

Comprobar las reglas por separado y recorrer catálogo, ajustes y los seis juegos en un dispositivo o emulador. Si se agregan pruebas locales con JUnit, ubicarlas en `app/src/test/` y reflejar el paquete del juego que prueban.

## Pruebas generales

- Tras compilar el módulo, abrir `activity_main.xml` en **Design**: las seis tarjetas deben mostrar su título, ilustración y color sin ejecutar la `Activity`. Abrir los layouts de los juegos nuevos y comprobar el contenido configurado mediante atributos.
- El catálogo abre los seis juegos y Atrás regresa al mismo catálogo sin duplicarlo.
- Cada tablero tiene el número correcto de filas y casillas, con tamaño uniforme. Cabe en un teléfono pequeño sin desplazamiento horizontal. Los mensajes se leen y los controles no quedan bajo las barras del sistema.
- Al rotar, se conservan el tablero y los datos de la partida. Al salir y volver a entrar, 2048, Buscaminas, Petdoku y Snake recuperan su guardado; comprobar también tras cerrar y volver a abrir la aplicación. KenKen inicia otra partida al volver a abrirse.
- **Reiniciar** restablece el tablero y los indicadores. Tras ganar o perder, no se aceptan nuevas jugadas hasta reiniciar.
- La aplicación funciona sin conexión. El estado de una casilla no se comunica solo por su color.

## 2048

| Caso | Resultado esperado |
|---|---|
| Mover `2, 2, 2, 2` a la izquierda. | La fila queda `4, 4, 0, 0` antes de generar la ficha nueva; el puntaje aumenta en 8. |
| Mover `2, 2, 4, 0` a la izquierda. | La fila queda `4, 4, 0, 0`; la ficha recién fusionada no se vuelve a fusionar. |
| Ejecutar un movimiento que no cambia el tablero. | No aparece una ficha nueva ni cambia el puntaje. |
| Llenar el tablero, pero dejar una pareja fusionable. | La partida continúa. Sin parejas fusionables, termina en derrota. |
| Alcanzar 2048 y elegir continuar. | El diálogo se confirma y el mismo tablero permite seguir jugando. |
| Deslizar en las cuatro direcciones; tocar sin deslizar, cancelar o empezar fuera del tablero. | Cada deslizamiento válido produce una sola jugada, y las demás entradas no mueven fichas. |
| Salir, volver a abrir y reiniciar. | Se recuperan tablero y puntuación; reiniciar mantiene el récord. No aparece un selector de controles. |

## Buscaminas

- La primera casilla revelada y todas sus vecinas son seguras, incluso en esquinas y bordes.
- Una bandera impide revelar la casilla. Quitarla permite descubrirla.
- Revelar una casilla sin minas vecinas abre las zonas vacías conectadas, sin abrir minas ni casillas con bandera.
- Revelar una mina termina la partida. Abrir las 54 casillas seguras da la victoria sin exigir banderas.
- Salir y volver conserva minas, banderas y casillas abiertas, sin regenerarlas.

## Petdoku y Snake

- Petdoku distingue toque simple, doble y arrastre en todas las filas tras cambiar a `LinearLayout`. Los trazos no afectan a mascotas ni errores.
- Petdoku conserva nivel, marcas y vidas al salir. Tres fallos bloquean la partida con un diálogo de reintento; reiniciar devuelve las vidas y ganar avanza al siguiente nivel tras una breve pausa.
- Snake permite varios giros sin levantar el dedo y pausa o continúa al tocar el tablero, sin botones direccionales.
- Snake se pausa al salir, rotar o reiniciar; no avanza en segundo plano y conserva el cuerpo guardado.

## KenKen

- Las cinco variantes tienen una solución prevista válida y cada una admite una sola solución. Todas las casillas pertenecen a una sola jaula contigua.
- Al abrir KenKen se elige una de las cinco variantes; **Reiniciar** cambia las pistas y las jaulas, evita repetir inmediatamente la variante anterior y limpia los valores. Rotar conserva la variante actual.
- Una fila o columna con números repetidos se rechaza aunque las jaulas cumplan sus objetivos.
- Una cuadrícula sin repetidos se rechaza si falla una jaula.
- `4 ÷ 2` cumple un objetivo de 2; los valores `3` y `2` no cumplen un objetivo de 1.
- **Borrar** limpia solo la casilla seleccionada. **Comprobar** sobre un tablero incompleto no declara victoria ni bloquea la partida.
- **Incrementar** empieza en 1 y se detiene en 4; **Decrementar** empieza en 4 si la casilla está vacía y se detiene en 1. **Borrar** actúa con un solo toque. Cambiar de modo no altera los valores.
- Las jaulas en forma de L se ven continuas entre filas y muestran una sola pista. Cada frontera de jaula se dibuja una sola vez, sin duplicar su grosor.
- Una comprobación incorrecta permite seguir editando. Una comprobación correcta bloquea las jugadas; **Reiniciar** limpia valores, selección y resultado, y activa **Incrementar**.
- Al rotar se conservan los valores, el modo y el resultado de la comprobación.
- El título no se duplica en el encabezado ilustrado. Los indicadores muestran casillas completadas y modo activo. Los modos tienen colores distintos y el activo muestra un contorno más grueso.
- La pantalla mantiene el estilo de tarjetas y ayuda compartido con los demás juegos. El tablero conserva las jaulas continuas y permite desplazarse verticalmente en teléfonos pequeños.
- Las tarjetas **Sumar**, **Restar** y **Borrar** muestran iconos y etiquetas. Siempre hay una sola marcada, incluso al tocar de nuevo el modo activo. Al resolver se deshabilitan; reiniciar activa de nuevo **Sumar**.
- Cambiar el modo oscuro global y abrir KenKen aplica el mismo tema que el catálogo. Volver a una pantalla de KenKen abierta tras cambiar la preferencia actualiza el tema sin perder valores ni modo. Comprobar contraste de pistas, números, selección, controles y mensajes en ambos temas.
- Las microinteracciones no bloquean toques rápidos, no cambian valores por sí mismas y no se repiten al rotar. Con las animaciones del sistema desactivadas, todos los controles y mensajes siguen funcionando sin movimiento.

## Exposición

`EstructuraJuegosTest` contiene regresiones instrumentadas para las filas `LinearLayout`, el guardado al reabrir y los componentes, modos, rotación y reinicio de KenKen. `ArcadeFlowTest` conserva las pruebas de navegación, gestos, ajustes y guardado de los cuatro juegos nuevos. Ejecutarlas manualmente en Android Studio con un dispositivo de prueba; no usar un dispositivo con partidas que deban conservarse, porque `ArcadeFlowTest` restablece las preferencias.

Cada integrante debe poder mostrar una jugada completa: **pulsación en la `Activity` → cambio de estado en el `ViewModel` → actualización de las casillas XML**. También debe poder explicar una regla del juego y señalar un componente XML reutilizado.

Las pruebas de guardado usan Robolectric (`GuardadoPartidasTest`): recorren el ciclo completo de guardar y restaurar en cada juego, fijan las claves inmutables del formato (`2048`, `mines`, `petdoku`, `snake`) y cubren JSON corrupto y carga única.

La compilación release usa R8 (`optimization.enable`); verificarla con `./gradlew :app:assembleRelease`.
## Tableros completos y Number Connect

`JuegosCompletosTest` comprueba tableros completamente visibles, selector y pista de Buscaminas, racha sin duplicados, fusiones por arrastre, guardado, reinicio y derrota de Number Connect. Restaura las preferencias originales al terminar cada prueba. `NumberConnectTest` comprueba conexiones diagonales, caminos inválidos, gravedad, puntuación, fin de partida y recuperación del estado.

