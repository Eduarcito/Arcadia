package com.example.arcadia.compartido.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.arcadia.R
import com.example.arcadia.databinding.ActivityArcadeGameBinding

class PantallaPartidaView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : ConstraintLayout(contexto, atributos) {
    private val binding = ActivityArcadeGameBinding.inflate(LayoutInflater.from(contexto), this, true)
    val tablero: TableroArcadeView get() = binding.board

    init {
        var acento = contexto.getColor(R.color.acento_2048)
        contexto.withStyledAttributes(atributos, R.styleable.PantallaPartidaView) {
            binding.title.text = getText(R.styleable.PantallaPartidaView_tituloPartida)
            binding.ayuda.mostrarTexto(getText(R.styleable.PantallaPartidaView_instruccionesPartida))
            binding.heroCaption.text = getText(R.styleable.PantallaPartidaView_lemaPartida)
            binding.gestureHint.text = getText(R.styleable.PantallaPartidaView_gestoPartida)
            acento = getColor(R.styleable.PantallaPartidaView_acentoPartida, acento)
        }
        ViewCompat.setAccessibilityHeading(binding.title, true)
        val oscuro = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        val tinta = if (oscuro) acento else ColorUtils.blendARGB(acento, Color.BLACK, .48f)
        binding.heroCaption.setTextColor(tinta)
        binding.indicadores.mostrarAcento(acento)
        binding.ayuda.mostrarAcento(acento)
    }

    fun alVolver(accion: () -> Unit) = binding.encabezado.alVolver(accion)

    fun alReiniciar(accion: () -> Unit) = binding.encabezado.alReiniciar(accion)

    fun alSiguienteNivel(accion: () -> Unit) {
        binding.siguienteNivel.setOnClickListener { accion() }
    }

    fun mostrarSiguienteNivel(visible: Boolean) {
        binding.siguienteNivel.isVisible = visible
    }

    fun mostrarEstado(recurso: Int) {
        binding.status.setText(recurso)
    }

    fun mostrarEstado(texto: String) {
        binding.status.text = texto
    }

    fun describirEstadisticaDerecha(texto: String) {
        binding.indicadores.describirDerecha(texto)
    }

    fun mostrarEstadisticas(izquierda: String, derecha: String) {
        binding.indicadores.mostrar(izquierda, derecha)
    }
}
