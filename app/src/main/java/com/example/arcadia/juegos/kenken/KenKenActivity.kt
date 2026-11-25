package com.example.arcadia.juegos.kenken

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.TemaArcadeActivity
import com.example.arcadia.databinding.ActivityKenkenBinding

class KenKenActivity : TemaArcadeActivity() {
    private lateinit var binding: ActivityKenkenBinding
    private val viewModel: KenKenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKenkenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        androidx.core.view.ViewCompat.setAccessibilityHeading(binding.tituloTablero, true)
        margenes(binding.root)
        binding.encabezado.mostrarTitulo(getString(R.string.juego_kenken))
        binding.encabezado.alVolver { finish() }
        binding.encabezado.alReiniciar {
            viewModel.reiniciar()
            binding.tablero.mostrarJaulas(viewModel.rompecabezas)
            actualizarPantalla()
        }
        binding.tablero.mostrarJaulas(viewModel.rompecabezas)
        binding.tablero.alSeleccionarCasilla { fila, columna ->
            viewModel.seleccionarCasilla(fila, columna)
            actualizarPantalla()
        }
        binding.controles.alIntroducirValor { valor ->
            viewModel.establecerValor(valor)
            actualizarPantalla()
        }
        binding.comprobar.setOnClickListener {
            viewModel.comprobar()
            actualizarPantalla()
            animarRespuestaKenKen(binding.estado, escalaInicial = 1f, duracion = 180L)
            if (viewModel.resultado == ResultadoComprobacionKenKen.CORRECTO) {
                binding.tablero.animarVictoria()
            }
        }
        actualizarPantalla()
    }

    private fun actualizarPantalla() {
        val seleccion = viewModel.casillaSeleccionada
        val habilitado = viewModel.resultado != ResultadoComprobacionKenKen.CORRECTO
        val duplicados = viewModel.indicesDuplicados()
        binding.tablero.mostrarEstado(viewModel.obtenerValores(), seleccion, habilitado, duplicados)
        binding.controles.habilitar(habilitado && seleccion != null)
        binding.comprobar.isEnabled = habilitado
        binding.indicadores.mostrar(
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
        binding.estado.setText(mensaje)
        val color = when (viewModel.resultado) {
            ResultadoComprobacionKenKen.CORRECTO -> R.color.kenken_incrementar_tinte
            ResultadoComprobacionKenKen.INCORRECTO -> R.color.kenken_borrar_tinte
            else -> R.color.kenken_texto_secundario
        }
        binding.estado.setTextColor(ContextCompat.getColor(this, color))
    }
}
