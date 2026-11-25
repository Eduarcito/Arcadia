package com.example.arcadia.compartido.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.arcadia.R

open class TemaArcadeActivity : AppCompatActivity() {
    protected val preferencias by lazy {
        getSharedPreferences("arcade_settings", Context.MODE_PRIVATE)
    }
    private var modoOscuro = true
    protected val temaVigente: Boolean
        get() = modoOscuro == preferencias.getBoolean("dark", true)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Arcade)
        modoOscuro = preferencias.getBoolean("dark", true)
        delegate.localNightMode = if (modoOscuro) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        if (!temaVigente) {
            recreate()
            return
        }
        actualizarBarras()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) actualizarBarras()
    }

    private fun actualizarBarras() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !modoOscuro
            isAppearanceLightNavigationBars = !modoOscuro
        }
    }

    protected fun margenes(vista: View) {
        ViewCompat.setOnApplyWindowInsetsListener(vista) { contenido, margenes ->
            val barras = margenes.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            contenido.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            margenes
        }
    }

    protected fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()
}
