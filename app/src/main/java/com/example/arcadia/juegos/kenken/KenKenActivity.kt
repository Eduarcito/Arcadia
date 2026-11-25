package com.example.arcadia.juegos.kenken

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.aplicarMargenesDelSistema
import com.example.arcadia.databinding.ActivityKenkenBinding

class KenKenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKenkenBinding
    private val viewModel: KenKenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKenkenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarMargenesDelSistema()
        binding.encabezado.alVolver { finish() }
        binding.encabezado.alReiniciar {
            viewModel.reiniciar()
            binding.tablero.mostrarJaulas(viewModel.rompecabezas)
            actualizarPantalla()
        }
        binding.tablero.mostrarJaulas(viewModel.rompecabezas)
        binding.tablero.alSeleccionarCasilla { fila, columna ->
            viewModel.aplicarModoEnCasilla(fila, columna)
            actualizarPantalla()
        }
        binding.controles.alCambiarModo { modo ->
            viewModel.cambiarModo(modo)
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
        binding.tablero.mostrarEstado(viewModel.obtenerValores(), seleccion, habilitado)
        binding.controles.mostrarModo(viewModel.modoEntrada)
        binding.controles.habilitar(habilitado)
        binding.comprobar.isEnabled = habilitado
        val instruccion = when (viewModel.modoEntrada) {
            ModoEntradaKenKen.INCREMENTAR -> R.string.kenken_instruccion_incrementar
            ModoEntradaKenKen.DECREMENTAR -> R.string.kenken_instruccion_decrementar
            ModoEntradaKenKen.BORRAR -> R.string.kenken_instruccion_borrar
        }
        val mensaje = when (viewModel.resultado) {
            ResultadoComprobacionKenKen.SIN_COMPROBAR -> instruccion
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
