package com.example.arcadia.juegos.petdoku

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.LinearLayout
import com.example.arcadia.compartido.ui.CasillaIlustradaView
import androidx.core.graphics.ColorUtils
import com.example.arcadia.R
import com.example.arcadia.databinding.TarjetaReglaPetdokuBinding

/** Three small examples that remain visible above the playable board. */
class ReglasVisualesPetdokuView(context: Context) : LinearLayout(context) {
    private val mascotas = mutableListOf<CasillaIlustradaView>()

    fun mostrarMascota(recurso: Int) {
        mascotas.forEach { it.mostrarIlustracion(recurso) }
    }

    private fun dp(valor: Int) = (valor * resources.displayMetrics.density + .5f).toInt()

    init {
        orientation = HORIZONTAL
        val reglas = listOf(R.string.petdoku_regla_color, R.string.petdoku_regla_fila,
            R.string.petdoku_regla_contacto)
        val descartes = listOf(setOf(0, 1, 3), setOf(1, 3, 5, 7), (0..8).filter { it != 4 }.toSet())
        val tinta = context.getColor(R.color.arcade_ink)
        val superficie = context.getColor(R.color.arcade_surface)
        val neutro = ColorUtils.blendARGB(superficie, tinta, .10f)
        val marcado = ColorUtils.blendARGB(superficie, tinta, .24f)
        reglas.forEachIndexed { tipo, texto ->
            val tarjeta = TarjetaReglaPetdokuBinding.inflate(LayoutInflater.from(context), this, false)
            tarjeta.regla.setText(texto)
            tarjeta.root.contentDescription = resources.getString(texto)
            repeat(9) { celda ->
                val casilla = CasillaIlustradaView(context).apply {
                    gravity = Gravity.CENTER
                    textSize = if (celda == 4) 9f else 12f
                    includeFontPadding = false
                    text = when { celda in descartes[tipo] -> "×"; else -> "" }
                    setTextColor(tinta)
                    if (celda == 4) mascotas.add(this)
                    val color = when {
                        tipo == 0 && celda in setOf(0, 1, 3, 4) -> ColorUtils.blendARGB(superficie, context.getColor(R.color.acento_petdoku), .48f)
                        celda in descartes[tipo] -> marcado
                        else -> neutro
                    }
                    background = GradientDrawable().apply { setColor(color); cornerRadius = dp(2).toFloat() }
                    layoutParams = GridLayout.LayoutParams(GridLayout.spec(celda / 3, 1f), GridLayout.spec(celda % 3, 1f)).apply {
                        width = 0; height = 0
                        setMargins(dp(1), dp(1), dp(1), dp(1))
                    }
                }
                tarjeta.diagrama.addView(casilla)
            }
            addView(tarjeta.root, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
                if (tipo > 0) marginStart = dp(6)
            })
        }
    }
}
