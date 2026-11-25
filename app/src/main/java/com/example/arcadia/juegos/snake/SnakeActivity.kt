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
import com.example.arcadia.compartido.estado.ARCHIVO_PARTIDAS

class SnakeActivity : PartidaArcadeActivity() {
    private val modelo: SnakeViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences(ARCHIVO_PARTIDAS,MODE_PRIVATE) }
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
        modelo.cargar(guardado)
        pantalla.tablero.gestoContinuo = true
        pantalla.tablero.alDeslizar = ::girar
        pantalla.tablero.alPulsarTablero = ::alternarPausa
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

    private fun detener() {
        modelo.corriendo = false; reloj.removeCallbacks(paso)
        modelo.guardar(guardado); mostrar()
    }

    private fun mostrar() {
        val juego = modelo.juego
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
    }

    override fun reinicioPierdeProgreso() = modelo.empezada && !modelo.juego.terminada
    override fun reiniciar() { reloj.removeCallbacks(paso); modelo.reiniciar(); modelo.guardar(guardado); mostrar() }
    override fun onPause() { detener(); super.onPause() }
}
