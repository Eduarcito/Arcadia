package com.example.arcadia.juegos.juego2048

import com.example.arcadia.compartido.estado.Direccion
import com.example.arcadia.compartido.estado.PartidaViewModel
import com.example.arcadia.compartido.estado.enteros
import com.example.arcadia.compartido.estado.lista
import org.json.JSONObject

class Juego2048ViewModel : PartidaViewModel() {
    override val clave = "2048"
    val juego = Reglas2048()
    var record = 0L
    var victoriaVista = false
    private val historial = ArrayDeque<Pair<List<Int>, Long>>()
    val puedeDeshacer: Boolean get() = historial.isNotEmpty()

    override fun restaurar(json: JSONObject) {
        if (juego.restaurar(json.enteros("cells"), json.getLong("score"))) {
            record = maxOf(json.optLong("best"), juego.puntos)
            victoriaVista = json.optBoolean("win")
        }
    }

    override fun estado() = JSONObject().lista("cells", juego.celdas)
        .put("score", juego.puntos).put("best", record).put("win", victoriaVista)

    fun mover(direccion: Direccion): Boolean {
        val celdasAntes = juego.celdas.toList()
        val puntosAntes = juego.puntos
        val cambio = juego.mover(direccion)
        if (cambio) {
            historial.addLast(celdasAntes to puntosAntes)
            if (historial.size > 500) historial.removeFirst()
        }
        record = maxOf(record, juego.puntos)
        return cambio
    }

    /** Deshace la última jugada; el historial es de sesión y no se guarda en disco. */
    fun deshacer(): Boolean {
        val (celdas, puntos) = historial.removeLastOrNull() ?: return false
        return juego.restaurar(celdas, puntos)
    }

    fun reiniciar() {
        juego.reiniciar()
        victoriaVista = false
        historial.clear()
    }
}
