package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.arcadia.R
import com.example.arcadia.databinding.ContenidoTarjetaJuegoBinding
import com.example.arcadia.databinding.TarjetaArcadeBinding
import com.google.android.material.card.MaterialCardView

class TarjetaJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : MaterialCardView(contexto, atributos) {

    private val controlJugar: View

    init {
        var estiloArcade = false
        var titulo: CharSequence? = null
        var descripcion: CharSequence? = null
        var tinta = 0
        var portada = 0
        context.withStyledAttributes(atributos, R.styleable.TarjetaJuegoView) {
            estiloArcade = getBoolean(R.styleable.TarjetaJuegoView_estiloArcade, false)
            titulo = getText(R.styleable.TarjetaJuegoView_tituloJuego)
            descripcion = getText(R.styleable.TarjetaJuegoView_descripcionJuego)
            tinta = getColor(R.styleable.TarjetaJuegoView_tintaTarjeta, 0)
            portada = getInt(R.styleable.TarjetaJuegoView_indicePortada, 0)
        }
        val inflador = LayoutInflater.from(context)
        if (estiloArcade) {
            val binding = TarjetaArcadeBinding.inflate(inflador, this, true)
            binding.title.text = titulo
            binding.title.setTextColor(tinta)
            binding.accent.setBackgroundColor(tinta)
            binding.play.setTextColor(tinta)
            binding.art.juego = portada
            binding.root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            contentDescription = context.getString(
                R.string.catalogo_jugar_a, titulo.toString().replace("\n", "")
            )
            controlJugar = this
        } else {
            val binding = ContenidoTarjetaJuegoBinding.inflate(inflador, this, true)
            // Evita compartir los IDs internos entre instancias de la tarjeta
            binding.txvNombreJuego.id = View.generateViewId()
            binding.txvDescripcionJuego.id = View.generateViewId()
            binding.btnJugar.id = View.generateViewId()
            binding.txvNombreJuego.text = titulo
            binding.txvDescripcionJuego.text = descripcion
            if (!titulo.isNullOrBlank()) {
                binding.btnJugar.contentDescription = context.getString(R.string.catalogo_jugar_a, titulo)
            }
            controlJugar = binding.btnJugar
        }
    }

    fun alJugar(accion: () -> Unit) {
        controlJugar.setOnClickListener { accion() }
    }
}
