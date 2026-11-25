package com.example.arcadia

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.arcadia.compartido.estado.ARCHIVO_AJUSTES
import com.example.arcadia.compartido.estado.TEMA_CLARO
import com.example.arcadia.compartido.estado.TEMA_OSCURO
import com.example.arcadia.compartido.estado.temaGuardado

class ArcadiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val tema = temaGuardado(getSharedPreferences(ARCHIVO_AJUSTES, MODE_PRIVATE))
        AppCompatDelegate.setDefaultNightMode(
            when (tema) {
                TEMA_CLARO -> AppCompatDelegate.MODE_NIGHT_NO
                TEMA_OSCURO -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}
