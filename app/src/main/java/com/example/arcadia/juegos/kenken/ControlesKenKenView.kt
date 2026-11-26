package com.example.arcadia.juegos.kenken

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.example.arcadia.databinding.ControlesKenkenBinding

// Agrupa el teclado numerico y los modos de entrada clasicos
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

    fun alCambiarModo(accion: (ModoEntradaKenKen) -> Unit) {
        binding.incrementar.setOnClickListener { accion(ModoEntradaKenKen.INCREMENTAR) }
        binding.decrementar.setOnClickListener { accion(ModoEntradaKenKen.DECREMENTAR) }
        binding.borrarModo.setOnClickListener { accion(ModoEntradaKenKen.BORRAR) }
    }

    fun mostrar(tipo: TipoControlKenKen, modo: ModoEntradaKenKen, habilitado: Boolean, haySeleccion: Boolean) {
        binding.teclado.isVisible = tipo == TipoControlKenKen.NUMEROS
        binding.modos.isVisible = tipo == TipoControlKenKen.MODOS
        val teclas = listOf(binding.numero1, binding.numero2, binding.numero3, binding.numero4, binding.borrar)
        for (tecla in teclas) tecla.isEnabled = habilitado && haySeleccion
        val modos = listOf(
            binding.incrementar to ModoEntradaKenKen.INCREMENTAR,
            binding.decrementar to ModoEntradaKenKen.DECREMENTAR,
            binding.borrarModo to ModoEntradaKenKen.BORRAR
        )
        for ((boton, entrada) in modos) {
            boton.isEnabled = habilitado
            boton.isChecked = modo == entrada
            val grosor = if (modo == entrada) 2 else 1
            boton.strokeWidth = (resources.displayMetrics.density * grosor).toInt()
        }
    }
}
