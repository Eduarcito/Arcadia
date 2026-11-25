package com.example.arcadia.arcade

import com.example.arcadia.juegos.juego2048.Direccion
import kotlin.math.abs

/** One move per stroke for 2048; continuous segments for steering Snake. */
class DetectorDeslizamiento(private val umbral: Float) {
    var continuo = false
    private var activo = false
    private var emitido = false
    private var x = 0f
    private var y = 0f

    fun iniciar(px: Float, py: Float) {
        x = px; y = py; activo = true; emitido = false
    }

    fun mover(px: Float, py: Float): Direccion? {
        if (!activo || (emitido && !continuo)) return null
        val dx = px - x
        val dy = py - y
        if (maxOf(abs(dx), abs(dy)) < umbral) return null
        // Wait until an ambiguous diagonal has a clear dominant axis.
        if (minOf(abs(dx), abs(dy)) * 1.15f > maxOf(abs(dx), abs(dy))) return null
        emitido = true
        if (continuo) { x = px; y = py }
        return if (abs(dx) > abs(dy)) {
            if (dx > 0) Direccion.DERECHA else Direccion.IZQUIERDA
        } else {
            if (dy > 0) Direccion.ABAJO else Direccion.ARRIBA
        }
    }

    fun cancelar() { activo = false }
}
