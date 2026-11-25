package com.example.arcadia.juegos.kenken

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.example.arcadia.R
import com.example.arcadia.databinding.CeldaKenkenBinding
import com.example.arcadia.databinding.FilaTableroBinding
import com.example.arcadia.databinding.TableroKenkenBinding

class TableroKenKenView @JvmOverloads constructor(
    contexto: Context,
    atributos: AttributeSet? = null
) : FrameLayout(contexto, atributos) {
    private val inflador = LayoutInflater.from(contexto)
    private val binding = TableroKenkenBinding.inflate(inflador, this, true)
    private val celdas = mutableListOf<CeldaKenkenBinding>()
    private var accionSeleccionar: ((Int, Int) -> Unit)? = null
    private var estadoMostrado = false
    private val descripcionesJaulas = MutableList(TAMANO_TABLERO * TAMANO_TABLERO) { "" }

    init {
        crearCasillas()
    }

    fun alSeleccionarCasilla(accion: (Int, Int) -> Unit) {
        accionSeleccionar = accion
    }

    fun mostrarJaulas(rompecabezas: RompecabezasKenKen) {
        require(rompecabezas.tamano == TAMANO_TABLERO)
        val jaulasPorCasilla = mutableMapOf<Int, JaulaKenKen>()
        for (jaula in rompecabezas.jaulas) {
            for (indice in jaula.casillas) {
                jaulasPorCasilla[indice] = jaula
            }
        }
        for ((indice, celda) in celdas.withIndex()) {
            val jaula = jaulasPorCasilla.getValue(indice)
            val fila = indice / TAMANO_TABLERO
            val columna = indice % TAMANO_TABLERO
            val simbolo = obtenerSimbolo(jaula.operacion)
            val pista = context.getString(R.string.kenken_pista_jaula, jaula.objetivo, simbolo)
            celda.pistaJaula.text = if (indice == jaula.casillas.minOrNull()) pista else ""
            descripcionesJaulas[indice] = context.getString(
                R.string.kenken_descripcion_jaula,
                jaula.objetivo,
                obtenerNombreOperacion(jaula.operacion)
            )
            celda.bordeSuperior.isVisible = fila == 0
            celda.bordeInicio.isVisible = columna == 0
            celda.bordeFin.isVisible = columna == TAMANO_TABLERO - 1 ||
                jaulasPorCasilla[indice + 1] != jaula
            celda.bordeInferior.isVisible = fila == TAMANO_TABLERO - 1 ||
                jaulasPorCasilla[indice + TAMANO_TABLERO] != jaula
        }
    }

    fun mostrarEstado(valores: List<Int>, casillaSeleccionada: Int?, habilitado: Boolean, duplicados: Set<Int> = emptySet()) {
        for ((indice, celda) in celdas.withIndex()) {
            val valor = valores[indice]
            val texto = if (valor == 0) "" else valor.toString()
            val cambioValor = estadoMostrado && celda.valor.text.toString() != texto
            celda.valor.text = texto
            val duplicada = indice in duplicados && valor != 0
            celda.valor.setTextColor(context.getColor(
                if (duplicada) R.color.kenken_tinte_borrar else R.color.kenken_texto_tablero
            ))
            if (cambioValor) {
                if (valor == 0) {
                    animarRespuestaKenKen(celda.root, escalaInicial = 1f)
                } else {
                    animarRespuestaKenKen(celda.valor, escalaInicial = 0.9f)
                }
            }
            celda.root.isSelected = indice == casillaSeleccionada
            celda.root.isEnabled = habilitado
            celda.root.isClickable = habilitado
            val textoValor = if (valor == 0) {
                context.getString(R.string.kenken_casilla_vacia)
            } else {
                valor.toString()
            }
            val descripcion = StringBuilder(context.getString(
                R.string.kenken_descripcion_casilla,
                indice / TAMANO_TABLERO + 1,
                indice % TAMANO_TABLERO + 1,
                textoValor,
                descripcionesJaulas[indice]
            ))
            if (duplicada) {
                descripcion.append(context.getString(R.string.kenken_repetida))
            }
            celda.root.contentDescription = descripcion
            if (cambioValor) {
                celda.root.announceForAccessibility(
                    context.getString(
                        R.string.kenken_anuncio_cambio,
                        indice / TAMANO_TABLERO + 1,
                        indice % TAMANO_TABLERO + 1,
                        textoValor
                    )
                )
            }
        }
        estadoMostrado = true
    }

    fun animarVictoria() {
        animarRespuestaKenKen(this, escalaInicial = 0.985f, duracion = 260L)
    }

    private fun obtenerSimbolo(operacion: OperacionKenKen): String {
        val recurso = when (operacion) {
            OperacionKenKen.VALOR -> return ""
            OperacionKenKen.SUMA -> R.string.kenken_simbolo_incrementar
            OperacionKenKen.RESTA -> R.string.kenken_simbolo_decrementar
            OperacionKenKen.MULTIPLICACION -> R.string.kenken_simbolo_multiplicar
            OperacionKenKen.DIVISION -> R.string.kenken_simbolo_dividir
        }
        return context.getString(recurso)
    }

    private fun obtenerNombreOperacion(operacion: OperacionKenKen): String {
        val recurso = when (operacion) {
            OperacionKenKen.VALOR -> R.string.kenken_operacion_valor
            OperacionKenKen.SUMA -> R.string.kenken_operacion_suma
            OperacionKenKen.RESTA -> R.string.kenken_operacion_resta
            OperacionKenKen.MULTIPLICACION -> R.string.kenken_operacion_multiplicacion
            OperacionKenKen.DIVISION -> R.string.kenken_operacion_division
        }
        return context.getString(recurso)
    }

    private fun crearCasillas() {
        for (fila in 0 until TAMANO_TABLERO) {
            val vistaFila = FilaTableroBinding.inflate(inflador, binding.root, true)

            for (columna in 0 until TAMANO_TABLERO) {
                val celda = CeldaKenkenBinding.inflate(inflador, vistaFila.root, true)
                celda.root.setOnClickListener { accionSeleccionar?.invoke(fila, columna) }
                celdas.add(celda)
            }
        }
    }

    private companion object {
        const val TAMANO_TABLERO = 4
    }
}
