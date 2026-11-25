package com.example.arcadia.juegos.buscaminas

import com.example.arcadia.compartido.estado.PartidaViewModel
import com.example.arcadia.compartido.estado.enteros
import com.example.arcadia.compartido.estado.lista
import org.json.JSONObject

class BuscaminasViewModel : PartidaViewModel() {
    override val clave="mines"
    val juego=ReglasBuscaminas()
    var modoBandera=false
    var racha=0; private set
    var recordRacha=0; private set
    var pistaUsada=false; private set
    private var resultadoRegistrado=false
    fun actuar(indice: Int, bandera: Boolean): Boolean {
        val cambio = if (bandera) juego.marcar(indice) else juego.abrir(indice)
        registrarResultado()
        return cambio
    }
    private fun registrarResultado() {
        if (!juego.terminada || resultadoRegistrado) return
        resultadoRegistrado=true
        racha=if(juego.victoria) racha+1 else 0
        recordRacha=maxOf(recordRacha,racha)
    }
    fun pista(): Boolean {
        if (pistaUsada || juego.terminada) return false
        val indice=if (!juego.iniciada) (0..63).firstOrNull { it !in juego.banderas }
            else (0..63).firstOrNull { it !in juego.minas && it !in juego.abiertas && it !in juego.banderas }
        if (indice == null || !juego.abrir(indice)) return false
        pistaUsada=true;registrarResultado();return true
    }
    fun reiniciar() {
        if (juego.iniciada && !juego.terminada) racha=0
        juego.reiniciar()
        modoBandera = false
        pistaUsada=false;resultadoRegistrado=false
    }
    override fun restaurar(json: JSONObject) {
        juego.restaurar(json.enteros("mines").toSet(),json.enteros("open").toSet(),json.enteros("flags").toSet(),json.getBoolean("started"),json.getInt("hit"))
        modoBandera=json.optBoolean("flagMode")
        racha=json.optInt("streak").coerceAtLeast(0);recordRacha=maxOf(racha,json.optInt("bestStreak"))
        pistaUsada=json.optBoolean("hintUsed");resultadoRegistrado=json.optBoolean("recorded",juego.terminada)
    }
    override fun estado()=JSONObject().lista("mines",juego.minas).lista("open",juego.abiertas).lista("flags",juego.banderas)
        .put("started",juego.iniciada).put("hit",juego.explosion).put("flagMode",modoBandera)
        .put("streak",racha).put("bestStreak",recordRacha).put("hintUsed",pistaUsada).put("recorded",resultadoRegistrado)
}
