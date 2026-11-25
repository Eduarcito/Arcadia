package com.example.arcadia.compartido.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import com.example.arcadia.R
import com.example.arcadia.databinding.IndicadoresJuegoBinding

class IndicadoresJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val binding = IndicadoresJuegoBinding.inflate(LayoutInflater.from(contexto), this, true)

    init {
        var acento = contexto.getColor(R.color.acento_2048)
        contexto.withStyledAttributes(atributos, R.styleable.IndicadoresJuegoView) {
            acento = getColor(R.styleable.IndicadoresJuegoView_acentoBloque, acento)
            val tamano = getDimension(
                R.styleable.IndicadoresJuegoView_tamanoIndicador, binding.statLeft.textSize
            )
            binding.statLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, tamano)
            binding.statRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, tamano)
        }
        mostrarAcento(acento)
    }

    fun mostrarAcento(acento: Int) {
        val oscuro = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        binding.statLeft.setTextColor(
            if (oscuro) acento else ColorUtils.blendARGB(acento, Color.BLACK, .48f)
        )
        binding.statLeft.background = fondoTarjetaJuego(context, acento)
        binding.statRight.background = fondoTarjetaJuego(context, acento)
    }

    fun mostrar(izquierda: String, derecha: String) {
        binding.statLeft.text = formato(izquierda)
        binding.statRight.text = formato(derecha)
    }

    init {
        listOf(binding.statLeft, binding.statRight).forEach {
            it.gravity = android.view.Gravity.CENTER
            it.setLineSpacing(0f, 1.15f)
        }
    }

    fun describirDerecha(texto: String) {
        binding.statRight.contentDescription = texto
    }

    fun compactar(tamano: Float = 24f, padding: Int = 12) {
        val margen = (padding * resources.displayMetrics.density).toInt()
        listOf(binding.statLeft, binding.statRight).forEach {
            it.textSize = tamano
            it.setPadding(margen, margen, margen, margen)
        }
    }

    private fun formato(texto: String): SpannableString {
        return SpannableString(texto).apply {
            val fin = texto.indexOf('\n')
            if (fin > 0) {
                setSpan(RelativeSizeSpan(.7f), 0, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(
                    ForegroundColorSpan(context.getColor(R.color.arcade_muted)),
                    0, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}
