package com.example.arcadia.compartido.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.example.arcadia.R
import com.example.arcadia.databinding.CeldaArcadeBinding
import com.example.arcadia.databinding.FilaTableroBinding
import com.example.arcadia.databinding.TableroArcadeBinding
import com.example.arcadia.compartido.estado.Direccion
import kotlin.math.abs

data class CasillaArcade(val texto: String = "", val color: Int = Color.LTGRAY,
    val tinta: Int = Color.rgb(39,32,43), val borde: Boolean = false, val descripcion: String = texto,
    val plana: Boolean = false, val ilustracion: Int = 0)

class TableroArcadeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private val binding = TableroArcadeBinding.inflate(LayoutInflater.from(context), this, true)
    private val serpiente = SnakeCanvasView(context).also { addView(it, LayoutParams(-1,-1)); it.visibility=GONE }
    private var lado = 0
    private val celdas = mutableListOf<CasillaIlustradaView>()
    private val detectoresToque = mutableListOf<GestureDetector>()
    private var anteriores = emptyList<CasillaArcade>()
    private val densidad = resources.displayMetrics.density
    private val slop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private val detector = DetectorDeslizamiento(maxOf(slop, 20 * densidad))
    private val accionesAccesibles = mutableListOf<Int>()
    var estiloMinas = false
    var gestoContinuo: Boolean
        get() = detector.continuo
        set(value) { detector.continuo = value }
    var alTocar: ((Int) -> Unit)? = null
    var alDobleTocar: ((Int) -> Unit)? = null
    var alMantener: ((Int) -> Unit)? = null
    var alPulsarTablero: (() -> Unit)? = null
        set(value) { field = value; isClickable = value != null }
    var alIniciarTrazo: ((Int) -> Unit)? = null
    var alPintar: ((Int) -> Unit)? = null
    var alDeslizar: ((Direccion) -> Unit)? = null
        set(value) {
            field = value
            accionesAccesibles.forEach { ViewCompat.removeAccessibilityAction(this,it) }; accionesAccesibles.clear()
            if (value != null) {
                isFocusable = true
                listOf(R.string.arcade_up to Direccion.ARRIBA, R.string.arcade_down to Direccion.ABAJO,
                    R.string.arcade_left to Direccion.IZQUIERDA, R.string.arcade_right to Direccion.DERECHA).forEach { (texto,direccion) ->
                    accionesAccesibles.add(ViewCompat.addAccessibilityAction(this,resources.getString(texto)) { _,_ -> value(direccion); true })
                }
            }
        }
    private var inicioX=0f; private var inicioY=0f
    private var ultimoX=0f; private var ultimoY=0f
    private var distancia=0f
    private var cancelado=false
    private var pintando=false
    private val visitadas=mutableSetOf<Int>()

    init {
        setPadding((8*densidad).toInt(),(8*densidad).toInt(),(8*densidad).toInt(),(8*densidad).toInt())
        clipToOutline=true
    }

    private fun fondo(n:Int) {
        if (estiloMinas) { background = null; setPadding(0,0,0,0); return }
        val paleta=when(n) {
            4 -> R.array.fondo_tablero_2048
            8 -> R.array.fondo_tablero_buscaminas
            6 -> R.array.fondo_tablero_petdoku
            else -> R.array.fondo_tablero_snake
        }
        val colores = resources.coloresJuego(paleta).toIntArray()
        background=GradientDrawable(GradientDrawable.Orientation.TL_BR,colores).apply { cornerRadius=24*densidad;setStroke((1*densidad).toInt(),0x33FFFFFF) }
    }

    fun mostrar(n: Int, valores: List<CasillaArcade>, interactivo: Boolean = true) {
        require(valores.size == n*n)
        serpiente.visibility=GONE; binding.root.visibility=VISIBLE
        if (lado != n) {
            cancelarToquesPendientes(); detectoresToque.clear()
            lado=n; fondo(n); binding.root.removeAllViews(); celdas.clear(); anteriores=emptyList()
            val filas = List(n) {
                FilaTableroBinding.inflate(LayoutInflater.from(context), binding.root, true).root
            }
            repeat(n*n) { i ->
                val fila = filas[i / n]
                val celda=CeldaArcadeBinding.inflate(LayoutInflater.from(context),fila,false).root
                celda.layoutParams=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f).apply {
                    val margen=((if(n==4)4 else 2)*densidad).toInt();setMargins(margen,margen,margen,margen)
                }
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(celda,9,if(n==4)36 else 24,1,android.util.TypedValue.COMPLEX_UNIT_SP)
                celda.setOnClickListener { alTocar?.invoke(i) }
                celda.setOnLongClickListener { alMantener?.invoke(i); alMantener!=null }
                val toques = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent) = true
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        if (celda.isClickable) celda.performClick()
                        return true
                    }
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        if (celda.isClickable) alDobleTocar?.invoke(i)
                        return true
                    }
                    override fun onLongPress(e: MotionEvent) {
                        if (celda.isLongClickable) celda.performLongClick()
                    }
                })
                detectoresToque.add(toques)
                celda.setOnTouchListener { _, event ->
                    if (alDobleTocar == null) false else {
                        toques.onTouchEvent(event)
                        true
                    }
                }
                if (alDobleTocar != null) ViewCompat.addAccessibilityAction(celda, resources.getString(R.string.arcade_pet_discover)) { _, _ ->
                    if (celda.isClickable) alDobleTocar?.invoke(i)
                    true
                }
                fila.addView(celda);celdas.add(celda)
            }
        }
        valores.forEachIndexed { i,valor ->
            val celda=celdas[i]
            celda.isClickable=interactivo && alTocar!=null
            celda.isLongClickable=interactivo && alMantener!=null
            if(anteriores.getOrNull(i)!=valor) {
                celda.text=valor.texto;celda.setTextColor(valor.tinta)
                celda.mostrarIlustracion(valor.ilustracion)
                celda.contentDescription = resources.getString(
                    R.string.casilla_arcade_descripcion, i/n+1, i%n+1,
                    valor.descripcion.ifEmpty { resources.getString(R.string.casilla_arcade_vacia) }
                )
                val inferior=androidx.core.graphics.ColorUtils.blendARGB(valor.color,Color.BLACK,0.07f)
                celda.background=if (valor.plana) null else GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,intArrayOf(valor.color,inferior)).apply {
                    cornerRadius=(if(n==4)12f else 7f)*densidad
                    setStroke(((if(valor.borde)3 else 1)*densidad).toInt(),if(valor.borde)0xFFD92854.toInt() else 0x20FFFFFF)
                }
                if(anteriores.isNotEmpty() && (anteriores[i].texto!=valor.texto || anteriores[i].ilustracion!=valor.ilustracion) && (valor.texto.isNotEmpty() || valor.ilustracion!=0)) {
                    celda.animate().cancel();celda.scaleX=.86f;celda.scaleY=.86f
                    celda.animate().scaleX(1f).scaleY(1f).setDuration(130).start()
                }
            }
        }
        anteriores=valores.toList()
    }

    fun mostrarSnake(cuerpo:List<Int>,comida:Int,direccion:Direccion,mensaje:String,animar:Boolean) {
        if(lado!=14){lado=14;fondo(14)}
        binding.root.visibility=GONE;serpiente.visibility=VISIBLE
        serpiente.mensaje=mensaje;serpiente.mostrar(cuerpo,comida,direccion,animar)
        contentDescription = resources.getString(
            R.string.snake_descripcion, cuerpo.first()/14+1, cuerpo.first()%14+1, mensaje
        )
    }

    override fun onMeasure(widthMeasureSpec:Int,heightMeasureSpec:Int) {
        val alto = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) Int.MAX_VALUE else MeasureSpec.getSize(heightMeasureSpec)
        val ancho=minOf(MeasureSpec.getSize(widthMeasureSpec),alto,(460*densidad).toInt())
        super.onMeasure(MeasureSpec.makeMeasureSpec(ancho,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(ancho,MeasureSpec.EXACTLY))
    }

    private fun iniciar(ev:MotionEvent) {
        inicioX=ev.x;inicioY=ev.y;ultimoX=ev.x;ultimoY=ev.y;distancia=0f
        cancelado=false;pintando=false;visitadas.clear();detector.iniciar(ev.x,ev.y)
        if(alDeslizar!=null || alPintar!=null)parent.requestDisallowInterceptTouchEvent(true)
    }
    override fun onInterceptTouchEvent(ev:MotionEvent):Boolean {
        if(ev.actionMasked==MotionEvent.ACTION_DOWN)iniciar(ev)
        if(alDeslizar!=null)return true
        when(ev.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN,MotionEvent.ACTION_CANCEL -> cancelar()
            MotionEvent.ACTION_MOVE -> if(!cancelado && alPintar!=null && maxOf(abs(ev.x-inicioX),abs(ev.y-inicioY))>slop*2) {
                val i=indice(inicioX,inicioY)
                if(i>=0) { pintando=true;alIniciarTrazo?.invoke(i);pintarHasta(ev.x,ev.y);return true }
            }
            MotionEvent.ACTION_UP -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return false
    }
    override fun onTouchEvent(ev:MotionEvent):Boolean {
        if(alDeslizar==null && !pintando)return super.onTouchEvent(ev)
        when(ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> iniciar(ev)
            MotionEvent.ACTION_POINTER_DOWN,MotionEvent.ACTION_CANCEL -> cancelar()
            MotionEvent.ACTION_MOVE,MotionEvent.ACTION_UP -> {
                distancia=maxOf(distancia,abs(ev.x-inicioX),abs(ev.y-inicioY))
                if(!cancelado) {
                    if(pintando)pintarHasta(ev.x,ev.y)
                    else detector.mover(ev.x,ev.y)?.let { alDeslizar?.invoke(it) }
                }
                if(ev.actionMasked==MotionEvent.ACTION_UP) {
                    if(!cancelado && !pintando && distancia<=slop)performClick()
                    cancelar()
                }
            }
        }
        return true
    }
    private fun indice(x:Float,y:Float):Int {
        val gx=x-binding.root.left;val gy=y-binding.root.top
        return celdas.indexOfFirst {
            val fila = it.parent as LinearLayout
            gx >= fila.left + it.left && gx < fila.left + it.right &&
                gy >= fila.top + it.top && gy < fila.top + it.bottom
        }
    }
    private fun pintarHasta(x:Float,y:Float) {
        val pasos=maxOf(1,(maxOf(abs(x-ultimoX),abs(y-ultimoY))/(width.toFloat()/maxOf(lado,1)/3)).toInt())
        for(paso in 0..pasos) {
            val i=indice(ultimoX+(x-ultimoX)*paso/pasos,ultimoY+(y-ultimoY)*paso/pasos)
            if(i>=0 && visitadas.add(i))alPintar?.invoke(i)
        }
        ultimoX=x;ultimoY=y
    }
    private fun cancelar() { cancelado=true;pintando=false;detector.cancelar();parent.requestDisallowInterceptTouchEvent(false) }
    fun cancelarToquesPendientes() {
        val ahora = android.os.SystemClock.uptimeMillis()
        val evento = MotionEvent.obtain(ahora, ahora, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
        detectoresToque.forEach { it.onTouchEvent(evento) }
        evento.recycle()
    }
    override fun onDetachedFromWindow() { cancelarToquesPendientes(); super.onDetachedFromWindow() }
    override fun performClick():Boolean { super.performClick();alPulsarTablero?.invoke();return true }
}
