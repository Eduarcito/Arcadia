package com.example.arcadia.juegos.buscaminas

import kotlin.random.Random

class ReglasBuscaminas(private val azar: Random = Random.Default) {
    val lado = 8
    val cantidadMinas = 10
    val minas = mutableSetOf<Int>()
    val abiertas = mutableSetOf<Int>()
    val banderas = mutableSetOf<Int>()
    var iniciada = false; private set
    var explosion = -1; private set
    val victoria get() = iniciada && abiertas.size == lado * lado - cantidadMinas && explosion == -1
    val terminada get() = explosion >= 0 || victoria

    fun vecinas(i: Int) = (0 until lado * lado).filter {
        it != i && kotlin.math.abs(it / lado - i / lado) <= 1 && kotlin.math.abs(it % lado - i % lado) <= 1
    }
    fun alrededor(i: Int) = vecinas(i).count { it in minas }

    fun marcar(i: Int): Boolean {
        if (i !in 0 until lado * lado || terminada || i in abiertas) return false
        if (i in banderas) banderas.remove(i)
        else if (banderas.size < cantidadMinas) banderas.add(i) else return false
        return true
    }

    fun abrir(i: Int): Boolean {
        if (i !in 0 until lado * lado || terminada || i in banderas || i in abiertas) return false
        if (!iniciada) {
            val protegidas = vecinas(i) + i
            minas.addAll((0 until lado * lado).filter { it !in protegidas }.shuffled(azar).take(cantidadMinas))
            iniciada = true
        }
        if (i in minas) { explosion = i; return true }
        val pendientes = ArrayDeque<Int>(); pendientes.add(i)
        while (pendientes.isNotEmpty()) {
            val actual = pendientes.removeFirst()
            if (actual in banderas || actual in minas || !abiertas.add(actual)) continue
            if (alrededor(actual) == 0) pendientes.addAll(vecinas(actual).filter { it !in abiertas })
        }
        return true
    }

    fun reiniciar() { minas.clear(); abiertas.clear(); banderas.clear(); iniciada = false; explosion = -1 }

    fun restaurar(m: Set<Int>, a: Set<Int>, b: Set<Int>, inicio: Boolean, golpe: Int): Boolean {
        if ((m + a + b).any { it !in 0..63 } || b.size > cantidadMinas || a.intersect(m + b).isNotEmpty() ||
            (inicio && m.size != cantidadMinas) || (!inicio && (m.isNotEmpty() || a.isNotEmpty())) ||
            (golpe != -1 && golpe !in m)) return false
        minas.clear(); minas.addAll(m); abiertas.clear(); abiertas.addAll(a); banderas.clear(); banderas.addAll(b)
        iniciada = inicio; explosion = golpe
        return true
    }
}
