package com.example.arcadia.juegos.kenken

import androidx.lifecycle.ViewModel

class KenKenViewModel : ViewModel() {
    val rompecabezas = crearRompecabezasDePrueba()
    private val valores = MutableList(rompecabezas.tamano * rompecabezas.tamano) { 0 }

    var resultado: ResultadoComprobacionKenKen = ResultadoComprobacionKenKen.SIN_COMPROBAR
        private set

    var casillaSeleccionada: Int? = null
        private set

    var modoEntrada: ModoEntradaKenKen = ModoEntradaKenKen.INCREMENTAR
        private set

    fun obtenerValores(): List<Int> = valores.toList()

    fun cambiarModo(modo: ModoEntradaKenKen) {
        if (resultado != ResultadoComprobacionKenKen.CORRECTO) {
            modoEntrada = modo
        }
    }

    fun aplicarModoEnCasilla(fila: Int, columna: Int) {
        val tamano = rompecabezas.tamano
        if (resultado == ResultadoComprobacionKenKen.CORRECTO ||
            fila !in 0 until tamano || columna !in 0 until tamano
        ) {
            return
        }

        val indice = fila * tamano + columna
        casillaSeleccionada = indice
        val valor = valores[indice]
        valores[indice] = when (modoEntrada) {
            ModoEntradaKenKen.INCREMENTAR -> if (valor < tamano) valor + 1 else tamano
            ModoEntradaKenKen.DECREMENTAR -> when {
                valor == 0 -> tamano
                valor > 1 -> valor - 1
                else -> 1
            }
            ModoEntradaKenKen.BORRAR -> 0
        }
        if (valores[indice] != valor) {
            resultado = ResultadoComprobacionKenKen.SIN_COMPROBAR
        }
    }

    fun comprobar() {
        resultado = comprobarTablero(valores, rompecabezas)
    }

    fun reiniciar() {
        valores.fill(0)
        casillaSeleccionada = null
        modoEntrada = ModoEntradaKenKen.INCREMENTAR
        resultado = ResultadoComprobacionKenKen.SIN_COMPROBAR
    }
}
