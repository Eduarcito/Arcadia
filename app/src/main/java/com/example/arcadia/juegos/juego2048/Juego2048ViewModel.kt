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

    override fun restaurar(json: JSONObject) {
        if (juego.restaurar(json.enteros("cells"), json.getLong("score"))) {
            record = maxOf(json.optLong("best"), juego.puntos)
            victoriaVista = json.optBoolean("win")
        }
    }

    override fun estado() = JSONObject().lista("cells", juego.celdas)
        .put("score", juego.puntos).put("best", record).put("win", victoriaVista)

    fun mover(direccion: Direccion): Boolean {
        val cambio = juego.mover(direccion)
        record = maxOf(record, juego.puntos)
        return cambio
    }

    fun reiniciar() {
        juego.reiniciar()
        victoriaVista = false
    }
}
