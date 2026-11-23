# Reglas y alcance de los juegos

Los tres juegos se ejecutan sin Internet. **Reiniciar** limpia el estado de la partida; tras un resultado definitivo no se aceptan más jugadas hasta reiniciar. El alcance de la entrega no incluye récords persistentes, niveles adicionales ni generadores de rompecabezas.

## 2048

- Tablero de **4 × 4** con dos fichas al iniciar. Incluir dos modos de control: **Deslizar** y **Botones de dirección**.
- La primera vez que se abre 2048 sin una preferencia guardada, mostrar el diálogo **Controles de 2048** con ambos modos. Si se cierra sin elegir, usar y guardar **Botones de dirección**. El botón **Controles** vuelve a abrir el diálogo; si se cierra sin elegir, conserva el modo activo.
- En **Deslizar**, ocultar los cuatro botones direccionales. Aceptar un deslizamiento que empiece en cualquier punto del tablero y ejecutar un solo movimiento al soltar el dedo, en la dirección del eje dominante. Ignorar toques cortos, gestos cancelados y gestos que empiecen fuera del tablero.
- En **Botones de dirección**, desactivar el control por gestos y mostrar los botones **Arriba**, **Abajo**, **Izquierda** y **Derecha**. Ambos modos llaman a `moverTablero(direccion)` y siguen las mismas reglas.
- Guardar solo la preferencia de control en el dispositivo. Cambiar de modo no altera el tablero ni el puntaje, y **Reiniciar** no borra la preferencia.
- En cada movimiento, desplazar las fichas hacia el lado indicado y fusionar las parejas de igual valor en ese orden. Una ficha resultante no puede fusionarse otra vez en el mismo movimiento.
- Sumar al puntaje el valor de cada ficha obtenida por fusión. Generar una nueva ficha de valor 2 o 4 en una casilla libre solo si el tablero cambió.
- Declarar victoria al alcanzar 2048 y derrota cuando no quede un movimiento válido. Un tablero lleno no es derrota si todavía hay fichas iguales contiguas que pueden fusionarse.

## Buscaminas

- Tablero de **6 × 6** con **6 minas**. Colocarlas al revelar la primera casilla válida, fuera de esa casilla y de todas sus vecinas.
- Contar como vecinas las casillas contiguas horizontales, verticales y diagonales dentro del tablero.
- Usar un selector visible **Descubrir / Marcar**. Marcar coloca o retira una bandera de una casilla cerrada. Una casilla marcada no se puede descubrir hasta retirar la bandera.
- Revelar un número cuando haya minas vecinas. Si no hay ninguna, revelar también las casillas seguras contiguas y continuar la expansión mientras se encuentren casillas vacías. No abrir casillas marcadas en la expansión.
- Perder al revelar una mina. Ganar al abrir las **30 casillas seguras**, aunque no se hayan colocado banderas.

## KenKen

- Tablero de **4 × 4** con un rompecabezas predefinido de solución única. Las cuatro filas y las cuatro columnas contienen los números del 1 al 4 sin repetir.
- Agrupar las casillas en jaulas contiguas y mostrar el objetivo y la operación de cada una. Cada casilla pertenece a una sola jaula. Una jaula de una casilla fija su valor objetivo; limitar resta y división a dos casillas.
- La suma y la multiplicación usan todos los valores de la jaula. Para la resta o la división, usar primero el valor mayor. La división debe ser exacta.
- Seleccionar una casilla y usar botones del **1 al 4** o **Borrar**. No abrir el teclado del sistema.
- **Comprobar** distingue entre tablero incompleto, tablero con errores y tablero correcto. La victoria requiere que todas las filas, columnas y jaulas sean válidas. Una comprobación fallida permite seguir corrigiendo.

Referencias de alcance: [Arcadia](https://arcade-games-eosin.vercel.app/) para la experiencia de 2048 y Buscaminas, y [reglas de KenKen en video](https://youtu.be/774XJRKYZYc) como introducción al rompecabezas. Las reglas anteriores definen la entrega móvil.
