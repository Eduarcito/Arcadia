package com.example.arcadia.juegos.kenken

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.compartido.estado.CLAVE_CONTROLES_KENKEN
import com.example.arcadia.databinding.ActivityKenkenBinding
import com.example.arcadia.databinding.HerramientasKenkenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class KenKenActivity : ArcadeActivity() {
    private lateinit var binding: ActivityKenkenBinding
    private lateinit var controles: HerramientasKenkenBinding
    private lateinit var tablero: TableroKenKenView
    private val viewModel: KenKenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tipoGuardado = preferencias.getString(CLAVE_CONTROLES_KENKEN, null)
        viewModel.cambiarTipoControl(
            TipoControlKenKen.entries.firstOrNull { it.name == tipoGuardado } ?: TipoControlKenKen.MODOS
        )
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
            val antes = viewModel.obtenerValores()
            viewModel.tocarCasilla(fila, columna)
            if (viewModel.obtenerValores() != antes) respuesta()
            actualizarPantalla()
        }
        controles.controles.alIntroducirValor { valor ->
            val antes = viewModel.obtenerValores()
            viewModel.establecerValor(valor)
            if (viewModel.obtenerValores() != antes) respuesta()
            actualizarPantalla()
        }
        controles.controles.alCambiarModo { modo ->
            viewModel.cambiarModo(modo)
            actualizarPantalla()
        }
        controles.tipoControl.setOnClickListener { mostrarSelectorControles() }
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
        controles.controles.mostrar(viewModel.tipoControl, viewModel.modoEntrada, habilitado, seleccion != null)
        controles.tipoControl.contentDescription = getString(
            R.string.kenken_controles_actual,
            getString(if (viewModel.tipoControl == TipoControlKenKen.MODOS) {
                R.string.kenken_controles_modos
            } else R.string.kenken_controles_numeros)
        )
        controles.comprobar.isEnabled = habilitado
        binding.pantalla.mostrarEstadisticas(
            getString(R.string.kenken_casillas_completadas, viewModel.obtenerValores().count { it != 0 }),
            if (duplicados.isEmpty()) {
                getString(R.string.kenken_sin_repetidos)
            } else {
                getString(R.string.kenken_repetidos_indicador, duplicados.size)
            }
        )
        val instruccion = if (viewModel.tipoControl == TipoControlKenKen.NUMEROS) {
            R.string.kenken_instruccion_teclado
        } else when (viewModel.modoEntrada) {
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
        binding.pantalla.mostrarEstado(mensaje)
        val color = when (viewModel.resultado) {
            ResultadoComprobacionKenKen.CORRECTO -> R.color.kenken_incrementar_tinte
            ResultadoComprobacionKenKen.INCORRECTO -> R.color.kenken_borrar_tinte
            else -> R.color.kenken_texto_secundario
        }
        binding.pantalla.colorEstado(color)
    }

    private fun mostrarSelectorControles() {
        val tipos = TipoControlKenKen.entries
        val opciones = arrayOf(
            getString(R.string.kenken_controles_modos),
            getString(R.string.kenken_controles_numeros)
        )
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.kenken_controles_titulo)
            .setSingleChoiceItems(opciones, tipos.indexOf(viewModel.tipoControl)) { dialogo, indice ->
                val tipo = tipos[indice]
                viewModel.cambiarTipoControl(tipo)
                preferencias.edit().putString(CLAVE_CONTROLES_KENKEN, tipo.name).apply()
                actualizarPantalla()
                dialogo.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
