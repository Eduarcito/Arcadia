package com.example.arcadia.juegos.numberconnect

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.example.arcadia.R
import com.example.arcadia.databinding.CeldaArcadeBinding
import com.example.arcadia.databinding.FilaTableroBinding
import com.example.arcadia.databinding.TableroArcadeBinding
import com.example.arcadia.compartido.ui.coloresJuego
import kotlin.math.abs

// La seleccion se confirma solo al levantar el dedo
class TableroNumberConnectView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context,attrs) {
    private val binding=TableroArcadeBinding.inflate(LayoutInflater.from(context),this,true)
    private val densidad=resources.displayMetrics.density
    private val celdas=mutableListOf<TextView>()
    private var valores=List(35){2}
    private val camino=mutableListOf<Int>()
    private var interactivo=true
    private var cancelado=false
    private var ultimoX=0f;private var ultimoY=0f
    private val linea=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.WHITE;strokeWidth=4*densidad;strokeCap=Paint.Cap.ROUND;alpha=170 }
    var alCombinar: ((List<Int>)->Unit)?=null
    var alSeleccionar: ((List<Int>)->Unit)?=null
    var puedeAgregar: ((List<Int>,Int)->Boolean)?=null
    init {
        setWillNotDraw(false)
        isFocusable=true
        val filas=List(7){FilaTableroBinding.inflate(LayoutInflater.from(context),binding.root,true).root}
        repeat(35){ i ->
            val celda=CeldaArcadeBinding.inflate(LayoutInflater.from(context),filas[i/5],false).root
            celda.layoutParams=LinearLayout.LayoutParams(0,-1,1f).apply { val m=(3*densidad).toInt();setMargins(m,m,m,m) }
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(celda,10,30,1,android.util.TypedValue.COMPLEX_UNIT_SP)
            celda.gravity=Gravity.CENTER
            celda.setOnClickListener { if(interactivo){ seleccionar(i);if(camino.size==1)alSeleccionar?.invoke(camino.toList()) } }
            filas[i/5].addView(celda);celdas.add(celda)
        }
        ViewCompat.addAccessibilityAction(this,resources.getString(R.string.connect_merge)){_,_-> confirmar();true }
    }
    fun mostrar(numeros: List<Int>, habilitado: Boolean) {
        require(numeros.size==35)
        valores=numeros.toList();interactivo=habilitado
        camino.clear();renderizar()
    }
    private fun renderizar() {
        val paleta=resources.coloresJuego(R.array.paleta_connect)
        celdas.forEachIndexed { i,celda ->
            val color=paleta[((31-Integer.numberOfLeadingZeros(valores[i]))-1).mod(paleta.size)]
            celda.text=valores[i].toString();celda.setTextColor(context.getColor(R.color.connect_tinta))
            celda.contentDescription=resources.getString(R.string.casilla_arcade_descripcion,i/5+1,i%5+1,valores[i].toString())
            celda.isClickable=interactivo;celda.isSelected=i in camino
            celda.background=GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,intArrayOf(color,ColorUtils.blendARGB(color,Color.BLACK,.12f))).apply {
                cornerRadius=7*densidad
                if(i in camino)setStroke((3*densidad).toInt(),Color.WHITE)
            }
            celda.alpha=if(camino.isEmpty() || valores[i]==valores[camino.first()])1f else .65f
        }
        invalidate()
    }
    override fun onMeasure(widthMeasureSpec: Int,heightMeasureSpec: Int) {
        val disponible=MeasureSpec.getSize(widthMeasureSpec)
        val alto=if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.UNSPECIFIED)Int.MAX_VALUE else MeasureSpec.getSize(heightMeasureSpec)
        val ancho=minOf(disponible,(alto.toLong()*5/7).toInt(),(430*densidad).toInt())
        super.onMeasure(MeasureSpec.makeMeasureSpec(ancho,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(ancho*7/5,MeasureSpec.EXACTLY))
    }
    private fun indice(x:Float,y:Float):Int = celdas.indexOfFirst { celda ->
        val fila=celda.parent as LinearLayout
        x>=binding.root.left+fila.left+celda.left && x<binding.root.left+fila.left+celda.right &&
            y>=binding.root.top+fila.top+celda.top && y<binding.root.top+fila.top+celda.bottom
    }
    private fun seleccionar(i:Int) {
        if(i !in valores.indices)return
        if(camino.size>=2 && i==camino[camino.lastIndex-1])camino.removeAt(camino.lastIndex)
        else if(puedeAgregar?.invoke(camino.toList(),i)==true)camino.add(i)
        else return
        renderizar();alSeleccionar?.invoke(camino.toList())
    }
    private fun trazar(x:Float,y:Float) {
        val pasos=maxOf(1,(maxOf(abs(x-ultimoX),abs(y-ultimoY))/maxOf(1f,width/20f)).toInt())
        for(p in 1..pasos)seleccionar(indice(ultimoX+(x-ultimoX)*p/pasos,ultimoY+(y-ultimoY)*p/pasos))
        ultimoX=x;ultimoY=y
    }
    override fun onInterceptTouchEvent(ev:MotionEvent)=true
    override fun onTouchEvent(ev:MotionEvent):Boolean {
        if(!interactivo)return true
        when(ev.actionMasked){
            MotionEvent.ACTION_DOWN->{cancelarSeleccion();cancelado=false;ultimoX=ev.x;ultimoY=ev.y;seleccionar(indice(ev.x,ev.y));parent.requestDisallowInterceptTouchEvent(true)}
            MotionEvent.ACTION_MOVE->if(!cancelado){
                for(h in 0 until ev.historySize)trazar(ev.getHistoricalX(h),ev.getHistoricalY(h))
                trazar(ev.x,ev.y)
            }
            MotionEvent.ACTION_UP->{if(!cancelado){trazar(ev.x,ev.y);confirmar();performClick()};parent.requestDisallowInterceptTouchEvent(false)}
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_POINTER_DOWN->{cancelado=true;cancelarSeleccion();parent.requestDisallowInterceptTouchEvent(false)}
        }
        return true
    }
    private fun confirmar() { val seleccion=camino.toList();cancelarSeleccion();if(interactivo && seleccion.size>=2)alCombinar?.invoke(seleccion) }
    fun cancelarSeleccion() { camino.clear();renderizar();alSeleccionar?.invoke(emptyList()) }
    override fun performClick():Boolean { super.performClick();return true }
    override fun dispatchDraw(canvas:Canvas) {
        super.dispatchDraw(canvas)
        camino.zipWithNext().forEach{(a,b)->
            fun centro(i:Int):Pair<Float,Float>{val celda=celdas[i];val fila=celda.parent as LinearLayout
                return Pair(binding.root.left+fila.left+celda.left+celda.width/2f,binding.root.top+fila.top+celda.top+celda.height/2f)}
            val desde=centro(a);val hasta=centro(b)
            canvas.drawLine(desde.first,desde.second,hasta.first,hasta.second,linea)
        }
    }
}
