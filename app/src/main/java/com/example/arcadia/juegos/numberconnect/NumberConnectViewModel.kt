package com.example.arcadia.juegos.numberconnect

import com.example.arcadia.compartido.estado.PartidaViewModel
import com.example.arcadia.compartido.estado.enteros
import com.example.arcadia.compartido.estado.lista
import org.json.JSONObject

data class PrevisualizacionConexion(val valores: List<Int>, val suma: Long, val resultado: Int?)

class NumberConnectViewModel : PartidaViewModel() {
    override val clave="number_connect"
    val juego=ReglasNumberConnect()
    var record=0L; private set
    fun puedeAgregar(camino:List<Int>,indice:Int)=juego.puedeAgregar(camino,indice)
    fun previsualizar(camino: List<Int>): PrevisualizacionConexion? {
        if (camino.isEmpty() || camino.any { it !in juego.celdas.indices }) return null
        val resultado = if (camino.size == 1) juego.celdas[camino.first()] else juego.resultado(camino)
        val valores = camino.map { juego.celdas[it] }
        return PrevisualizacionConexion(valores, valores.sumOf { it.toLong() }, resultado)
    }
    fun combinar(camino: List<Int>): Boolean {
        val cambio=juego.combinar(camino)
        record=maxOf(record,juego.puntos)
        return cambio
    }
    fun reiniciar()=juego.reiniciar()
    override fun restaurar(json: JSONObject) {
        if(juego.restaurar(json.enteros("cells"),json.getLong("score"))) record=maxOf(juego.puntos,json.optLong("best"))
    }
    override fun estado()=JSONObject().lista("cells",juego.celdas).put("score",juego.puntos).put("best",record)
}
