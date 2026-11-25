package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.arcadia.R

// Conserva el texto y los gestos de la casilla y adapta la ilustracion a su espacio
class CasillaIlustradaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private var dibujo: Drawable? = null
    private var bordesRegion = 0
    private val pinturaBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.borde_region_petdoku)
        strokeWidth = resources.displayMetrics.density * 2.5f
    }
    var recursoIlustracion: Int = 0
        private set

    /** Máscara de bordes gruesos de región: 1 superior, 2 inferior, 4 inicio, 8 fin. */
    fun mostrarBordesRegion(mascara: Int) {
        if (bordesRegion == mascara) return
        bordesRegion = mascara
        invalidate()
    }

    fun mostrarIlustracion(recurso: Int) {
        if (recursoIlustracion == recurso) return
        recursoIlustracion = recurso
        dibujo = if (recurso == 0) null else context.getDrawable(recurso)?.mutate()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bordesRegion != 0) {
            val m = pinturaBorde.strokeWidth / 2
            if (bordesRegion and 1 != 0) canvas.drawLine(0f, m, width.toFloat(), m, pinturaBorde)
            if (bordesRegion and 2 != 0) canvas.drawLine(0f, height - m, width.toFloat(), height - m, pinturaBorde)
            if (bordesRegion and 4 != 0) canvas.drawLine(m, 0f, m, height.toFloat(), pinturaBorde)
            if (bordesRegion and 8 != 0) canvas.drawLine(width - m, 0f, width - m, height.toFloat(), pinturaBorde)
        }
        dibujo?.let {
            val lado = (minOf(width, height) * .94f).toInt()
            val x = (width - lado) / 2
            val y = (height - lado) / 2
            it.setBounds(x, y, x + lado, y + lado)
            it.draw(canvas)
        }
    }
}
