package com.example.arcadia.juegos.numberconnect

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.arcadia.R
import com.example.arcadia.databinding.ContenidoPrevisualizacionConnectBinding

class PrevisualizacionConnectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding = ContenidoPrevisualizacionConnectBinding.inflate(LayoutInflater.from(context), this, true)

    init { mostrar(null) }

    fun mostrar(previsualizacion: PrevisualizacionConexion?) {
        if (previsualizacion == null) {
            binding.sumaConnect.setText(R.string.connect_preview_vacio)
            binding.resultadoConnect.setText(R.string.connect_preview_sin_ficha)
            return
        }
        val valores = previsualizacion.valores
        val operacion = if (valores.size <= 4) valores.joinToString(" + ")
            else resources.getString(R.string.connect_producto, valores.size, valores.first())
        binding.sumaConnect.text = resources.getString(R.string.connect_suma, operacion, previsualizacion.suma)
        binding.resultadoConnect.text = previsualizacion.resultado?.toString()
            ?: resources.getString(R.string.connect_preview_sin_ficha)
    }
}
