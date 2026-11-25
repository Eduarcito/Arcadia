package com.example.arcadia.juegos.kenken

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasKenKenTest {
    private val rompecabezas = crearRompecabezasDePrueba()
    private val solucion = listOf(
        1, 2, 3, 4,
        2, 3, 4, 1,
        3, 4, 1, 2,
        4, 1, 2, 3
    )

    @Test
    fun aceptaLaSolucionDelTableroDePrueba() {
        assertEquals(ResultadoComprobacionKenKen.CORRECTO, comprobarTablero(solucion, rompecabezas))
    }

    @Test
    fun distingueTableroIncompletoYValoresInvalidos() {
        assertEquals(
            ResultadoComprobacionKenKen.INCOMPLETO,
            comprobarTablero(List(16) { 0 }, rompecabezas)
        )
        val invalido = solucion.toMutableList()
        invalido[0] = 5
        assertEquals(ResultadoComprobacionKenKen.INCORRECTO, comprobarTablero(invalido, rompecabezas))
        assertEquals(
            ResultadoComprobacionKenKen.INCORRECTO,
            comprobarTablero(listOf(1), rompecabezas)
        )
    }

    @Test
    fun rechazaFilasRepetidasAunqueLasJaulasSeanValidas() {
        val valores = listOf(
            1, 1, 3, 4,
            2, 2, 4, 1,
            3, 3, 1, 2,
            4, 4, 2, 3
        )
        assertEquals(
            ResultadoComprobacionKenKen.INCORRECTO,
            comprobarTablero(valores, crearJaulasIndividuales(valores))
        )
    }

    @Test
    fun rechazaColumnasRepetidasAunqueLasJaulasSeanValidas() {
        val valores = List(16) { it % 4 + 1 }
        assertEquals(
            ResultadoComprobacionKenKen.INCORRECTO,
            comprobarTablero(valores, crearJaulasIndividuales(valores))
        )
    }

    @Test
    fun rechazaJaulasInvalidasAunqueFilasYColumnasSeanValidas() {
        val valores = solucion.map { it % 4 + 1 }
        assertEquals(
            ResultadoComprobacionKenKen.INCORRECTO,
            comprobarTablero(valores, rompecabezas)
        )
    }

    @Test
    fun sumaYMultiplicacionUsanTodosLosValores() {
        assertTrue(cumpleJaula(listOf(2, 3, 3), 8, OperacionKenKen.SUMA))
        assertFalse(cumpleJaula(listOf(2, 3, 3), 5, OperacionKenKen.SUMA))
        assertTrue(cumpleJaula(listOf(4, 1, 2), 8, OperacionKenKen.MULTIPLICACION))
        assertFalse(cumpleJaula(listOf(4, 1, 2), 4, OperacionKenKen.MULTIPLICACION))
    }

    @Test
    fun restaYDivisionSonIndependientesDelOrden() {
        assertTrue(cumpleJaula(listOf(1, 4), 3, OperacionKenKen.RESTA))
        assertTrue(cumpleJaula(listOf(4, 1), 3, OperacionKenKen.RESTA))
        assertTrue(cumpleJaula(listOf(2, 4), 2, OperacionKenKen.DIVISION))
        assertTrue(cumpleJaula(listOf(4, 2), 2, OperacionKenKen.DIVISION))
        assertFalse(cumpleJaula(listOf(3, 2), 1, OperacionKenKen.DIVISION))
    }

    @Test
    fun exigeDosCasillasParaRestaYDivision() {
        assertFalse(cumpleJaula(listOf(4, 2, 1), 1, OperacionKenKen.RESTA))
        assertFalse(cumpleJaula(listOf(4, 2, 1), 2, OperacionKenKen.DIVISION))
        assertFalse(cumpleJaula(listOf(4, 0), 2, OperacionKenKen.DIVISION))
        assertFalse(cumpleJaula(emptyList(), 0, OperacionKenKen.SUMA))
    }

    @Test
    fun unaJaulaIndividualExigeSuObjetivo() {
        assertTrue(cumpleJaula(listOf(3), 3, OperacionKenKen.VALOR))
        assertFalse(cumpleJaula(listOf(2), 3, OperacionKenKen.VALOR))
        assertFalse(cumpleJaula(listOf(1, 2), 3, OperacionKenKen.VALOR))
    }

    @Test
    fun cadaCasillaPerteneceAUnaJaulaContigua() {
        val indices = rompecabezas.jaulas.flatMap { it.casillas }
        assertEquals((0 until 16).toList(), indices.sorted())
        for (jaula in rompecabezas.jaulas) {
            val pendientes = jaula.casillas.toMutableSet()
            val visitadas = mutableListOf(jaula.casillas.first())
            pendientes.remove(visitadas.first())
            var posicion = 0
            while (posicion < visitadas.size) {
                val indice = visitadas[posicion]
                for (vecina in jaula.casillas) {
                    val distancia = kotlin.math.abs(indice / 4 - vecina / 4) +
                        kotlin.math.abs(indice % 4 - vecina % 4)
                    if (distancia == 1 && pendientes.remove(vecina)) {
                        visitadas.add(vecina)
                    }
                }
                posicion++
            }
            assertTrue(pendientes.isEmpty())
        }
    }

    @Test
    fun elRompecabezasDePruebaTieneUnaSolaSolucion() {
        assertEquals(1, contarSoluciones(MutableList(16) { 0 }, 0))
    }

    private fun crearJaulasIndividuales(valores: List<Int>): RompecabezasKenKen {
        val jaulas = valores.mapIndexed { indice, valor ->
            JaulaKenKen(listOf(indice), valor, OperacionKenKen.VALOR)
        }
        return RompecabezasKenKen(4, jaulas)
    }

    private fun contarSoluciones(valores: MutableList<Int>, indice: Int): Int {
        if (indice == valores.size) {
            return if (comprobarTablero(valores, rompecabezas) ==
                ResultadoComprobacionKenKen.CORRECTO
            ) 1 else 0
        }
        var cantidad = 0
        for (valor in 1..4) {
            var disponible = true
            for (posicion in 0 until 4) {
                if (valores[indice / 4 * 4 + posicion] == valor ||
                    valores[posicion * 4 + indice % 4] == valor
                ) {
                    disponible = false
                }
            }
            if (disponible) {
                valores[indice] = valor
                cantidad += contarSoluciones(valores, indice + 1)
                valores[indice] = 0
            }
        }
        return cantidad
    }
}
