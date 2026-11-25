package com.example.arcadia.compartido.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.arcadia.R
import com.example.arcadia.databinding.ActivityArcadeGameBinding
import com.example.arcadia.databinding.ContenidoPartidaCompactaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PantallaPartidaView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : ConstraintLayout(contexto, atributos) {
    private val binding = ActivityArcadeGameBinding.inflate(LayoutInflater.from(contexto), this, true)
    val tablero: TableroArcadeView get() = binding.board
    private var compacta: ContenidoPartidaCompactaBinding? = null
    private var volver: (() -> Unit)? = null
    private var reiniciar: (() -> Unit)? = null
    private var instrucciones: CharSequence? = null

    init {
        var acento = contexto.getColor(R.color.acento_2048)
        contexto.withStyledAttributes(atributos, R.styleable.PantallaPartidaView) {
            binding.title.text = getText(R.styleable.PantallaPartidaView_tituloPartida)
            binding.ayuda.mostrarTexto(getText(R.styleable.PantallaPartidaView_instruccionesPartida))
            instrucciones = getText(R.styleable.PantallaPartidaView_instruccionesPartida)
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

    fun alVolver(accion: () -> Unit) { volver = accion; binding.encabezado.alVolver(accion) }

    fun usarPantallaCompleta(tableroAlternativo: View? = null, herramientas: View? = null, previsualizacion: View? = null) {
        if (compacta != null) return
        val nueva = ContenidoPartidaCompactaBinding.inflate(LayoutInflater.from(context), this, false)
        (binding.indicadores.parent as ViewGroup).removeView(binding.indicadores)
        binding.indicadores.compactar(20f, 8)
        nueva.marcadores.addView(binding.indicadores)
        val vistaTablero = tableroAlternativo ?: binding.board
        (vistaTablero.parent as? ViewGroup)?.removeView(vistaTablero)
        nueva.zonaTablero.addView(vistaTablero, FrameLayout.LayoutParams(-1, -1, Gravity.CENTER))
        herramientas?.let { nueva.herramientas.addView(it) }
        previsualizacion?.let { nueva.previsualizacion.addView(it) }
        nueva.nombre.text = binding.title.text
        nueva.gesto.text = binding.gestureHint.text
        nueva.status.text = binding.status.text
        nueva.back.setOnClickListener { volver?.invoke() }
        nueva.restart.setOnClickListener { reiniciar?.invoke() }
        nueva.ayuda.setOnClickListener {
            MaterialAlertDialogBuilder(context).setTitle(R.string.arcade_help_title)
                .setMessage(instrucciones).setPositiveButton(android.R.string.ok, null).show()
        }
        removeView(binding.root)
        addView(nueva.root, LayoutParams(0, 0).apply {
            topToTop = LayoutParams.PARENT_ID; bottomToBottom = LayoutParams.PARENT_ID
            startToStart = LayoutParams.PARENT_ID; endToEnd = LayoutParams.PARENT_ID
        })
        compacta = nueva
    }

    fun mostrarReglasVisuales(vista: View) {
        binding.reglasVisuales.removeAllViews()
        binding.reglasVisuales.addView(vista)
        binding.reglasVisuales.isVisible = true
    }

    /** Opt-in for games with illustrated rules above the board. */
    fun compactarCabecera() {
        fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()
        binding.title.textSize = 28f
        binding.title.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = dp(4); bottomMargin = dp(12)
        }
        binding.indicadores.compactar()
        binding.reglasVisuales.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = dp(8) }
        binding.gestureHint.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = dp(8); bottomMargin = dp(8)
        }
    }

    fun alReiniciar(accion: () -> Unit) { reiniciar = accion; binding.encabezado.alReiniciar(accion) }

    fun alSiguienteNivel(accion: () -> Unit) {
        binding.siguienteNivel.setOnClickListener { accion() }
    }

    fun mostrarSiguienteNivel(visible: Boolean) {
        binding.siguienteNivel.isVisible = visible
    }

    fun mostrarEstado(recurso: Int) {
        binding.status.setText(recurso)
        compacta?.status?.setText(recurso)
    }

    fun mostrarEstado(texto: String) {
        binding.status.text = texto
        compacta?.status?.text = texto
    }

    fun describirEstadisticaDerecha(texto: String) {
        binding.indicadores.describirDerecha(texto)
    }

    fun mostrarEstadisticas(izquierda: String, derecha: String) {
        binding.indicadores.mostrar(izquierda, derecha)
    }
}
