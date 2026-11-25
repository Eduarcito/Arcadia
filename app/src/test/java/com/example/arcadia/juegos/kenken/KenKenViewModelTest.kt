package com.example.arcadia.juegos.kenken

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KenKenViewModelTest {
    @Test
    fun iniciaVacioSinSeleccion() {
        val modelo = KenKenViewModel()
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
        assertNull(modelo.casillaSeleccionada)
    }

    @Test
    fun escribeElValorDelTecladoEnLaCasillaSeleccionada() {
        val modelo = KenKenViewModel()
        modelo.seleccionarCasilla(1, 2)
        modelo.establecerValor(4)
        assertEquals(4, modelo.obtenerValores()[6])
        assertEquals(6, modelo.casillaSeleccionada)
    }

    @Test
    fun elTecladoNoEscribeSinCasillaSeleccionada() {
        val modelo = KenKenViewModel()
        modelo.establecerValor(3)
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
    }

    @Test
    fun corregirEsUnSoloToqueSinCiclar() {
        val modelo = KenKenViewModel()
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(1)
        modelo.establecerValor(4)
        assertEquals(4, modelo.obtenerValores()[0])
    }

    @Test
    fun borraSoloLaCasillaSeleccionada() {
        val modelo = KenKenViewModel()
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(1)
        modelo.seleccionarCasilla(3, 3)
        modelo.establecerValor(2)
        modelo.establecerValor(0)
        val esperado = MutableList(16) { 0 }
        esperado[0] = 1
        assertEquals(esperado, modelo.obtenerValores())
    }

    @Test
    fun escribirReiniciaElResultadoDeLaComprobacion() {
        val modelo = KenKenViewModel()
        modelo.comprobar()
        assertEquals(ResultadoComprobacionKenKen.INCOMPLETO, modelo.resultado)
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(1)
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
    }

    @Test
    fun ignoraCoordenadasYValoresFueraDeRango() {
        val modelo = KenKenViewModel()
        modelo.seleccionarCasilla(-1, 0)
        modelo.seleccionarCasilla(0, 4)
        assertNull(modelo.casillaSeleccionada)
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(5)
        modelo.establecerValor(-1)
        assertEquals(0, modelo.obtenerValores()[0])
    }

    @Test
    fun losValoresConsultadosNoCambianConJugadasPosteriores() {
        val modelo = KenKenViewModel()
        val valoresAnteriores = modelo.obtenerValores()
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(1)
        assertEquals(0, valoresAnteriores[0])
        assertEquals(1, modelo.obtenerValores()[0])
    }

    @Test
    fun detectaDuplicadosEnFilaYColumnaEnVivo() {
        val modelo = KenKenViewModel()
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(2)
        assertTrue(modelo.indicesDuplicados().isEmpty())
        modelo.seleccionarCasilla(0, 3)
        modelo.establecerValor(2)
        assertEquals(setOf(0, 3), modelo.indicesDuplicados())
        modelo.seleccionarCasilla(2, 0)
        modelo.establecerValor(2)
        assertEquals(setOf(0, 3, 8), modelo.indicesDuplicados())
        modelo.seleccionarCasilla(0, 3)
        modelo.establecerValor(0)
        assertEquals(setOf(0, 8), modelo.indicesDuplicados())
    }

    @Test
    fun losDuplicadosIgnoranCasillasVacias() {
        val modelo = KenKenViewModel()
        assertTrue(modelo.indicesDuplicados().isEmpty())
    }

    @Test
    fun comprobarIncorrectoPermiteCorregir() {
        val modelo = KenKenViewModel()
        for (fila in 0 until 4) {
            for (columna in 0 until 4) {
                modelo.seleccionarCasilla(fila, columna)
                modelo.establecerValor(1)
            }
        }
        modelo.comprobar()
        assertEquals(ResultadoComprobacionKenKen.INCORRECTO, modelo.resultado)
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(2)
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
    }

    @Test
    fun laVictoriaBloqueaCambiosHastaReiniciar() {
        val modelo = crearModeloResuelto()
        modelo.comprobar()
        val valores = modelo.obtenerValores()
        assertEquals(ResultadoComprobacionKenKen.CORRECTO, modelo.resultado)
        val seleccionPrevia = modelo.casillaSeleccionada
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(0)
        assertEquals(seleccionPrevia, modelo.casillaSeleccionada)
        assertEquals(valores, modelo.obtenerValores())
    }

    @Test
    fun reiniciarLimpiaValoresSeleccionYResultado() {
        val modelo = crearModeloResuelto()
        modelo.comprobar()
        modelo.reiniciar()
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
        assertNull(modelo.casillaSeleccionada)
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
    }

    @Test
    fun eligeUnaVarianteDelCatalogoAlIniciar() {
        val modelo = KenKenViewModel()
        assertTrue(crearVariantesKenKen().contains(modelo.rompecabezas))
    }

    @Test
    fun reiniciarEligeOtraVarianteDelCatalogo() {
        val variantes = crearVariantesKenKen()
        val modelo = KenKenViewModel()
        repeat(20) {
            val anterior = modelo.rompecabezas
            modelo.reiniciar()
            assertNotEquals(anterior, modelo.rompecabezas)
            assertTrue(variantes.contains(modelo.rompecabezas))
            assertEquals(List(16) { 0 }, modelo.obtenerValores())
        }
    }

    @Test
    fun jugarNoCambiaElRompecabezas() {
        val modelo = KenKenViewModel()
        val anterior = modelo.rompecabezas
        modelo.seleccionarCasilla(0, 0)
        modelo.establecerValor(1)
        modelo.comprobar()
        assertEquals(anterior, modelo.rompecabezas)
    }

    private fun crearModeloResuelto(): KenKenViewModel {
        val modelo = KenKenViewModel(listOf(crearVariantesKenKen().first()))
        val solucion = listOf(
            1, 2, 3, 4,
            2, 3, 4, 1,
            3, 4, 1, 2,
            4, 1, 2, 3
        )
        for ((indice, valor) in solucion.withIndex()) {
            modelo.seleccionarCasilla(indice / 4, indice % 4)
            modelo.establecerValor(valor)
        }
        return modelo
    }
}
