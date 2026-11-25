package com.example.arcadia.juegos.snake

import com.example.arcadia.arcade.PartidaViewModel
import com.example.arcadia.arcade.enteros
import com.example.arcadia.arcade.lista
import org.json.JSONObject

class SnakeViewModel : PartidaViewModel() {
    override val clave="snake"
    val juego=ReglasSnake()
    var record=0
    var empezada=false
    var corriendo=false
    override fun restaurar(json: JSONObject) {
        if(juego.restaurar(json.enteros("body"),json.getInt("direction"),json.getInt("food"),json.getInt("score"),json.getBoolean("over"))) {
            record=maxOf(json.optInt("best"),juego.puntos);empezada=json.optBoolean("started")
        }
    }
    override fun estado()=JSONObject().lista("body",juego.cuerpo).put("direction",juego.direccion.ordinal)
        .put("food",juego.comida).put("score",juego.puntos).put("best",record).put("over",juego.terminada).put("started",empezada)
    fun paso() { juego.paso();record=maxOf(record,juego.puntos);if(juego.terminada)corriendo=false }
    fun reiniciar() { juego.reiniciar();empezada=false;corriendo=false }
}
