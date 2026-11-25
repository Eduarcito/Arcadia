package com.example.arcadia.juegos.snake

import com.example.arcadia.compartido.estado.Direccion
import kotlin.random.Random

class ReglasSnake(private val azar: Random = Random.Default) {
    val lado = 14
    var cuerpo = listOf(7 * lado + 6, 7 * lado + 5, 7 * lado + 4); private set
    var direccion = Direccion.DERECHA; private set
    private var pendiente = direccion
    var comida = -1; private set
    var puntos = 0; private set
    var terminada = false; private set
    val victoria get() = cuerpo.size == lado * lado

    init { colocarComida() }

    fun girar(nueva: Direccion): Boolean {
        if (terminada || (direccion.ordinal + 2) % 4 == nueva.ordinal) return false
        pendiente = nueva
        return true
    }

    fun paso() {
        if (terminada) return
        direccion = pendiente
        val cabeza = cuerpo.first(); val fila = cabeza / lado; val columna = cabeza % lado
        val siguienteFila = fila + when (direccion) { Direccion.ARRIBA -> -1; Direccion.ABAJO -> 1; else -> 0 }
        val siguienteColumna = columna + when (direccion) { Direccion.IZQUIERDA -> -1; Direccion.DERECHA -> 1; else -> 0 }
        val siguiente = siguienteFila * lado + siguienteColumna
        val come = siguiente == comida
        val obstaculos = if (come) cuerpo else cuerpo.dropLast(1)
        if (siguienteFila !in 0 until lado || siguienteColumna !in 0 until lado || siguiente in obstaculos) {
            terminada = true; return
        }
        cuerpo = listOf(siguiente) + if (come) cuerpo else cuerpo.dropLast(1)
        if (come) { puntos += 10; colocarComida(); if (victoria) terminada = true }
    }

    fun reiniciar() {
        cuerpo = listOf(104,103,102); direccion = Direccion.DERECHA; pendiente = direccion
        puntos = 0; terminada = false; colocarComida()
    }

    fun restaurar(c: List<Int>, d: Int, f: Int, p: Int, fin: Boolean): Boolean {
        if (c.size < 3 || c.size > lado * lado || c.distinct().size != c.size || c.any { it !in 0..195 } ||
            c.zipWithNext().any { (a,b) -> kotlin.math.abs(a / lado - b / lado) + kotlin.math.abs(a % lado - b % lado) != 1 } ||
            d !in 0..3 || p < 0 || (c.size < 196 && (f !in 0..195 || f in c))) return false
        cuerpo = c.toList(); direccion = Direccion.entries[d]; pendiente = direccion; comida = f; puntos = p; terminada = fin
        return true
    }

    private fun colocarComida() { comida = (0 until lado * lado).filter { it !in cuerpo }.randomOrNull(azar) ?: -1 }
}
