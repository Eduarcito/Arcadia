package com.example.arcadia.compartido.estado

import android.content.SharedPreferences

// Nombres de archivos y claves de preferencias; no cambiar: conservan partidas y ajustes guardados
const val ARCHIVO_PARTIDAS = "arcade_games"
const val ARCHIVO_AJUSTES = "arcade_settings"
const val CLAVE_OSCURO = "dark"
const val CLAVE_TEMA = "theme"
const val TEMA_SISTEMA = 0
const val TEMA_CLARO = 1
const val TEMA_OSCURO = 2
const val CLAVE_SONIDO = "sound"
const val CLAVE_MUSICA = "music"
const val CLAVE_VIBRACION = "vibration"
const val CLAVE_VOLUMEN = "volume"
const val CLAVE_VOLUMEN_MUSICA = "volume_music"
const val VOLUMEN_INICIAL = 45

fun temaGuardado(preferencias: SharedPreferences): Int {
    if (preferencias.contains(CLAVE_TEMA)) return preferencias.getInt(CLAVE_TEMA, TEMA_OSCURO)
    return if (preferencias.getBoolean(CLAVE_OSCURO, true)) TEMA_OSCURO else TEMA_CLARO
}
