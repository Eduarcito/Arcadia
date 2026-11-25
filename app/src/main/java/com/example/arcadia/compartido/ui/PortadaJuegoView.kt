package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.example.arcadia.R

// Dibuja las ilustraciones a la resolucion de la pantalla
class PortadaJuegoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context,attrs) {
    var juego = 0
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tipoNegro = Typeface.create("sans-serif-black",Typeface.BOLD)
    private val trazo = Path()
    private val mascota by lazy { context.getDrawable(R.drawable.petdoku_perrito)?.mutate() }
    private val fichas2048 = resources.coloresJuego(R.array.portada_2048_fichas)
    private val regionesPetdoku = resources.coloresJuego(R.array.portada_petdoku_regiones)
    private val numerosConnect = resources.coloresJuego(R.array.portada_connect_numeros)

    private fun color(recurso: Int) = context.getColor(recurso)
    private fun rect(c: Canvas,x:Float,y:Float,w:Float,h:Float,colorFondo:Int,r:Float=10f) {
        p.style=Paint.Style.FILL; p.color=colorFondo; c.drawRoundRect(x,y,x+w,y+h,r,r,p)
    }
    private fun text(c:Canvas,s:String,x:Float,y:Float,size:Float,colorTinta:Int) {
        p.color=colorTinta;p.typeface=tipoNegro;p.textSize=size;p.textAlign=Paint.Align.CENTER
        c.drawText(s,x,y,p)
    }
    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.save(); c.translate(width/2f,height/2f); val scale=minOf(width/170f,height/160f);c.scale(scale,scale);c.translate(-85f,-80f)
        when(juego) {
            0 -> {
                c.rotate(-9f,85f,80f)
                val nums=listOf("2","4","8","16","32","64","128","256","2048")
                for(i in 0..8){val x=8f+(i%3)*52;val y=4f+(i/3)*52;rect(c,x,y,47f,47f,fichas2048[i]);text(c,nums[i],x+23.5f,y+30,if(i==8)16f else 23f,color(R.color.portada_2048_tinta))}
            }
            1 -> {
                c.rotate(8f,85f,80f)
                for(i in 0..15){val x=7f+(i%4)*40;val y=1f+(i/4)*40;rect(c,x,y,35f,35f,color(if(i in listOf(0,1,4,5,8))R.color.portada_minas_clara else R.color.portada_minas_oscura),6f)
                    if(i==1||i==4)text(c,"1",x+17,y+26,25f,color(R.color.portada_minas_uno))
                    if(i==5)text(c,"2",x+17,y+26,25f,color(R.color.portada_minas_dos))
                    if(i==10){p.color=Color.WHITE;p.strokeWidth=3f;c.drawLine(x+12,y+8,x+12,y+29,p);trazo.reset();trazo.moveTo(x+13,y+7);trazo.lineTo(x+30,y+14);trazo.lineTo(x+13,y+20);trazo.close();p.color=color(R.color.portada_minas_bandera);c.drawPath(trazo,p)}
                }
            }
            2 -> {
                c.rotate(-8f,85f,80f)
                for(i in 0..8){val x=7f+(i%3)*52;val y=2f+(i/3)*52;rect(c,x,y,47f,47f,regionesPetdoku[(i+i/3)%4]);
                    if(i in listOf(0,5,7)) text(c,"×",x+24,y+37,43f,Color.WHITE)}
                perrito(c,82f,24f);perrito(c,30f,129f);perrito(c,137f,128f)
            }
            3 -> {
                trazo.reset();trazo.moveTo(13f,143f);trazo.lineTo(13f,110f);trazo.quadTo(13f,90f,35f,90f);trazo.lineTo(119f,90f);trazo.quadTo(149f,90f,149f,62f);trazo.quadTo(149f,35f,120f,35f);trazo.lineTo(65f,35f)
                p.style=Paint.Style.STROKE;p.strokeWidth=30f;p.strokeCap=Paint.Cap.ROUND;p.strokeJoin=Paint.Join.ROUND;p.color=color(R.color.portada_snake_cuerpo);c.drawPath(trazo,p);p.style=Paint.Style.FILL
                p.color=color(R.color.portada_snake_cabeza);c.drawCircle(61f,35f,23f,p)
                for(x in listOf(53f,68f)){p.color=Color.WHITE;c.drawCircle(x,29f,7f,p);p.color=color(R.color.portada_snake_pupila);c.drawCircle(x-1,29f,3f,p)}
                p.color=color(R.color.portada_snake_fruta);c.drawCircle(73f,135f,12f,p);p.color=color(R.color.portada_snake_hoja);c.drawOval(76f,115f,86f,125f,p)
            }
            5 -> {
                c.rotate(-6f,85f,80f)
                val nums=listOf("4","8","32","4","4","16","64","4","8")
                for(i in 0..8){val x=7f+(i%3)*52;val y=2f+(i/3)*52;rect(c,x,y,47f,47f,numerosConnect[i]);text(c,nums[i],x+24,y+34,28f,color(R.color.portada_connect_tinta))}
                p.color=Color.WHITE;p.strokeWidth=4f;p.strokeCap=Paint.Cap.ROUND
                c.drawLine(31f,26f,31f,78f,p);c.drawLine(31f,78f,83f,78f,p);c.drawLine(83f,78f,83f,130f,p)
            }
            else -> {
                c.rotate(-7f,85f,80f)
                for(i in 0..8){val x=7f+(i%3)*52;val y=2f+(i/3)*52;rect(c,x,y,47f,47f,color(if(i%3==0)R.color.portada_kenken_casilla else R.color.portada_kenken_casilla_alterna));text(c,arrayOf(1,2,3,2,3,1,3,1,2)[i].toString(),x+24,y+35,31f,color(R.color.portada_kenken_numero))}
                text(c,"6+",30f,16f,11f,color(R.color.portada_kenken_pista))
            }
        }
        c.restore()
    }
    private fun perrito(c: Canvas, x: Float, y: Float) {
        mascota?.let {
            it.setBounds((x-24).toInt(),(y-24).toInt(),(x+24).toInt(),(y+24).toInt())
            it.draw(c)
        }
    }
}
