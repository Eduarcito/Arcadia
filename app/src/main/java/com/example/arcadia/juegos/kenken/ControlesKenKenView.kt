package com.example.arcadia.juegos.kenken

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.arcadia.R
import com.example.arcadia.databinding.ControlesKenkenBinding

class ControlesKenKenView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val binding = ControlesKenkenBinding.inflate(LayoutInflater.from(contexto), this, true)
    private var modoMostrado: ModoEntradaKenKen? = null

    fun alCambiarModo(accion: (ModoEntradaKenKen) -> Unit) {
        val modos = listOf(
            binding.incrementar to ModoEntradaKenKen.INCREMENTAR,
            binding.decrementar to ModoEntradaKenKen.DECREMENTAR,
            binding.borrar to ModoEntradaKenKen.BORRAR
        )
        for ((boton, modo) in modos) {
            boton.setOnClickListener {
                mostrarModo(modo)
                accion(modo)
            }
        }
    }

    fun mostrarModo(modo: ModoEntradaKenKen) {
        val identificador = when (modo) {
            ModoEntradaKenKen.INCREMENTAR -> R.id.incrementar
            ModoEntradaKenKen.DECREMENTAR -> R.id.decrementar
            ModoEntradaKenKen.BORRAR -> R.id.borrar
        }
        val botones = listOf(binding.incrementar, binding.decrementar, binding.borrar)
        for (boton in botones) {
            boton.isChecked = boton.id == identificador
            val grosor = if (boton.id == identificador) {
                R.dimen.grosor_modo_activo_kenken
            } else {
                R.dimen.grosor_modo_inactivo_kenken
            }
            boton.strokeWidth = resources.getDimensionPixelSize(grosor)
            if (boton.id == identificador && modoMostrado != null && modoMostrado != modo) {
                animarRespuestaKenKen(boton, escalaInicial = 0.97f)
            }
        }
        modoMostrado = modo
    }

    fun habilitar(habilitado: Boolean) {
        binding.incrementar.isEnabled = habilitado
        binding.decrementar.isEnabled = habilitado
        binding.borrar.isEnabled = habilitado
    }
}
