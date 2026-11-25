package com.example.arcadia

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class ArcadiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
