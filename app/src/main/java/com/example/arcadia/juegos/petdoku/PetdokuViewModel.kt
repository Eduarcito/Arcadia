package com.example.arcadia.juegos.petdoku

import com.example.arcadia.arcade.PartidaViewModel
import com.example.arcadia.arcade.enteros
import com.example.arcadia.arcade.lista
import org.json.JSONObject

class PetdokuViewModel : PartidaViewModel() {
    override val clave="petdoku"
    val juego=ReglasPetdoku()
    override fun restaurar(json: JSONObject) { juego.restaurar(json.getInt("level"),json.enteros("marks"),json.optInt("lives",3)) }
    override fun estado()=JSONObject().put("level",juego.nivel).lista("marks",juego.marcas).put("lives",juego.vidas)
}
