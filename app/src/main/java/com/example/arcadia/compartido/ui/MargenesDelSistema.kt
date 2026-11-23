package com.example.arcadia.compartido.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

internal fun View.aplicarMargenesDelSistema() {
    val izquierda = paddingLeft
    val superior = paddingTop
    val derecha = paddingRight
    val inferior = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, margenes ->
        val tipos = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        val bordes = margenes.getInsets(tipos)
        vista.setPadding(izquierda + bordes.left, superior + bordes.top,
            derecha + bordes.right, inferior + bordes.bottom)
        margenes
    }
    ViewCompat.requestApplyInsets(this)
}
