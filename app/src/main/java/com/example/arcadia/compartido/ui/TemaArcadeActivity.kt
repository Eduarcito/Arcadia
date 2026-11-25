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
import com.example.arcadia.compartido.estado.ARCHIVO_AJUSTES
import com.example.arcadia.compartido.estado.TEMA_CLARO
import com.example.arcadia.compartido.estado.TEMA_OSCURO
import com.example.arcadia.compartido.estado.temaGuardado

open class TemaArcadeActivity : AppCompatActivity() {
    protected val preferencias by lazy {
        getSharedPreferences(ARCHIVO_AJUSTES, Context.MODE_PRIVATE)
    }
    private var modoOscuro = true
    private var tema = TEMA_OSCURO
    protected val temaVigente: Boolean
        get() = tema == temaGuardado(preferencias)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Arcade)
        tema = temaGuardado(preferencias)
        delegate.localNightMode = when (tema) {
            TEMA_CLARO -> AppCompatDelegate.MODE_NIGHT_NO
            TEMA_OSCURO -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        modoOscuro = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
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
