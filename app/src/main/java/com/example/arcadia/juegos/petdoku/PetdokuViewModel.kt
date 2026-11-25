package com.example.arcadia.juegos.petdoku

import com.example.arcadia.compartido.estado.PartidaViewModel
import com.example.arcadia.compartido.estado.enteros
import com.example.arcadia.compartido.estado.lista
import org.json.JSONObject

class PetdokuViewModel : PartidaViewModel() {
    override val clave="petdoku"
    val juego=ReglasPetdoku()
    fun tocar(indice: Int, descubrir: Boolean): Boolean {
        return if (descubrir) juego.descubrir(indice) else juego.tocar(indice)
    }
    fun pintarDescarte(indice: Int, borrando: Boolean) = juego.pintarDescarte(indice, borrando)
    fun siguiente() = juego.siguiente()
    fun reiniciar() = juego.reiniciar()
    override fun restaurar(json: JSONObject) { juego.restaurar(json.getInt("level"),json.enteros("marks"),json.optInt("lives",3)) }
    override fun estado()=JSONObject().put("level",juego.nivel).lista("marks",juego.marcas).put("lives",juego.vidas)
}
