package com.example.arcadia.juegos.kenken

import androidx.lifecycle.ViewModel

class KenKenViewModel(
    private val variantes: List<RompecabezasKenKen> = crearVariantesKenKen()
) : ViewModel() {
    var rompecabezas = variantes.random()
        private set

    private val valores = MutableList(rompecabezas.tamano * rompecabezas.tamano) { 0 }

    var resultado: ResultadoComprobacionKenKen = ResultadoComprobacionKenKen.SIN_COMPROBAR
        private set

    var casillaSeleccionada: Int? = null
        private set

    fun obtenerValores(): List<Int> = valores.toList()

    fun seleccionarCasilla(fila: Int, columna: Int) {
        val tamano = rompecabezas.tamano
        if (resultado == ResultadoComprobacionKenKen.CORRECTO ||
            fila !in 0 until tamano || columna !in 0 until tamano
        ) {
            return
        }
        casillaSeleccionada = fila * tamano + columna
    }

    /** Pone [valor] (1..tamano) en la casilla seleccionada; 0 la borra. */
    fun establecerValor(valor: Int) {
        val indice = casillaSeleccionada ?: return
        if (resultado == ResultadoComprobacionKenKen.CORRECTO) return
        if (valor !in 0..rompecabezas.tamano) return
        if (valores[indice] != valor) {
            valores[indice] = valor
            resultado = ResultadoComprobacionKenKen.SIN_COMPROBAR
        }
    }

    /** Casillas con un valor repetido en su fila o columna, para aviso en vivo. */
    fun indicesDuplicados(): Set<Int> {
        val tamano = rompecabezas.tamano
        val duplicados = mutableSetOf<Int>()
        for (linea in 0 until tamano) {
            for (horizontal in listOf(true, false)) {
                val indices = (0 until tamano).map { if (horizontal) linea * tamano + it else it * tamano + linea }
                val ocupados = indices.filter { valores[it] != 0 }
                val repetidos = ocupados.groupBy { valores[it] }.filter { it.value.size > 1 }.values.flatten()
                duplicados.addAll(repetidos)
            }
        }
        return duplicados
    }

    fun comprobar() {
        resultado = comprobarTablero(valores, rompecabezas)
    }

    fun reiniciar() {
        val alternativas = variantes.filter { it != rompecabezas }
        rompecabezas = alternativas.randomOrNull() ?: rompecabezas
        valores.fill(0)
        casillaSeleccionada = null
        resultado = ResultadoComprobacionKenKen.SIN_COMPROBAR
    }
}
