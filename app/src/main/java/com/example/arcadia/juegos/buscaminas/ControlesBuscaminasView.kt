package com.example.arcadia.juegos.buscaminas

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.arcadia.R
import com.example.arcadia.databinding.ControlesBuscaminasBinding

class ControlesBuscaminasView @JvmOverloads constructor(context:Context,attrs:AttributeSet?=null):FrameLayout(context,attrs){
    private val binding=ControlesBuscaminasBinding.inflate(LayoutInflater.from(context),this,true)
    fun alCambiarModo(accion:(Boolean)->Unit){
        binding.modo.addOnButtonCheckedListener { _,id,checked -> if(checked)accion(id==R.id.marcar) }
    }
    fun alPedirPista(accion:()->Unit){binding.pista.setOnClickListener { accion() }}
    fun mostrar(minas:Int,banderas:Boolean,pistaDisponible:Boolean){
        binding.minasRestantes.text=resources.getString(R.string.minas_contador,minas)
        binding.minasRestantes.contentDescription=resources.getString(R.string.minas_contador_descripcion,minas)
        binding.modo.check(if(banderas)R.id.marcar else R.id.descubrir)
        binding.pista.isEnabled=pistaDisponible
    }
}
