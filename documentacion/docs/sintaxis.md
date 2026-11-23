# Sintaxis y ejemplos

Los ejemplos ilustran convenciones del proyecto. Los fragmentos de pantalla se integran en su `Activity` o layout correspondiente; no representan pantallas completas.

## Convenciones

- Escribir paquetes en minúsculas (`juegos.kenken`), clases y archivos de clase en `PascalCase` (`KenKenActivity.kt`), y funciones y variables en `camelCase` (`validarJaulas`, `filaSeleccionada`).
- Usar `snake_case` en archivos e IDs de recursos XML (`celda_kenken.xml`, `btn_reiniciar`) y `MAYUSCULAS_CON_GUIONES_BAJOS` para las constantes Kotlin (`TAMANO_TABLERO`).
- Usar `val` por defecto y `var` solo si se reasigna el valor. Usar `private` para detalles de una clase e `internal` para funciones de ámbito de módulo que deban probarse o llamarse desde otro archivo. `internal` no restringe el uso a una carpeta.
- Usar cuatro espacios de sangría. Preferir `if` para decisiones simples, `when` para varias opciones y `for` o `while` cuando permitan seguir mejor una regla paso a paso. No usar `!!` ni encadenar muchas operaciones si una versión explícita resulta más clara.
- Los comentarios deben explicar decisiones o comportamientos importantes y escribirse sin tildes, sin eñe y sin punto final. Los textos que ve el usuario sí siguen la ortografía habitual del español.

## 1. Reutilizar un encabezado XML

Dentro de `activity_kenken.xml`, incluir un encabezado con una vista raíz normal. El botón con ID `btn_reiniciar` se declara en `encabezado_juego.xml`.

```xml
<include
    android:id="@+id/encabezado_juego"
    layout="@layout/encabezado_juego"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

Dentro de `KenKenActivity.kt`, después de inicializar el binding, conectar el botón con la partida:

```kotlin
private fun configurarEventos() {
    binding.encabezadoJuego.btnReiniciar.setOnClickListener {
        viewModel.reiniciar()
        actualizarPantalla()
    }
}
```

`<include>` es de Android y reutiliza el layout. View Binding genera `ActivityKenkenBinding` a partir de `activity_kenken.xml` y expone los IDs en Kotlin. `setOnClickListener()` pertenece a las vistas de Android. `reiniciar()` y `actualizarPantalla()` son funciones propias: la primera cambia la partida y la segunda actualiza la interfaz.

Activar `viewBinding = true` en `android.buildFeatures` del `app/build.gradle.kts`. Usar `AppCompatActivity` de AndroidX AppCompat para las pantallas, `ViewModel` de AndroidX Lifecycle y el delegado `by viewModels()` de AndroidX Activity KTX para obtener el estado de pantalla. No se necesita una biblioteca externa para View Binding.

## 2. Construir un tablero con `ConstraintLayout` y `LinearLayout`

En `activity_kenken.xml`, dentro del `ConstraintLayout` raíz, declarar el tablero debajo del encabezado. La raíz debe declarar `xmlns:app="http://schemas.android.com/apk/res-auto"` para los atributos de `ConstraintLayout`.

```xml
<LinearLayout
    android:id="@+id/tablero"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:orientation="vertical"
    app:layout_constraintDimensionRatio="H,1:1"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintTop_toBottomOf="@id/encabezado_juego" />
```

El ancho se ajusta entre las restricciones laterales; `H,1:1` calcula el alto a partir de ese ancho. `ConstraintLayout` pertenece a AndroidX. `LinearLayout` es parte del SDK de Android y no requiere otra biblioteca.

En `fila_tablero.xml`, declarar una fila con peso `1`. Como el tablero es vertical, el peso reparte el alto y por eso la fila parte de `0dp` en esa dimensión:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"
    android:orientation="horizontal" />
```

En el elemento raíz de `celda_kenken.xml`, usar `layout_width="0dp"`, `layout_height="match_parent"` y `layout_weight="1"` para repartir el ancho de la fila. Aplicar el mismo patrón a los otros dos XML de casilla.

En `KenKenActivity.kt`, llamar a `crearTablero()` una vez desde `onCreate()`, después de inicializar el binding:

