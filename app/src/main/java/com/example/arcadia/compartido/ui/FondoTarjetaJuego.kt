package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import com.example.arcadia.R

internal fun fondoTarjetaJuego(contexto: Context, acento: Int): GradientDrawable {
    val densidad = contexto.resources.displayMetrics.density
    return GradientDrawable().apply {
        cornerRadius = (20 * densidad).toInt().toFloat()
        setColor(contexto.getColor(R.color.arcade_surface))
        setStroke(densidad.toInt(), ColorUtils.setAlphaComponent(acento, 42))
    }
}
