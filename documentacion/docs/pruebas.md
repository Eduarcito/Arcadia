# Pruebas y criterios de entrega

Comprobar las reglas por separado y recorrer las cuatro pantallas en un dispositivo o emulador. Si se agregan pruebas locales con JUnit, ubicarlas en `app/src/test/` y reflejar el paquete del juego que prueban.

## Pruebas generales

- El catálogo abre los tres juegos y Atrás regresa al mismo catálogo sin duplicarlo.
- Cada tablero tiene el número correcto de filas y casillas, con tamaño uniforme. Cabe en un teléfono pequeño sin desplazamiento horizontal. Los mensajes se leen y los controles no quedan bajo las barras del sistema.
- Al rotar, se conservan el tablero y los datos de la partida. Al salir y volver a entrar, empieza otra partida.
- **Reiniciar** restablece el tablero y los indicadores. Tras ganar o perder, no se aceptan nuevas jugadas hasta reiniciar.
- La aplicación funciona sin conexión. El estado de una casilla no se comunica solo por su color.

## 2048

| Caso | Resultado esperado |
|---|---|
| Mover `2, 2, 2, 2` a la izquierda. | La fila queda `4, 4, 0, 0` antes de generar la ficha nueva; el puntaje aumenta en 8. |
| Mover `2, 2, 4, 0` a la izquierda. | La fila queda `4, 4, 0, 0`; la ficha recién fusionada no se vuelve a fusionar. |
| Ejecutar un movimiento que no cambia el tablero. | No aparece una ficha nueva ni cambia el puntaje. |
| Llenar el tablero, pero dejar una pareja fusionable. | La partida continúa. Sin parejas fusionables, termina en derrota. |
| Abrir 2048 sin una preferencia guardada y cerrar el diálogo sin elegir. | Se activa el modo de botones y no se vuelve a abrir el diálogo automáticamente. |
| Probar ambas opciones con el mismo tablero de partida y la misma dirección. | El desplazamiento, las fusiones y el puntaje son iguales. |
| Deslizar en las cuatro direcciones; tocar sin deslizar, cancelar o empezar fuera del tablero. | Cada deslizamiento válido produce una sola jugada, y las demás entradas no mueven fichas. |
| Cambiar desde **Controles**, rotar o volver a abrir la aplicación. | El modo se conserva. Cambiarlo no altera el tablero ni el puntaje. Reiniciar la partida tampoco cambia el modo. |

## Buscaminas

- La primera casilla revelada y todas sus vecinas son seguras, incluso en esquinas y bordes.
- Una bandera impide revelar la casilla. Quitarla permite descubrirla.
- Revelar una casilla sin minas vecinas abre las zonas vacías conectadas, sin abrir minas ni casillas con bandera.
- Revelar una mina termina la partida. Abrir las 30 casillas seguras da la victoria sin exigir banderas.

## KenKen

- La solución prevista es válida y el rompecabezas no admite otra solución.
- Una fila o columna con números repetidos se rechaza aunque las jaulas cumplan sus objetivos.
- Una cuadrícula sin repetidos se rechaza si falla una jaula.
- `4 ÷ 2` cumple un objetivo de 2; los valores `3` y `2` no cumplen un objetivo de 1.
- **Borrar** limpia solo la casilla seleccionada. **Comprobar** sobre un tablero incompleto no declara victoria ni bloquea la partida.

## Exposición

Cada integrante debe poder mostrar una jugada completa: **pulsación en la `Activity` → cambio de estado en el `ViewModel` → actualización de las casillas XML**. También debe poder explicar una regla del juego y señalar un componente XML reutilizado.
