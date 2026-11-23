package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.arcadia.R
import com.example.arcadia.databinding.ContenidoTarjetaJuegoBinding
import com.google.android.material.card.MaterialCardView

class TarjetaJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : MaterialCardView(contexto, atributos) {

    private val binding = ContenidoTarjetaJuegoBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // Evita compartir los IDs internos entre instancias de la tarjeta
        binding.txvNombreJuego.id = View.generateViewId()
        binding.txvDescripcionJuego.id = View.generateViewId()
        binding.btnJugar.id = View.generateViewId()

        context.withStyledAttributes(atributos, R.styleable.TarjetaJuegoView) {
            val titulo = getText(R.styleable.TarjetaJuegoView_tituloJuego)
            binding.txvNombreJuego.text = titulo
            binding.txvDescripcionJuego.text = getText(R.styleable.TarjetaJuegoView_descripcionJuego)
            if (!titulo.isNullOrBlank()) {
                binding.btnJugar.contentDescription = context.getString(R.string.catalogo_jugar_a, titulo)
            }
        }
    }

    fun alJugar(accion: () -> Unit) {
        binding.btnJugar.setOnClickListener { accion() }
    }
}
