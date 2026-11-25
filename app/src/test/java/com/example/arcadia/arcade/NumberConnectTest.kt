package com.example.arcadia.arcade

import com.example.arcadia.juegos.numberconnect.ReglasNumberConnect
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class NumberConnectTest {
    @Test fun inicioTiene35FichasYUnaJugada() {
        repeat(20) { val g=ReglasNumberConnect(Random(it));assertEquals(35,g.celdas.size);assertFalse(g.terminado);assertEquals(0L,g.puntos) }
    }
    @Test fun combinaParejaEnUltimaCasillaYCaenLasColumnas() {
        val g=ReglasNumberConnect(Random(1));val antes=List(35){if(it in 30..31)8 else 2}
        g.restaurar(antes,100);assertTrue(g.combinar(listOf(30,31)))
        assertEquals(16,g.celdas[31]);assertEquals(2,g.celdas[30]);assertEquals(116L,g.puntos)
        assertEquals(35,g.celdas.size)
        for(i in 0..34)if(i%5!=0 && i!=31)assertEquals(antes[i],g.celdas[i])
    }
    @Test fun tresFichasDiagonalesRedondeanALaSiguientePotencia() {
        val g=ReglasNumberConnect();g.restaurar(List(35){8},0)
        assertEquals(32,g.resultado(listOf(24,28,32)));assertTrue(g.combinar(listOf(24,28,32)))
        assertEquals(32,g.celdas[32]);assertEquals(32L,g.puntos)
    }
    @Test fun rechazaSaltosRepeticionesYBordesNoAdyacentes() {
        val g=ReglasNumberConnect();g.restaurar(List(35){8},0);val antes=g.celdas
        for(camino in listOf(emptyList(),listOf(0),listOf(0,0),listOf(0,1,0),listOf(4,5),listOf(0,10),listOf(-1,0),listOf(34,35))) {
            assertFalse(g.combinar(camino));assertEquals(antes,g.celdas);assertEquals(0L,g.puntos)
        }
    }
    @Test fun soloSePuedenUnirNumerosIguales() {
        val g=ReglasNumberConnect();g.restaurar(List(35){if(it==0)4 else 8},0)
        assertFalse(g.combinar(listOf(0,1)));assertTrue(g.combinar(listOf(1,2)))
    }
    @Test fun finDePartidaIncluyeDiagonales() {
        val g=ReglasNumberConnect();val separados=List(35){1 shl (1+(it/5%2)*2+it%5%2)}
        g.restaurar(separados,0);assertTrue(g.terminado)
        g.restaurar(separados.toMutableList().apply{this[6]=this[0]},0);assertFalse(g.terminado)
    }
    @Test fun noDesbordaValoresNiAceptaGuardadoInvalido() {
        val g=ReglasNumberConnect();g.restaurar(List(35){ReglasNumberConnect.MAXIMO},0)
        assertTrue(g.terminado);assertFalse(g.combinar(listOf(0,1)))
        assertFalse(g.restaurar(List(35){3},0));assertFalse(g.restaurar(List(34){2},0));assertFalse(g.restaurar(List(35){2},-1))
    }
    @Test fun reinicioYRestauracionConservanInvariantes() {
        val g=ReglasNumberConnect();val valores=List(35){1 shl (it%12+1)}
        assertTrue(g.restaurar(valores,123456));assertEquals(valores,g.celdas);assertEquals(123456L,g.puntos)
        g.reiniciar();assertEquals(0L,g.puntos);assertFalse(g.terminado)
    }
    @Test fun cadenaCompletaDe35Casillas() {
        val g=ReglasNumberConnect();g.restaurar(List(35){8},0)
        val camino=(0..6).flatMap { f -> (if(f%2==0)(0..4).toList() else (4 downTo 0).toList()).map{f*5+it} }
        assertTrue(g.combinar(camino));assertEquals(512L,g.puntos);assertEquals(512,g.celdas[34])
        assertTrue(g.celdas.all { it>0 && it and (it-1)==0 })
    }
}
