package com.example.arcadia.compartido.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.google.android.material.button.MaterialButton

/**
 * Botón de acción principal con el acento del juego, en el estilo del **Comprobar**
 * de KenKen: relleno redondeado, texto en contraste según la luminancia del acento.
 */
fun botonAccionArcade(contexto: Context, acento: Int): MaterialButton {
    val densidad = contexto.resources.displayMetrics.density
    val tinta = if (ColorUtils.calculateLuminance(acento) > 0.45) Color.rgb(40, 30, 46) else Color.WHITE
    val estados = arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf())
    return MaterialButton(contexto).apply {
        layoutParams = android.widget.FrameLayout.LayoutParams(-1, -2)
        backgroundTintList = ColorStateList(estados, intArrayOf(ColorUtils.setAlphaComponent(acento, 70), acento))
        setTextColor(ColorStateList(estados, intArrayOf(ColorUtils.setAlphaComponent(tinta, 130), tinta)))
        iconTint = ColorStateList(estados, intArrayOf(ColorUtils.setAlphaComponent(tinta, 130), tinta))
        cornerRadius = (20 * densidad).toInt()
        minHeight = (52 * densidad).toInt()
        insetTop = 0; insetBottom = 0
        isAllCaps = false
        textSize = 16f
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    }
}
