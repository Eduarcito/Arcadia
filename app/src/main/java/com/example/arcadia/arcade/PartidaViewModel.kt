package com.example.arcadia.arcade

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

abstract class PartidaViewModel : ViewModel() {
    abstract val clave: String
    private var cargado = false
    abstract fun restaurar(json: JSONObject)
    abstract fun estado(): JSONObject
    fun cargar(preferencias: SharedPreferences) {
        if (cargado) return
        cargado = true
        preferencias.getString(clave,null)?.let { runCatching { restaurar(JSONObject(it)) } }
    }
    fun guardar(preferencias: SharedPreferences) { preferencias.edit().putString(clave,estado().toString()).apply() }
}

fun JSONObject.enteros(clave: String): List<Int> {
    val array = getJSONArray(clave)
    return List(array.length()) { array.getInt(it) }
}
fun JSONObject.lista(clave: String, datos: Collection<Int>): JSONObject = put(clave,JSONArray(datos))
