package com.example.arcadia.compartido.ui

import android.content.res.Resources

internal fun Resources.coloresJuego(recurso: Int): List<Int> {
    val colores = obtainTypedArray(recurso)
    return try {
        List(colores.length()) { colores.getColor(it, 0) }
    } finally {
        colores.recycle()
    }
}
