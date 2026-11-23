# Estructura del proyecto

Organizar el código **por funcionalidad dentro de un único módulo `app`**. Una carpeta por juego permite encontrar su pantalla, su estado y sus reglas sin recorrer carpetas globales de `Activity`, modelos y lógica. Esta agrupación no requiere módulos Gradle adicionales ni una arquitectura de varias capas.

## Código Kotlin

Las rutas de esta tabla son relativas al paquete base de la aplicación bajo `app/src/main/java/` o `app/src/main/kotlin/`, según la plantilla del proyecto.

| Ruta | Responsabilidad |
|---|---|
| `MainActivity.kt` | Muestra el catálogo y abre cada juego con un `Intent` explícito. |
| `juegos/juego2048/Juego2048Activity.kt` | Conecta el tablero, los botones, los gestos y su diálogo de configuración. Lee y guarda la preferencia de control. |
| `juegos/juego2048/Juego2048ViewModel.kt` | Conserva el tablero, el puntaje y el resultado. |
| `juegos/juego2048/Reglas2048.kt` | Aloja operaciones comprobables sin Android, como fusionar una línea de fichas. |
| `juegos/buscaminas/BuscaminasActivity.kt` | Muestra el tablero y permite elegir entre descubrir y marcar. |
| `juegos/buscaminas/BuscaminasViewModel.kt` | Conserva las minas, las banderas, las casillas abiertas y el resultado. |
| `juegos/buscaminas/ReglasBuscaminas.kt` | Aloja operaciones como calcular las casillas vecinas. |
| `juegos/kenken/KenKenActivity.kt` | Conecta las casillas, el teclado numérico del juego y los controles. |
| `juegos/kenken/KenKenViewModel.kt` | Conserva los números ingresados, la casilla seleccionada y el resultado de la comprobación. |
| `juegos/kenken/ReglasKenKen.kt` | Comprueba filas, columnas y operaciones de las jaulas. |
| `juegos/kenken/RompecabezasKenKen.kt` | Define las casillas de cada jaula, su objetivo y su operación. |
| `compartido/ui/` | Funciones de interfaz que ya usan dos o más pantallas. Crear solo cuando exista esa necesidad. |

Registrar las cuatro `Activity` en `AndroidManifest.xml`. Solo el catálogo necesita ser la actividad de inicio. Volver al catálogo con Atrás o con `finish()` en el botón de regreso; no abrir otro catálogo encima del actual.

## Recursos Android

| Ruta | Contenido |
|---|---|
| `res/layout/activity_main.xml` | Catálogo con `ConstraintLayout` y tres tarjetas fijas en una columna. |
| `res/layout/activity_juego_2048.xml`, `activity_buscaminas.xml`, `activity_kenken.xml` | Raíz `ConstraintLayout` y contenedor `LinearLayout` vertical para el tablero de cada juego. |
| `res/layout/fila_tablero.xml` | Plantilla compartida: un `LinearLayout` horizontal que agrupa las casillas de cada fila. |
| `res/layout/celda_2048.xml`, `celda_buscaminas.xml`, `celda_kenken.xml` | Plantillas para inflar las casillas de cada tablero. |
| `res/values/` | Textos, colores, tamaños, estilos y tema. |
| `res/drawable/` | Fondos, bordes y estados visuales definidos en XML. |

Declarar las vistas fijas en el propio XML de la pantalla, sin usar `<include>`. Android no admite subcarpetas arbitrarias dentro de `res/layout/`. Distinguir los recursos de cada juego por sus nombres. Para tres tarjetas fijas no hace falta un `RecyclerView` ni un adaptador.

## Responsabilidades

- En `onCreate()`, inflar el layout de pantalla y usar dos bucles para crear el tablero: el primero añade las filas y el segundo añade las casillas de cada fila. Configurar los eventos y mostrar el estado actual. Tras una acción o al reiniciar, actualizar las casillas existentes sin reconstruirlas.
- Usar un `ViewModel` de pantalla para mantener la partida al rotar. La `Activity` llama a una función como `revelarCasilla()` y luego a `actualizarPantalla()`. No hacen falta `Flow` ni `LiveData` para estas operaciones síncronas.
- No modificar el tablero desde la interfaz ni exponer colecciones mutables para que la pantalla las cambie. Los juegos no deben depender entre sí.
- En 2048, detectar el gesto en la `Activity` y pasar solo la dirección al `ViewModel`. Guardar el modo elegido con `SharedPreferences` del SDK de Android, sin guardar el tablero ni introducir una base de datos.
- Mantener una función como `private` si solo sirve dentro de su clase. Extraer reglas a `Reglas*.kt` cuando así se puedan probar por separado o resulte más claro leerlas. No crear archivos de reglas vacíos ni un `Utilidades.kt` genérico.
- Salir del juego y abrirlo de nuevo inicia otra partida. No se exige restaurarla si Android termina el proceso.

Referencias: [Arquitectura Android para vistas](https://developer.android.com/topic/architecture/views/recommendations-views) y [organización por funcionalidad](https://developer.android.com/topic/modularization/patterns). Este proyecto toma el principio de separación de responsabilidades, no la configuración de una aplicación multimódulo.