```kotlin
private val celdas = mutableListOf<CeldaKenkenBinding>()

private fun crearTablero() {
    val tamanoTablero = 4

    for (fila in 0 until tamanoTablero) {
        val vistaFila = FilaTableroBinding.inflate(layoutInflater, binding.tablero, true)

        for (columna in 0 until tamanoTablero) {
            val vistaCelda = CeldaKenkenBinding.inflate(layoutInflater, vistaFila.root, true)
            vistaCelda.root.setOnClickListener {
                viewModel.seleccionarCasilla(fila, columna)
                actualizarPantalla()
            }
            celdas.add(vistaCelda)
        }
    }
}
```

`FilaTableroBinding` y `CeldaKenkenBinding` se generan desde sus XML. `inflate()` crea la vista y, al pasar `true`, la añade al contenedor indicado. `until` recorre del `0` al `3` en este ejemplo. `seleccionarCasilla()` es una función propia del `ViewModel`. Las referencias quedan en la `Activity` por orden de fila y columna, para que `actualizarPantalla()` las use sin volver a inflarlas.

## 3. Validar una regla sin importar Android

En `juegos/kenken/ReglasKenKen.kt`, la validación de una jaula de división recibe los dos valores y el objetivo:

```kotlin
internal fun cumpleDivision(primerValor: Int, segundoValor: Int, objetivo: Int): Boolean {
    if (primerValor <= 0 || segundoValor <= 0) {
        return false
    }

    val mayor = maxOf(primerValor, segundoValor)
    val menor = minOf(primerValor, segundoValor)

    return mayor % menor == 0 && mayor / menor == objetivo
}
```

`maxOf()` y `minOf()` son de la biblioteca estándar de Kotlin. `%` comprueba que el resto sea cero y evita aceptar `3 / 2` como `1` por el truncamiento de la división entera. La función se puede probar localmente sin abrir la aplicación.

## 4. Conectar el selector de controles de 2048

En `Juego2048Activity.kt`, usar `MaterialAlertDialogBuilder` de la biblioteca de Google para vistas XML. Los dos botones del diálogo eligen el modo; cerrar el diálogo con Atrás o tocando fuera activa el valor predeterminado solo si aún no hay una preferencia.

```kotlin
private fun mostrarSelectorControles() {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.titulo_controles_2048)
        .setPositiveButton(R.string.control_deslizar) { _, _ ->
            cambiarModoControl(ModoControl.DESLIZAR)
        }
        .setNegativeButton(R.string.control_botones) { _, _ ->
            cambiarModoControl(ModoControl.BOTONES)
        }
        .setOnCancelListener {
            if (modoControl == null) {
                cambiarModoControl(ModoControl.BOTONES)
            }
        }
        .show()
}
```

Declarar `ModoControl` como un `enum class` con `DESLIZAR` y `BOTONES`. `modoControl` es `null` solo si no se ha guardado una preferencia. `cambiarModoControl()` es una función propia de la `Activity`: guarda el modo con `SharedPreferences` y actualiza qué controles se muestran, sin cambiar la partida. Cargar la preferencia al abrir la pantalla y mostrar el diálogo automáticamente solo si falta.

Para el deslizamiento, usar `setOnTouchListener()` en el tablero y leer `MotionEvent.ACTION_DOWN`, `ACTION_UP` y `ACTION_CANCEL`, del SDK de Android. Guardar las coordenadas iniciales, comparar la distancia en X e Y al soltar y descartar desplazamientos por debajo de un umbral en `dp`, convertido a píxeles. El eje con mayor desplazamiento marca la dirección y su signo determina el sentido; en caso de empate, usar el eje horizontal. Tanto el gesto válido como los botones llaman a la misma función de la `Activity`, que ejecuta `viewModel.moverTablero(direccion)` y `actualizarPantalla()`.

## 5. Decidir cuándo compartir una función visual

Si una sola pantalla oculta el teclado, su función se queda como `private` en esa `Activity`. Si una segunda pantalla necesita lo mismo, se puede extraer a `compartido/ui/Teclado.kt`:

```kotlin
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

internal fun ocultarTeclado(ventana: Window, campo: View) {
    WindowCompat.getInsetsController(ventana, campo)
        .hide(WindowInsetsCompat.Type.ime())
    campo.clearFocus()
}
```

`Window`, `View` y `clearFocus()` son del SDK de Android. `WindowCompat` y `WindowInsetsCompat` son de AndroidX Core y permiten ocultar el teclado del sistema. **Este ejemplo no se incorpora al proyecto:** ningún juego requiere entrada de texto.

Referencias: [Convenciones de Kotlin](https://kotlinlang.org/docs/coding-conventions.html) y [View Binding](https://developer.android.com/topic/libraries/view-binding).
