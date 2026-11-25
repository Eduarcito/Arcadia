package com.example.arcadia.juegos.snake

import android.os.Bundle
import android.view.accessibility.AccessibilityManager
import androidx.core.content.getSystemService
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.estado.Direccion
import com.example.arcadia.databinding.ActivitySnakeBinding

class SnakeActivity : PartidaArcadeActivity() {
    private val modelo: SnakeViewModel by viewModels()
    private var botonPausa: com.google.android.material.button.MaterialButton? = null
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    override val partidaPerdida get() = modelo.juego.terminada && !modelo.juego.victoria
    private val reloj = Handler(Looper.getMainLooper())
    private val paso = object : Runnable {
        override fun run() {
            if (!modelo.corriendo) return
            val anterior = modelo.juego.puntos
            modelo.paso()
            if (modelo.juego.puntos != anterior) { respuesta(); modelo.guardar(guardado) }
            mostrar()
            if (modelo.corriendo) reloj.postDelayed(this, maxOf(100L,230L-modelo.juego.puntos))
            else modelo.guardar(guardado)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivitySnakeBinding.inflate(layoutInflater).root)
        if (!pasoAPaso) {
            botonPausa = com.example.arcadia.compartido.ui.botonAccionArcade(this, getColor(R.color.acento_snake)).apply {
                textoPausa()
                setOnClickListener { alternarPausa() }
            }
            pantalla.usarPantallaCompleta(herramientas = botonPausa)
        } else {
            pantalla.usarPantallaCompleta()
        }
        modelo.cargar(guardado)
        pantalla.tablero.gestoContinuo = true
        pantalla.tablero.alDeslizar = ::girar
        pantalla.tablero.alPulsarTablero = ::tocarTablero
        pantalla.alReiniciar {
            detener()
            if (reinicioPierdeProgreso()) confirmar { reiniciar() } else reiniciar()
        }
        mostrar(); modelo.guardar(guardado)
    }

    private val pasoAPaso: Boolean
        get() = getSystemService<AccessibilityManager>()?.isTouchExplorationEnabled == true

    private fun girar(direccion: Direccion) {
        if (modelo.juego.terminada) return
        if (!modelo.girar(direccion)) return
        if (pasoAPaso) {
            if (!modelo.empezada) modelo.empezada = true
            pasoManual()
        } else if (!modelo.corriendo) empezar()
    }

    private fun pasoManual() {
        val anterior = modelo.juego.puntos
        modelo.corriendo = false; reloj.removeCallbacks(paso)
        modelo.paso()
        if (modelo.juego.puntos != anterior) { respuesta() }
        modelo.guardar(guardado); mostrar()
        if (!modelo.juego.terminada) anunciarFruta()
    }

    private fun anunciarFruta() {
        val juego = modelo.juego
        if (juego.comida < 0) return
        val (horizontal, vertical) = juego.direccionFruta()
        val partes = mutableListOf<String>()
        if (vertical < 0) partes.add(getString(R.string.arcade_snake_hacia_arriba))
        if (vertical > 0) partes.add(getString(R.string.arcade_snake_hacia_abajo))
        if (horizontal < 0) partes.add(getString(R.string.arcade_snake_hacia_izquierda))
        if (horizontal > 0) partes.add(getString(R.string.arcade_snake_hacia_derecha))
        if (partes.isEmpty()) return
        val direccion = partes.joinToString(getString(R.string.arcade_snake_y))
        val distancia = kotlin.math.abs(horizontal) + kotlin.math.abs(vertical)
        var anuncio = getString(R.string.arcade_snake_fruta_direccion, direccion, distancia)
        if (juego.peligroAdelante()) anuncio += " " + getString(R.string.arcade_snake_peligro)
        pantalla.tablero.announceForAccessibility(anuncio)
    }

    private fun empezar() {
        modelo.empezada = true
        if (pasoAPaso) { modelo.corriendo = false; modelo.guardar(guardado); mostrar(); return }
        modelo.corriendo = true
        reloj.removeCallbacks(paso); reloj.postDelayed(paso,230)
        modelo.guardar(guardado); mostrar()
    }

    private fun alternarPausa() {
        if (modelo.juego.terminada) return
        if (pasoAPaso) {
            if (!modelo.empezada) modelo.empezada = true
            pasoManual()
        } else if (modelo.corriendo) detener() else empezar()
    }

    private fun tocarTablero() {
        // En modo contrarreloj un deslizamiento corto no debe pausar por accidente:
        // el toque solo avanza en el modo paso a paso; la pausa tiene botón propio.
        if (pasoAPaso) alternarPausa()
    }

    private fun detener() {
        modelo.corriendo = false; reloj.removeCallbacks(paso)
        modelo.guardar(guardado); mostrar()
    }

    private fun com.google.android.material.button.MaterialButton.textoPausa() {
        setText(if (modelo.corriendo) R.string.arcade_snake_pausar else R.string.arcade_snake_continuar)
        setIconResource(if (modelo.corriendo) R.drawable.ic_pausa else R.drawable.ic_reanudar)
    }

    private fun mostrar() {
        val juego = modelo.juego
        botonPausa?.textoPausa()
        botonPausa?.isEnabled = !juego.terminada
        val mensaje = when {
            juego.victoria -> getString(R.string.arcade_snake_win)
            juego.terminada -> getString(R.string.arcade_snake_overlay_over)
            modelo.corriendo -> ""
            pasoAPaso && modelo.empezada -> ""
            modelo.empezada -> getString(R.string.arcade_snake_overlay_paused)
            else -> getString(R.string.arcade_snake_overlay_ready)
        }
        pantalla.tablero.mostrarSnake(juego.cuerpo,juego.comida,juego.direccion,mensaje,modelo.corriendo)
        estadisticas(getString(R.string.arcade_score,juego.puntos),getString(R.string.arcade_best,modelo.record))
        pantalla.mostrarEstado(when {
            juego.victoria -> R.string.arcade_snake_win
            juego.terminada -> R.string.arcade_snake_over
            pasoAPaso && modelo.empezada -> R.string.arcade_snake_paso_a_paso
            modelo.corriendo -> R.string.arcade_snake_running
            modelo.empezada -> R.string.arcade_snake_paused
            else -> R.string.arcade_snake_ready
        })
        comprobarDerrota()
    }

    override fun reinicioPierdeProgreso() = modelo.empezada && !modelo.juego.terminada
    override fun reiniciar() { reloj.removeCallbacks(paso); modelo.reiniciar(); modelo.guardar(guardado); mostrar() }
    override fun onPause() { detener(); super.onPause() }
}
