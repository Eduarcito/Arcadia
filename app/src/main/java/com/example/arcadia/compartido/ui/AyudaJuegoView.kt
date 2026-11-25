package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.example.arcadia.R
import com.example.arcadia.databinding.AyudaJuegoBinding

class AyudaJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val binding = AyudaJuegoBinding.inflate(LayoutInflater.from(contexto), this, true)

    init {
        var acento = contexto.getColor(R.color.acento_2048)
        contexto.withStyledAttributes(atributos, R.styleable.AyudaJuegoView) {
            binding.instructions.text = getText(R.styleable.AyudaJuegoView_textoAyuda)
            acento = getColor(R.styleable.AyudaJuegoView_acentoBloque, acento)
        }
        mostrarAcento(acento)
    }

    fun mostrarTexto(texto: CharSequence?) {
        binding.instructions.text = texto
    }

    fun mostrarAcento(acento: Int) {
        background = fondoTarjetaJuego(context, acento)
    }
}
