# Reglas y alcance de los juegos

Los seis juegos se ejecutan sin Internet. 2048, Buscaminas, Petdoku, Snake y Number Connect guardan automáticamente la partida como JSON en preferencias privadas. **Reiniciar** limpia la partida y conserva los récords; KenKen no tiene guardado persistente. Tras un resultado definitivo se bloquean nuevas jugadas, salvo que 2048 permita continuar después de ganar.

## 2048

- Tablero de **4 × 4** con dos fichas al iniciar. Control mediante deslizamientos y direcciones accesibles del tablero, sin botones direccionales ni selector.
- Cada trazo válido produce una sola jugada en el eje dominante. Ignorar toques cortos y gestos cancelados.
- Guardar tablero, puntuación, récord y confirmación del diálogo de victoria. Al volver se recupera la partida; reiniciar conserva el récord.
- En cada movimiento, desplazar las fichas hacia el lado indicado y fusionar las parejas de igual valor en ese orden. Una ficha resultante no puede fusionarse otra vez en el mismo movimiento.
- Sumar al puntaje el valor de cada ficha obtenida por fusión. Generar una nueva ficha de valor 2 o 4 en una casilla libre solo si el tablero cambió.
- Declarar victoria al alcanzar 2048 y derrota cuando no quede un movimiento válido. Un tablero lleno no es derrota si todavía hay fichas iguales contiguas que pueden fusionarse.
- Al ganar, mostrar el diálogo que permite continuar con el mismo tablero.

## Buscaminas

- Tablero de **8 × 8** con **10 minas**. Colocarlas al revelar la primera casilla válida, fuera de esa casilla y de todas sus vecinas.
- Contar como vecinas las casillas contiguas horizontales, verticales y diagonales dentro del tablero.
- El selector inferior permite descubrir o marcar con un toque. En modo descubrir, un toque descubre una casilla; una pulsación larga coloca o retira la bandera. Una casilla marcada no se puede descubrir hasta retirar la bandera.
- Revelar un número cuando haya minas vecinas. Si no hay ninguna, revelar también las casillas seguras contiguas y continuar la expansión mientras se encuentren casillas vacías. No abrir casillas marcadas en la expansión.
- Perder al revelar una mina. Ganar al abrir las **54 casillas seguras**, aunque no se hayan colocado banderas.
- Guardar minas, casillas abiertas, banderas, modo y estado de la partida.
- Mostrar racha y mejor racha; una victoria incrementa la racha una sola vez, perder o abandonar una partida iniciada la reinicia. Una pista gratuita por partida revela una casilla segura; su consumo también se guarda.

## Petdoku

- Tablero de **6 × 6**, con **12 niveles de solución única**. Cada fila, columna y región contiene una mascota; no pueden tocarse en diagonal.
- Un toque añade o borra una nota; un doble toque descubre una mascota. Un error queda marcado y consume una de las tres vidas. Sin vidas, el tablero se bloquea.
- Arrastrar marca varias casillas; empezar sobre una nota permite borrar notas sin quitar mascotas ni errores.
- Guardar nivel, marcas y vidas. La victoria permite pasar al siguiente nivel.

## Snake

- Tablero de **14 × 14**. Deslizar dirige la serpiente, también sin levantar el dedo; tocar el tablero pausa o continúa.
- Comer aumenta la longitud y la puntuación, con velocidad progresiva. Chocar con paredes o cuerpo termina la partida.
- Guardar cuerpo, dirección, comida, puntuación, récord y resultado. Al salir se pausa y se eliminan los callbacks; al volver no se reanuda automáticamente.

## Number Connect

- Tablero de **5 × 7** con potencias de dos. Arrastrar conecta dos o más números iguales vecinos, incluidas diagonales. Retroceder sobre la casilla anterior acorta el trazo.
- Al levantar el dedo, combinar en la última casilla: sumar la selección y redondear hacia arriba a la siguiente potencia de dos (tres fichas de 8 producen 32). Aplicar gravedad y rellenar desde arriba.
- Sumar el valor obtenido a la puntuación. No fusionar toques aislados ni gestos cancelados. Las reglas validan cada casilla agregada.
- Guardar tablero, puntuación y récord. Cuando no queden parejas iguales vecinas, mostrar reintentar o menú principal.
- El tablero se adapta al espacio disponible sin desplazamiento, como los de 2048, Buscaminas y Snake.

## KenKen

- Tablero de **4 × 4** elegido al azar de un catálogo de **cinco rompecabezas predefinidos de solución única**. Las cuatro filas y las cuatro columnas contienen los números del 1 al 4 sin repetir. Al abrir el juego se elige una variante; **Reiniciar** limpia la partida y elige otra, sin repetir inmediatamente la anterior. Rotar conserva la variante actual. No generar rompecabezas nuevos.
- Agrupar las casillas en jaulas contiguas y mostrar el objetivo y la operación de cada una. Cada casilla pertenece a una sola jaula. Una jaula de una casilla fija su valor objetivo; limitar resta y división a dos casillas.
- La suma y la multiplicación usan todos los valores de la jaula. Para la resta o la división, usar primero el valor mayor. La división debe ser exacta.
- Usar tres modos de entrada: **Incrementar**, **Decrementar** y **Borrar**, con un solo modo activo e **Incrementar** seleccionado al iniciar. Cada toque en una casilla aplica el modo: incrementar desde vacío empieza en 1 y se detiene en 4; decrementar desde vacío empieza en 4 y se detiene en 1; borrar elimina el valor inmediatamente. Cambiar de modo no modifica las casillas. Conservar el modo al rotar y no abrir el teclado del sistema.
- **Comprobar** distingue entre tablero incompleto, tablero con errores y tablero correcto. La victoria requiere que todas las filas, columnas y jaulas sean válidas. Una comprobación fallida permite seguir corrigiendo.

Referencias de alcance: [Arcadia](https://arcade-games-eosin.vercel.app/) para la experiencia de 2048 y Buscaminas, y [reglas de KenKen en video](https://youtu.be/774XJRKYZYc) como introducción al rompecabezas. Las reglas anteriores definen la entrega móvil.

Con TalkBack activo, Snake juega paso a paso: cada deslizamiento avanza una casilla, el toque sigue recto y se anuncian la fruta y el peligro adelante.
