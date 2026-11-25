package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.example.arcadia.R
import com.example.arcadia.databinding.EncabezadoArcadeBinding

class EncabezadoJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val volver: View
    private val reiniciar: View
    private var titulo: android.widget.TextView? = null

    fun mostrarTitulo(texto: CharSequence) {
        titulo?.text = texto
    }

    init {
        var mostrarReiniciar = false
        contexto.withStyledAttributes(atributos, R.styleable.EncabezadoJuegoView) {
            mostrarReiniciar = getBoolean(R.styleable.EncabezadoJuegoView_mostrarReiniciar, false)
        }
        val binding = EncabezadoArcadeBinding.inflate(LayoutInflater.from(contexto), this, true)
        titulo = binding.brand
        volver = binding.back
        reiniciar = binding.restart
        reiniciar.isVisible = mostrarReiniciar
    }

    fun alVolver(accion: () -> Unit) {
        volver.setOnClickListener { accion() }
    }

    fun alReiniciar(accion: () -> Unit) {
        reiniciar.setOnClickListener { accion() }
    }
}
