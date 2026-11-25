package com.example.arcadia.arcade

import com.example.arcadia.juegos.juego2048.Direccion
import com.example.arcadia.juegos.petdoku.ReglasPetdoku
import org.junit.Assert.*
import org.junit.Test

class DetectorDeslizamientoTest {
    @Test fun distingueLasCuatroDirecciones() {
        val casos = listOf(Triple(30f,0f,Direccion.DERECHA),Triple(-30f,0f,Direccion.IZQUIERDA),
            Triple(0f,30f,Direccion.ABAJO),Triple(0f,-30f,Direccion.ARRIBA))
        for((x,y,direccion) in casos) {
            val detector = DetectorDeslizamiento(20f)
            detector.iniciar(100f,100f)
            assertEquals(direccion,detector.mover(100f+x,100f+y))
        }
    }
    @Test fun ignoraTemblorDelDedoYDiagonalesAmbiguas() {
        val detector = DetectorDeslizamiento(20f)
        detector.iniciar(0f,0f)
        assertNull(detector.mover(8f,3f))
        assertNull(detector.mover(30f,30f))
        assertEquals(Direccion.DERECHA,detector.mover(50f,30f))
    }
    @Test fun unSoloMovimientoPorGesto2048() {
        val detector = DetectorDeslizamiento(20f)
        detector.iniciar(100f,100f)
        assertEquals(Direccion.IZQUIERDA,detector.mover(70f,100f))
        assertNull(detector.mover(30f,100f))
        assertNull(detector.mover(20f,30f))
        detector.iniciar(20f,30f)
        assertEquals(Direccion.ABAJO,detector.mover(20f,60f))
    }
    @Test fun snakePuedeCambiarDeDireccionSinLevantarElDedo() {
        val detector = DetectorDeslizamiento(20f).apply { continuo = true }
        detector.iniciar(100f,100f)
        assertEquals(Direccion.DERECHA,detector.mover(130f,100f))
        assertEquals(Direccion.ARRIBA,detector.mover(130f,70f))
        assertEquals(Direccion.IZQUIERDA,detector.mover(100f,70f))
    }
    @Test fun cancelarOMultitactilNoProduceOtroMovimiento() {
        val detector = DetectorDeslizamiento(20f).apply { continuo = true }
        detector.iniciar(0f,0f);detector.cancelar()
        assertNull(detector.mover(50f,0f))
        detector.iniciar(0f,0f)
        assertEquals(Direccion.ABAJO,detector.mover(0f,30f))
    }
    @Test fun pincelPetdokuEsIdempotenteYPreservaMascotas() {
        val juego = ReglasPetdoku()
        juego.descubrir(4)
        assertFalse(juego.pintarDescarte(4,false))
        assertFalse(juego.pintarDescarte(4,true))
        assertTrue(juego.pintarDescarte(1,false))
        assertFalse(juego.pintarDescarte(1,false))
        assertEquals(2,juego.marcas[1])
        assertTrue(juego.pintarDescarte(1,true))
        assertEquals(0,juego.marcas[1]);assertEquals(1,juego.marcas[4])
    }
}
