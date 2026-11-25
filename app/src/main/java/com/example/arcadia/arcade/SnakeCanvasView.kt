package com.example.arcadia.arcade

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.arcadia.juegos.juego2048.Direccion

/** Smooth, connected Snake body; the containing board owns touch input. */
class SnakeCanvasView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var cuerpo = emptyList<Int>()
    private var anterior = emptyList<Int>()
    private var fruta = -1
    private var direccion = Direccion.DERECHA
    private var progreso = 1f
    private var animacion: ValueAnimator? = null
    var mensaje = ""
        set(value) { field = value; invalidate() }

    fun mostrar(nuevo: List<Int>, comida: Int, d: Direccion, animar: Boolean) {
        if (!animar) { animacion?.cancel(); progreso = 1f }
        if (nuevo != cuerpo) {
            animacion?.cancel()
            anterior = cuerpo
            cuerpo = nuevo.toList()
            progreso = if (animar && anterior.isNotEmpty()) 0f else 1f
            if (progreso == 0f) {
                animacion = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 85; interpolator = LinearInterpolator()
                    addUpdateListener { progreso = it.animatedValue as Float; invalidate() }
                    start()
                }
            }
        }
        fruta = comida; direccion = d; invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = width / 14f
        paint.style = Paint.Style.FILL
        for (i in 0..195) {
            paint.color = if ((i / 14 + i % 14) % 2 == 0) 0xFF16352D.toInt() else 0xFF1A3C32.toInt()
            canvas.drawRect((i % 14) * size, (i / 14) * size, (i % 14 + 1) * size, (i / 14 + 1) * size, paint)
        }
        if (fruta >= 0) {
            val x = (fruta % 14 + .5f) * size; val y = (fruta / 14 + .5f) * size
            paint.color = 0xFFFF7D9F.toInt(); canvas.drawCircle(x, y + size*.05f, size*.31f, paint)
            paint.color = 0xFFBEEB70.toInt(); canvas.drawOval(x, y-size*.42f, x+size*.26f, y-size*.20f, paint)
            paint.color = 0x66FFFFFF; canvas.drawCircle(x-size*.1f, y-size*.05f, size*.07f, paint)
        }
        if (cuerpo.isEmpty()) return
        fun posicion(i: Int, horizontal: Boolean): Float {
            val n = cuerpo[i]
            val a = anterior.getOrElse(i) { anterior.lastOrNull() ?: n }
            val destino = if (horizontal) n % 14 else n / 14
            val origen = if (horizontal) a % 14 else a / 14
            return (origen + (destino-origen)*progreso + .5f)*size
        }
        path.reset()
        cuerpo.indices.reversed().forEachIndexed { j, i ->
            if (j == 0) path.moveTo(posicion(i,true),posicion(i,false))
            else path.lineTo(posicion(i,true),posicion(i,false))
        }
        paint.style = Paint.Style.STROKE; paint.strokeCap = Paint.Cap.ROUND; paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = size*.79f; paint.color = 0xFF91DF69.toInt(); canvas.drawPath(path,paint)
        paint.style = Paint.Style.FILL
        val hx = posicion(0,true); val hy = posicion(0,false)
        paint.color = 0xFFC9F58D.toInt(); canvas.drawCircle(hx,hy,size*.46f,paint)
        val dx = when(direccion) { Direccion.DERECHA->1; Direccion.IZQUIERDA->-1; else->0 }
        val dy = when(direccion) { Direccion.ABAJO->1; Direccion.ARRIBA->-1; else->0 }
        for (signo in listOf(-1,1)) {
            val ex = hx + dx*size*.13f - dy*signo*size*.19f
            val ey = hy + dy*size*.13f + dx*signo*size*.19f
            paint.color = Color.WHITE; canvas.drawCircle(ex,ey,size*.14f,paint)
            paint.color = 0xFF15352A.toInt(); canvas.drawCircle(ex+dx*size*.04f,ey+dy*size*.04f,size*.065f,paint)
        }
        if (mensaje.isNotEmpty()) {
            paint.color = 0xAA10271F.toInt(); canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)
            paint.color = Color.WHITE; paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create("sans-serif",Typeface.BOLD); paint.textSize = size*.72f
            val medida = paint.measureText(mensaje)
            if(medida>width*.85f)paint.textSize *= width*.85f/medida
            canvas.drawText(mensaje,width/2f,height/2f-(paint.ascent()+paint.descent())/2,paint)
        }
    }
    override fun onDetachedFromWindow() { animacion?.cancel(); super.onDetachedFromWindow() }
}
