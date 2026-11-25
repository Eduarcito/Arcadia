package com.example.arcadia.juegos.buscaminas

import com.example.arcadia.compartido.estado.PartidaViewModel
import com.example.arcadia.compartido.estado.enteros
import com.example.arcadia.compartido.estado.lista
import org.json.JSONObject

class BuscaminasViewModel : PartidaViewModel() {
    override val clave = "mines"
    val juego = ReglasBuscaminas()
    fun actuar(indice: Int, bandera: Boolean): Boolean {
        return if (bandera) juego.marcar(indice) else juego.abrir(indice)
    }
    fun reiniciar() {
        juego.reiniciar()
    }
    override fun restaurar(json: JSONObject) {
        juego.restaurar(json.enteros("mines").toSet(), json.enteros("open").toSet(), json.enteros("flags").toSet(), json.getBoolean("started"), json.getInt("hit"))
    }
    // La clave flagMode se conserva en el formato guardado aunque el modo ya no existe
    override fun estado() = JSONObject().lista("mines", juego.minas).lista("open", juego.abiertas).lista("flags", juego.banderas)
        .put("started", juego.iniciada).put("hit", juego.explosion).put("flagMode", false)
}
