package com.example.arcadia.compartido.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.example.arcadia.R
import com.example.arcadia.databinding.EncabezadoJuegoBinding

class EncabezadoJuegoView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val binding = EncabezadoJuegoBinding.inflate(LayoutInflater.from(contexto), this, true)

    init {
        contexto.withStyledAttributes(atributos, R.styleable.EncabezadoJuegoView) {
            binding.titulo.text = getText(R.styleable.EncabezadoJuegoView_tituloEncabezado)
            binding.titulo.isVisible =
                getBoolean(R.styleable.EncabezadoJuegoView_mostrarTitulo, true)
            binding.btnReiniciar.isVisible =
                getBoolean(R.styleable.EncabezadoJuegoView_mostrarReiniciar, false)
        }
    }

    fun alVolver(accion: () -> Unit) {
        binding.btnVolver.setOnClickListener { accion() }
    }

    fun alReiniciar(accion: () -> Unit) {
        binding.btnReiniciar.setOnClickListener { accion() }
    }
}
