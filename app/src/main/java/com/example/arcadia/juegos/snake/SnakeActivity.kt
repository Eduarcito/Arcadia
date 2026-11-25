package com.example.arcadia.juegos.snake

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.arcade.PartidaArcadeActivity
import com.example.arcadia.juegos.juego2048.Direccion

class SnakeActivity : PartidaArcadeActivity() {
    private val modelo: SnakeViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
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
        preparar(R.string.arcade_snake_title,R.string.arcade_snake_help)
        modelo.cargar(guardado)
        binding.board.gestoContinuo = true
        binding.board.alDeslizar = ::girar
        binding.board.alPulsarTablero = ::alternarPausa
        binding.restart.setOnClickListener {
            detener()
            confirmar { reiniciar() }
        }
        mostrar(); modelo.guardar(guardado)
    }

    private fun girar(direccion: Direccion) {
        if (modelo.juego.terminada) return
        if (modelo.juego.girar(direccion) && !modelo.corriendo) empezar()
    }

    private fun empezar() {
        modelo.corriendo = true; modelo.empezada = true
        reloj.removeCallbacks(paso); reloj.postDelayed(paso,230)
        modelo.guardar(guardado); mostrar()
    }

    private fun alternarPausa() {
        if (modelo.juego.terminada) return
        if (modelo.corriendo) detener() else empezar()
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
            modelo.empezada -> getString(R.string.arcade_snake_overlay_paused)
            else -> getString(R.string.arcade_snake_overlay_ready)
        }
        binding.board.mostrarSnake(juego.cuerpo,juego.comida,juego.direccion,mensaje,modelo.corriendo)
        estadisticas(getString(R.string.arcade_score,juego.puntos),getString(R.string.arcade_best,modelo.record))
        binding.status.setText(when {
            juego.victoria -> R.string.arcade_snake_win
            juego.terminada -> R.string.arcade_snake_over
            modelo.corriendo -> R.string.arcade_snake_running
            modelo.empezada -> R.string.arcade_snake_paused
            else -> R.string.arcade_snake_ready
        })
    }

    override fun reiniciar() { reloj.removeCallbacks(paso); modelo.reiniciar(); modelo.guardar(guardado); mostrar() }
    override fun onPause() { detener(); super.onPause() }
}
