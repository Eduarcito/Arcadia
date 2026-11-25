package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.example.arcadia.R
import com.example.arcadia.databinding.EncabezadoJuegoBinding
import com.example.arcadia.databinding.EncabezadoArcadeBinding

class EncabezadoJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val volver: View
    private val reiniciar: View

    init {
        var estiloArcade = false
        var titulo: CharSequence? = null
        var mostrarTitulo = true
        var mostrarReiniciar = false
        contexto.withStyledAttributes(atributos, R.styleable.EncabezadoJuegoView) {
            estiloArcade = getBoolean(R.styleable.EncabezadoJuegoView_estiloArcade, false)
            titulo = getText(R.styleable.EncabezadoJuegoView_tituloEncabezado)
            mostrarTitulo = getBoolean(R.styleable.EncabezadoJuegoView_mostrarTitulo, true)
            mostrarReiniciar = getBoolean(R.styleable.EncabezadoJuegoView_mostrarReiniciar, false)
        }
        val inflador = LayoutInflater.from(contexto)
        if (estiloArcade) {
            val binding = EncabezadoArcadeBinding.inflate(inflador, this, true)
            volver = binding.back
            reiniciar = binding.restart
        } else {
            val binding = EncabezadoJuegoBinding.inflate(inflador, this, true)
            binding.titulo.text = titulo
            binding.titulo.isVisible = mostrarTitulo
            volver = binding.btnVolver
            reiniciar = binding.btnReiniciar
        }
        reiniciar.isVisible = mostrarReiniciar
    }

    fun alVolver(accion: () -> Unit) {
        volver.setOnClickListener { accion() }
    }

    fun alReiniciar(accion: () -> Unit) {
        reiniciar.setOnClickListener { accion() }
    }
}
