package com.example.arcadia.juegos.numberconnect

import kotlin.math.abs
import kotlin.random.Random

class ReglasNumberConnect(private val azar: Random = Random.Default) {
    val columnas = 5
    val filas = 7
    var celdas = List(35) { 0 }; private set
    var puntos = 0L; private set
    val terminado get() = celdas.indices.none { a ->
        celdas.indices.any { b -> b > a && vecinas(a,b) && celdas[a] == celdas[b] && celdas[a] < MAXIMO }
    }
    init { reiniciar() }

    fun vecinas(a: Int, b: Int): Boolean = a in 0..34 && b in 0..34 && a != b &&
        abs(a / columnas - b / columnas) <= 1 && abs(a % columnas - b % columnas) <= 1

    fun puedeAgregar(camino:List<Int>,indice:Int):Boolean = indice in celdas.indices && indice !in camino &&
        (camino.isEmpty() || (camino.all { it in celdas.indices } && celdas[indice]==celdas[camino.first()] && vecinas(camino.last(),indice)))

    fun resultado(camino: List<Int>): Int? {
        if (camino.size < 2 || camino.distinct().size != camino.size || camino.any { it !in celdas.indices }) return null
        val valor=celdas[camino.first()]
        if (camino.any { celdas[it] != valor } || camino.zipWithNext().any { (a,b) -> !vecinas(a,b) }) return null
        var resultado=valor.toLong()
        val suma=valor.toLong()*camino.size
        while(resultado<suma) resultado*=2
        return resultado.takeIf { it <= MAXIMO }?.toInt()
    }

    fun combinar(camino: List<Int>): Boolean {
        val valor=resultado(camino) ?: return false
        val nuevas=celdas.toMutableList()
        camino.forEach { nuevas[it]=0 }
        nuevas[camino.last()]=valor
        puntos+=valor
        for(columna in 0 until columnas) {
            val restantes=(0 until filas).map { nuevas[it*columnas+columna] }.filter { it>0 }
            val vacias=filas-restantes.size
            for(fila in 0 until filas) nuevas[fila*columnas+columna]=if(fila<vacias) generar() else restantes[fila-vacias]
        }
        celdas=nuevas
        return true
    }

    fun reiniciar() {
        puntos=0
        celdas=List(35) { generar() }.toMutableList().apply { this[1]=this[0] }
    }

    fun restaurar(valores: List<Int>, puntuacion: Long): Boolean {
        if(valores.size!=35 || puntuacion<0 || valores.any { it<2 || it>MAXIMO || it and (it-1) != 0 }) return false
        celdas=valores.toList();puntos=puntuacion;return true
    }
    private fun generar() = listOf(2,4,8,16,32)[azar.nextInt(5)]
    companion object { const val MAXIMO = 1 shl 29 }
}
