package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

// Conserva el texto y los gestos de la casilla y adapta la ilustracion a su espacio
class CasillaIlustradaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private var dibujo: Drawable? = null
    var recursoIlustracion: Int = 0
        private set

    fun mostrarIlustracion(recurso: Int) {
        if (recursoIlustracion == recurso) return
        recursoIlustracion = recurso
        dibujo = if (recurso == 0) null else context.getDrawable(recurso)?.mutate()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dibujo?.let {
            val lado = (minOf(width, height) * .94f).toInt()
            val x = (width - lado) / 2
            val y = (height - lado) / 2
            it.setBounds(x, y, x + lado, y + lado)
            it.draw(canvas)
        }
    }
}
