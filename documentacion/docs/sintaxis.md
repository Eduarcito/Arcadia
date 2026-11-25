# Sintaxis y ejemplos

Los ejemplos ilustran convenciones del proyecto. Los fragmentos de pantalla se integran en su `Activity` o layout correspondiente; no representan pantallas completas.

## Convenciones

- Escribir paquetes en minúsculas (`juegos.kenken`), clases y archivos de clase en `PascalCase` (`KenKenActivity.kt`), y funciones y variables en `camelCase` (`validarJaulas`, `filaSeleccionada`).
- Usar `snake_case` en archivos e IDs de recursos XML (`celda_kenken.xml`, `btn_reiniciar`) y `MAYUSCULAS_CON_GUIONES_BAJOS` para las constantes Kotlin (`TAMANO_TABLERO`).
- Usar `val` por defecto y `var` solo si se reasigna el valor. Usar `private` para detalles de una clase e `internal` para funciones de ámbito de módulo que deban probarse o llamarse desde otro archivo. `internal` no restringe el uso a una carpeta.
- Usar cuatro espacios de sangría. Preferir `if` para decisiones simples, `when` para varias opciones y `for` o `while` cuando permitan seguir mejor una regla paso a paso. No usar `!!` ni encadenar muchas operaciones si una versión explícita resulta más clara.
- Los comentarios deben explicar decisiones o comportamientos importantes y escribirse sin tildes, sin eñe y sin punto final. Los textos que ve el usuario sí siguen la ortografía habitual del español.

## 1. Usar el encabezado compartido en otra pantalla

Declarar una instancia del componente y activar el botón de reinicio cuando la pantalla lo necesite:

```xml
<com.example.arcadia.compartido.ui.EncabezadoJuegoView
    android:id="@+id/encabezado"
    android:layout_width="0dp"
    android:layout_height="64dp"
    app:mostrarReiniciar="true"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

En `onCreate()`, después de inicializar el binding, conectar el botón:

```kotlin
binding.encabezado.alVolver { finish() }
```

View Binding genera `ActivityKenkenBinding` a partir de `activity_kenken.xml`. La función propia `alVolver()` conecta la acción con el botón interno; la pantalla no necesita conocer su ID. `finish()`, de `Activity` del SDK de Android, cierra el juego y vuelve al catálogo que lo abrió.

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
    app:layout_constraintTop_toBottomOf="@id/encabezado" />
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
                viewModel.aplicarModoEnCasilla(fila, columna)
                actualizarPantalla()
            }
            celdas.add(vistaCelda)
        }
    }
}
```

`FilaTableroBinding` y `CeldaKenkenBinding` se generan desde sus XML. `inflate()` crea la vista y, al pasar `true`, la añade al contenedor indicado. `until` recorre del `0` al `3` en este ejemplo. `aplicarModoEnCasilla()` es una función propia del `ViewModel` que aplica el modo de entrada activo. Las referencias quedan en la `Activity` por orden de fila y columna, para que `actualizarPantalla()` las use sin volver a inflarlas.

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

## 4. Conectar los gestos de 2048

`Juego2048Activity` infla `ActivityJuego2048Binding` y pasa su raíz a `preparar()`. `PantallaPartidaView` conserva su View Binding interno y expone el tablero compuesto para conectar `alDeslizar`.

La actividad recibe una `Direccion` de `compartido/estado/`, llama a `modelo.mover(direccion)`, guarda la partida y muestra el resultado. El detector de gestos compartido descarta desplazamientos cortos, cancela trazos inválidos y entrega una sola jugada por trazo en 2048. Snake activa el modo continuo del mismo detector.

`MotionEvent` es del SDK de Android; `DetectorDeslizamiento` es una clase del proyecto. No se requiere una biblioteca de gestos adicional. Las cuatro direcciones también se exponen como acciones de accesibilidad del tablero mediante AndroidX Core.

El único diálogo propio de 2048 es el de victoria, construido con `MaterialAlertDialogBuilder` de Material Components. No se añade selector de controles.

`PartidaViewModel.cargar()` y `guardar()` son funciones del proyecto que mantienen el contrato existente de JSON en `SharedPreferences`, del SDK de Android. Su uso permite recuperar tablero, puntuación y récord al volver a abrir, sin base de datos.

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
