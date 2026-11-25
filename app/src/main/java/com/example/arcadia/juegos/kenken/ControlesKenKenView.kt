package com.example.arcadia.juegos.kenken

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.arcadia.databinding.ControlesKenkenBinding

/** Teclado numérico de KenKen: un toque por valor sobre la casilla seleccionada. */
class ControlesKenKenView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val binding = ControlesKenkenBinding.inflate(LayoutInflater.from(contexto), this, true)

    fun alIntroducirValor(accion: (Int) -> Unit) {
        val teclas = listOf(
            binding.numero1 to 1, binding.numero2 to 2, binding.numero3 to 3, binding.numero4 to 4,
            binding.borrar to 0
        )
        for ((tecla, valor) in teclas) {
            tecla.setOnClickListener { accion(valor) }
        }
    }

    fun habilitar(habilitado: Boolean) {
        binding.numero1.isEnabled = habilitado
        binding.numero2.isEnabled = habilitado
        binding.numero3.isEnabled = habilitado
        binding.numero4.isEnabled = habilitado
        binding.borrar.isEnabled = habilitado
    }
}
