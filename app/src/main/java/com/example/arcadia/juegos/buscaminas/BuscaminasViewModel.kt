package com.example.arcadia.juegos.buscaminas

import com.example.arcadia.arcade.PartidaViewModel
import com.example.arcadia.arcade.enteros
import com.example.arcadia.arcade.lista
import org.json.JSONObject

class BuscaminasViewModel : PartidaViewModel() {
    override val clave="mines"
    val juego=ReglasBuscaminas()
    var modoBandera=false
    override fun restaurar(json: JSONObject) {
        juego.restaurar(json.enteros("mines").toSet(),json.enteros("open").toSet(),json.enteros("flags").toSet(),json.getBoolean("started"),json.getInt("hit"))
        modoBandera=json.optBoolean("flagMode")
    }
    override fun estado()=JSONObject().lista("mines",juego.minas).lista("open",juego.abiertas).lista("flags",juego.banderas)
        .put("started",juego.iniciada).put("hit",juego.explosion).put("flagMode",modoBandera)
}
