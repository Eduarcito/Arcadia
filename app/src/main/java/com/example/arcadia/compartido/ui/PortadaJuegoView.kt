package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

// Dibuja las ilustraciones a la resolucion de la pantalla
class PortadaJuegoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context,attrs) {
    var juego = 0
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private fun color(hex: String) = Color.parseColor(hex)
    private fun rect(c: Canvas,x:Float,y:Float,w:Float,h:Float,hex:String,r:Float=10f) {
        p.style=Paint.Style.FILL; p.color=color(hex); c.drawRoundRect(x,y,x+w,y+h,r,r,p)
    }
    private fun text(c:Canvas,s:String,x:Float,y:Float,size:Float,hex:String) {
        p.color=color(hex);p.typeface=Typeface.create("sans-serif-black",Typeface.BOLD);p.textSize=size;p.textAlign=Paint.Align.CENTER
        c.drawText(s,x,y,p)
    }
    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.save(); c.translate(width/2f,height/2f); val scale=minOf(width/170f,height/160f);c.scale(scale,scale);c.translate(-85f,-80f)
        when(juego) {
            0 -> {
                c.rotate(-9f,85f,80f)
                val nums=listOf("2","4","8","16","32","64","128","256","2048")
                val colors=listOf("#F5E3C6","#F1D396","#FFB263","#FF9674","#F77C6D","#EA646A","#EDC552","#F7D25E","#FFDD70")
                for(i in 0..8){val x=8f+(i%3)*52;val y=4f+(i/3)*52;rect(c,x,y,47f,47f,colors[i]);text(c,nums[i],x+23.5f,y+30,if(i==8)16f else 23f,"#50301C")}
            }
            1 -> {
                c.rotate(8f,85f,80f)
                for(i in 0..15){val x=7f+(i%4)*40;val y=1f+(i/4)*40;rect(c,x,y,35f,35f,if(i in listOf(0,1,4,5,8))"#DEF3F0" else "#4B9492",6f)
                    if(i==1||i==4)text(c,"1",x+17,y+26,25f,"#3188B9")
                    if(i==5)text(c,"2",x+17,y+26,25f,"#1A735E")
                    if(i==10){p.color=color("#FFFFFF");p.strokeWidth=3f;c.drawLine(x+12,y+8,x+12,y+29,p);val path=Path();path.moveTo(x+13,y+7);path.lineTo(x+30,y+14);path.lineTo(x+13,y+20);path.close();p.color=color("#FFB6B9");c.drawPath(path,p)}
                }
            }
            2 -> {
                c.rotate(-8f,85f,80f)
                val colors=listOf("#F1A8C6","#8CD4AF","#A7D9F0","#F4CE8E")
                for(i in 0..8){val x=7f+(i%3)*52;val y=2f+(i/3)*52;rect(c,x,y,47f,47f,colors[(i+i/3)%4]);
                    if(i in listOf(0,5,7)) text(c,"×",x+24,y+37,43f,"#FFFFFF")}
                cat(c,82f,24f);cat(c,30f,129f);cat(c,137f,128f)
            }
            3 -> {
                val path=Path();path.moveTo(13f,143f);path.lineTo(13f,110f);path.quadTo(13f,90f,35f,90f);path.lineTo(119f,90f);path.quadTo(149f,90f,149f,62f);path.quadTo(149f,35f,120f,35f);path.lineTo(65f,35f)
                p.style=Paint.Style.STROKE;p.strokeWidth=30f;p.strokeCap=Paint.Cap.ROUND;p.strokeJoin=Paint.Join.ROUND;p.color=color("#85DC66");c.drawPath(path,p);p.style=Paint.Style.FILL
                p.color=color("#B8F77C");c.drawCircle(61f,35f,23f,p)
                for(x in listOf(53f,68f)){p.color=Color.WHITE;c.drawCircle(x,29f,7f,p);p.color=color("#192A23");c.drawCircle(x-1,29f,3f,p)}
                p.color=color("#FF7A9A");c.drawCircle(73f,135f,12f,p);p.color=color("#C3F488");c.drawOval(76f,115f,86f,125f,p)
            }
            else -> {
                c.rotate(-7f,85f,80f)
                for(i in 0..8){val x=7f+(i%3)*52;val y=2f+(i/3)*52;rect(c,x,y,47f,47f,if(i%3==0)"#E4D4FF" else "#F3EAFB");text(c,((i*2)%4+1).toString(),x+24,y+35,31f,"#795092")}
                text(c,"6+",30f,16f,11f,"#65417B")
            }
        }
        c.restore()
    }
    private fun cat(c:Canvas,x:Float,y:Float) {
        p.color=color("#FFF5E9");c.drawCircle(x,y,20f,p)
        val ears=Path();ears.moveTo(x-19,y-3);ears.lineTo(x-18,y-27);ears.lineTo(x-4,y-17);ears.moveTo(x+19,y-3);ears.lineTo(x+18,y-27);ears.lineTo(x+4,y-17);c.drawPath(ears,p)
        p.color=color("#42363B");c.drawCircle(x-7,y-2,3f,p);c.drawCircle(x+7,y-2,3f,p)
        p.color=color("#DF8CA7");c.drawCircle(x,y+6,3f,p)
    }
}
