package com.example.arcadia.juegos.kenken

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KenKenViewModelTest {
    @Test
    fun iniciaVacioConIncrementoActivo() {
        val modelo = KenKenViewModel()
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
        assertNull(modelo.casillaSeleccionada)
        assertEquals(ModoEntradaKenKen.INCREMENTAR, modelo.modoEntrada)
    }

    @Test
    fun incrementaDesdeUnoSinSuperarCuatro() {
        val modelo = KenKenViewModel()
        for (valor in 1..4) {
            modelo.aplicarModoEnCasilla(1, 2)
            assertEquals(valor, modelo.obtenerValores()[6])
        }
        modelo.aplicarModoEnCasilla(1, 2)
        assertEquals(4, modelo.obtenerValores()[6])
        assertEquals(6, modelo.casillaSeleccionada)
    }

    @Test
    fun decrementaDesdeVacioHastaUnoSinBorrar() {
        val modelo = KenKenViewModel()
        modelo.cambiarModo(ModoEntradaKenKen.DECREMENTAR)
        for (valor in 4 downTo 1) {
            modelo.aplicarModoEnCasilla(0, 0)
            assertEquals(valor, modelo.obtenerValores()[0])
        }
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(1, modelo.obtenerValores()[0])
    }

    @Test
    fun borraSoloLaCasillaTocada() {
        val modelo = KenKenViewModel()
        modelo.aplicarModoEnCasilla(0, 0)
        modelo.aplicarModoEnCasilla(3, 3)
        modelo.cambiarModo(ModoEntradaKenKen.BORRAR)
        modelo.aplicarModoEnCasilla(3, 3)
        modelo.aplicarModoEnCasilla(2, 2)
        val esperado = MutableList(16) { 0 }
        esperado[0] = 1
        assertEquals(esperado, modelo.obtenerValores())
    }

    @Test
    fun cambiarModoNoModificaValoresNiSeleccion() {
        val modelo = KenKenViewModel()
        modelo.aplicarModoEnCasilla(1, 1)
        val valores = modelo.obtenerValores()
        modelo.cambiarModo(ModoEntradaKenKen.DECREMENTAR)
        assertEquals(valores, modelo.obtenerValores())
        assertEquals(5, modelo.casillaSeleccionada)
        assertEquals(ModoEntradaKenKen.DECREMENTAR, modelo.modoEntrada)
    }

    @Test
    fun aplicaElModoActualSobreValoresExistentes() {
        val modelo = KenKenViewModel()
        modelo.cambiarModo(ModoEntradaKenKen.DECREMENTAR)
        modelo.aplicarModoEnCasilla(0, 0)
        modelo.aplicarModoEnCasilla(0, 0)
        modelo.cambiarModo(ModoEntradaKenKen.INCREMENTAR)
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(4, modelo.obtenerValores()[0])
    }

    @Test
    fun ignoraCoordenadasFueraDelTablero() {
        val modelo = KenKenViewModel()
        modelo.aplicarModoEnCasilla(-1, 0)
        modelo.aplicarModoEnCasilla(0, 4)
        modelo.aplicarModoEnCasilla(4, 0)
        assertNull(modelo.casillaSeleccionada)
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
    }

    @Test
    fun losValoresConsultadosNoCambianConJugadasPosteriores() {
        val modelo = KenKenViewModel()
        val valoresAnteriores = modelo.obtenerValores()
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(0, valoresAnteriores[0])
        assertEquals(1, modelo.obtenerValores()[0])
    }

    @Test
    fun comprobarIncompletoPermiteSeguirJugando() {
        val modelo = KenKenViewModel()
        modelo.comprobar()
        assertEquals(ResultadoComprobacionKenKen.INCOMPLETO, modelo.resultado)
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(1, modelo.obtenerValores()[0])
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
    }

    @Test
    fun comprobarIncorrectoPermiteCorregir() {
        val modelo = KenKenViewModel()
        for (fila in 0 until 4) {
            for (columna in 0 until 4) {
                modelo.aplicarModoEnCasilla(fila, columna)
            }
        }
        modelo.comprobar()
        assertEquals(ResultadoComprobacionKenKen.INCORRECTO, modelo.resultado)
        modelo.cambiarModo(ModoEntradaKenKen.BORRAR)
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(0, modelo.obtenerValores()[0])
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
    }

    @Test
    fun laVictoriaBloqueaCambiosHastaReiniciar() {
        val modelo = crearModeloResuelto()
        modelo.comprobar()
        val valores = modelo.obtenerValores()
        assertEquals(ResultadoComprobacionKenKen.CORRECTO, modelo.resultado)
        modelo.cambiarModo(ModoEntradaKenKen.BORRAR)
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(valores, modelo.obtenerValores())
        assertEquals(ModoEntradaKenKen.INCREMENTAR, modelo.modoEntrada)
        assertEquals(ResultadoComprobacionKenKen.CORRECTO, modelo.resultado)
    }

    @Test
    fun reiniciarLimpiaValoresSeleccionModoYResultado() {
        val modelo = crearModeloResuelto()
        modelo.comprobar()
        modelo.reiniciar()
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
        assertNull(modelo.casillaSeleccionada)
        assertEquals(ModoEntradaKenKen.INCREMENTAR, modelo.modoEntrada)
        assertEquals(ResultadoComprobacionKenKen.SIN_COMPROBAR, modelo.resultado)
        modelo.aplicarModoEnCasilla(0, 0)
        assertEquals(1, modelo.obtenerValores()[0])
    }

    @Test
    fun reiniciarTambienRestableceElModoDuranteLaPartida() {
        val modelo = KenKenViewModel()
        modelo.cambiarModo(ModoEntradaKenKen.DECREMENTAR)
        modelo.aplicarModoEnCasilla(0, 0)
        modelo.reiniciar()
        assertEquals(ModoEntradaKenKen.INCREMENTAR, modelo.modoEntrada)
        assertEquals(List(16) { 0 }, modelo.obtenerValores())
    }

    private fun crearModeloResuelto(): KenKenViewModel {
        val modelo = KenKenViewModel()
        val solucion = listOf(
            1, 2, 3, 4,
            2, 3, 4, 1,
            3, 4, 1, 2,
            4, 1, 2, 3
        )
        for ((indice, valor) in solucion.withIndex()) {
            repeat(valor) {
                modelo.aplicarModoEnCasilla(indice / 4, indice % 4)
            }
        }
        return modelo
    }
}
