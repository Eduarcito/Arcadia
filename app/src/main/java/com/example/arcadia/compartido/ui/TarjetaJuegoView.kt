package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.arcadia.R
import com.example.arcadia.databinding.TarjetaArcadeBinding
import com.google.android.material.card.MaterialCardView

class TarjetaJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : MaterialCardView(contexto, atributos) {

    init {
        var titulo: CharSequence? = null
        var tinta = 0
        var portada = 0
        context.withStyledAttributes(atributos, R.styleable.TarjetaJuegoView) {
            titulo = getText(R.styleable.TarjetaJuegoView_tituloJuego)
            tinta = getColor(R.styleable.TarjetaJuegoView_tintaTarjeta, 0)
            portada = getInt(R.styleable.TarjetaJuegoView_indicePortada, 0)
        }
        val binding = TarjetaArcadeBinding.inflate(LayoutInflater.from(context), this, true)
        binding.title.text = titulo
        binding.title.setTextColor(tinta)
        binding.accent.setBackgroundColor(tinta)
        binding.play.setTextColor(tinta)
        binding.art.juego = portada
        binding.root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        contentDescription = context.getString(
            R.string.catalogo_jugar_a, titulo.toString().replace("\n", "")
        )
    }

    fun alJugar(accion: () -> Unit) {
        setOnClickListener { accion() }
    }
}
