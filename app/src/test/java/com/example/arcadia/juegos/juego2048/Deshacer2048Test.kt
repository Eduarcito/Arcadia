package com.example.arcadia.juegos.juego2048

import com.example.arcadia.compartido.estado.Direccion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Deshacer2048Test {
    private fun modeloConJugada(): Pair<Juego2048ViewModel, Pair<List<Int>, Long>> {
        val modelo = Juego2048ViewModel()
        val antes = modelo.juego.celdas to modelo.juego.puntos
        while (!modelo.mover(Direccion.IZQUIERDA)) modelo.mover(Direccion.ARRIBA)
        return modelo to antes
    }

    @Test
    fun deshacerDevuelveTableroYPuntosAnteriores() {
        val (modelo, antes) = modeloConJugada()
        assertTrue(modelo.puedeDeshacer)
        assertTrue(modelo.deshacer())
        assertEquals(antes.first, modelo.juego.celdas)
        assertEquals(antes.second, modelo.juego.puntos)
    }

    @Test
    fun soloLosMovimientosQueCambianElTableroEntranAlHistorial() {
        val modelo = Juego2048ViewModel()
        var exitos = 0
        repeat(20) {
            for (direccion in Direccion.entries) {
                if (modelo.mover(direccion)) { exitos++; break }
            }
        }
        var deshechos = 0
        while (modelo.deshacer()) deshechos++
        assertEquals(exitos, deshechos)
    }

    @Test
    fun sinHistorialNoSePuedeDeshacer() {
        val modelo = Juego2048ViewModel()
        assertFalse(modelo.puedeDeshacer)
        assertFalse(modelo.deshacer())
    }

    @Test
    fun reiniciarLimpiaElHistorial() {
        val (modelo, _) = modeloConJugada()
        modelo.reiniciar()
        assertFalse(modelo.puedeDeshacer)
    }
}
