package com.example.arcadia.juegos.kenken

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.databinding.ActivityKenkenBinding
import com.example.arcadia.databinding.HerramientasKenkenBinding

class KenKenActivity : ArcadeActivity() {
    private lateinit var binding: ActivityKenkenBinding
    private lateinit var controles: HerramientasKenkenBinding
    private lateinit var tablero: TableroKenKenView
    private val viewModel: KenKenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKenkenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        margenes(binding.root)
        tablero = TableroKenKenView(this)
        controles = HerramientasKenkenBinding.inflate(LayoutInflater.from(this))
        binding.pantalla.usarPantallaCompleta(tableroAlternativo = tablero, herramientas = controles.root)
        binding.pantalla.alVolver { finish() }
        binding.pantalla.alReiniciar {
            viewModel.reiniciar()
            tablero.mostrarJaulas(viewModel.rompecabezas)
            actualizarPantalla()
        }
        tablero.mostrarJaulas(viewModel.rompecabezas)
        tablero.alSeleccionarCasilla { fila, columna ->
            viewModel.seleccionarCasilla(fila, columna)
            actualizarPantalla()
        }
        controles.controles.alIntroducirValor { valor ->
            val antes = viewModel.obtenerValores()
            viewModel.establecerValor(valor)
            if (viewModel.obtenerValores() != antes) respuesta()
            actualizarPantalla()
        }
        controles.comprobar.setOnClickListener {
            respuesta()
            viewModel.comprobar()
            actualizarPantalla()
            if (viewModel.resultado == ResultadoComprobacionKenKen.CORRECTO) {
                tablero.animarVictoria()
            }
        }
        actualizarPantalla()
    }

    private fun actualizarPantalla() {
        val seleccion = viewModel.casillaSeleccionada
        val habilitado = viewModel.resultado != ResultadoComprobacionKenKen.CORRECTO
        val duplicados = viewModel.indicesDuplicados()
        tablero.mostrarEstado(viewModel.obtenerValores(), seleccion, habilitado, duplicados)
        controles.controles.habilitar(habilitado && seleccion != null)
        controles.comprobar.isEnabled = habilitado
        binding.pantalla.mostrarEstadisticas(
            getString(R.string.kenken_casillas_completadas, viewModel.obtenerValores().count { it != 0 }),
            if (duplicados.isEmpty()) {
                getString(R.string.kenken_sin_repetidos)
            } else {
                getString(R.string.kenken_repetidos_indicador, duplicados.size)
            }
        )
        val mensaje = when (viewModel.resultado) {
            ResultadoComprobacionKenKen.SIN_COMPROBAR -> R.string.kenken_instruccion_teclado
            ResultadoComprobacionKenKen.INCOMPLETO -> R.string.kenken_resultado_incompleto
            ResultadoComprobacionKenKen.INCORRECTO -> R.string.kenken_resultado_incorrecto
            ResultadoComprobacionKenKen.CORRECTO -> R.string.kenken_resultado_correcto
        }
        binding.pantalla.mostrarEstado(mensaje)
        val color = when (viewModel.resultado) {
            ResultadoComprobacionKenKen.CORRECTO -> R.color.kenken_incrementar_tinte
            ResultadoComprobacionKenKen.INCORRECTO -> R.color.kenken_borrar_tinte
            else -> R.color.kenken_texto_secundario
        }
        binding.pantalla.colorEstado(color)
    }
}
