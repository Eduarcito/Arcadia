package com.example.arcadia.juegos.juego2048

import kotlin.random.Random

enum class Direccion { ARRIBA, DERECHA, ABAJO, IZQUIERDA }

class Reglas2048(val azar: Random = Random.Default) {
    var celdas = List(16) { 0 }; private set
    var puntos = 0L; private set
    val victoria get() = celdas.any { it >= 2048 }
    val terminado get() = Direccion.entries.all { deslizar(it).first == celdas }

    init { reiniciar() }

    fun restaurar(valores: List<Int>, puntaje: Long): Boolean {
        if (valores.size != 16 || valores.none { it > 0 } || puntaje < 0 ||
            valores.any { it != 0 && (it < 2 || (it and (it - 1)) != 0) }) return false
        celdas = valores.toList(); puntos = puntaje
        return true
    }

    fun reiniciar() { celdas = List(16) { 0 }; puntos = 0; agregar(); agregar() }

    fun mover(direccion: Direccion): Boolean {
        val (resultado, suma) = deslizar(direccion)
        if (resultado == celdas) return false
        celdas = resultado; puntos += suma; agregar()
        return true
    }

    private fun deslizar(direccion: Direccion): Pair<List<Int>, Long> {
        val salida = MutableList(16) { 0 }
        var suma = 0L
        for (linea in 0..3) {
            val indices = (0..3).map { paso -> when (direccion) {
                Direccion.IZQUIERDA -> linea * 4 + paso
                Direccion.DERECHA -> linea * 4 + 3 - paso
                Direccion.ARRIBA -> paso * 4 + linea
                Direccion.ABAJO -> (3 - paso) * 4 + linea
            } }
            val valores = indices.map { celdas[it] }.filter { it > 0 }
            var lectura = 0; var escritura = 0
            while (lectura < valores.size) {
                val valor = valores[lectura]
                if (lectura + 1 < valores.size && valor == valores[lectura + 1] && valor <= Int.MAX_VALUE / 2) {
                    salida[indices[escritura++]] = valor * 2
                    suma += valor * 2L; lectura += 2
                } else { salida[indices[escritura++]] = valor; lectura++ }
            }
        }
        return salida to suma
    }

    private fun agregar() {
        val libres = celdas.indices.filter { celdas[it] == 0 }
        if (libres.isEmpty()) return
        celdas = celdas.toMutableList().apply { this[libres.random(azar)] = if (azar.nextInt(10) == 0) 4 else 2 }
    }
}
