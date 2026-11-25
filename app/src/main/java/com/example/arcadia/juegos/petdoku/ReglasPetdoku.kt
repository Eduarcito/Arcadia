package com.example.arcadia.juegos.petdoku

import kotlin.math.abs

// Doce niveles con regiones conectadas; cada tablero tiene una solución única.
// Los números identifican las seis regiones de color de cada tablero.
val NIVELES_PETDOKU = listOf(
    listOf(1,1,1,1,0,0,1,1,1,1,0,2,1,1,1,1,1,2,1,3,3,1,1,1,1,3,4,4,4,1,5,5,4,4,4,4),
    listOf(0,0,0,0,1,1,0,0,0,0,1,1,2,2,2,0,1,1,3,3,0,0,0,4,5,5,5,4,4,4,5,5,5,4,4,4),
    listOf(0,0,2,2,2,1,0,3,3,2,2,1,3,3,2,2,2,2,3,3,4,2,2,2,3,3,4,2,2,2,3,4,4,5,5,2),
    listOf(2,2,0,0,1,1,2,2,2,2,1,1,5,3,4,2,4,4,5,3,4,4,4,4,5,4,4,4,4,4,5,5,5,4,4,4),
    listOf(0,0,1,1,1,1,4,1,1,1,1,1,4,1,1,3,1,2,4,4,4,3,5,2,4,4,4,3,5,2,4,4,4,4,5,5),
    listOf(1,1,1,1,0,2,1,1,1,1,0,2,1,3,1,1,1,2,1,3,4,4,4,4,5,5,4,4,4,4,5,5,4,4,4,4),
    listOf(4,4,4,1,0,0,4,4,4,1,1,1,2,2,4,1,1,3,2,2,4,4,3,3,5,5,4,4,4,4,5,5,5,4,4,4),
    listOf(1,1,1,1,2,0,5,1,1,1,2,0,5,3,1,1,2,2,5,3,4,4,4,4,5,4,4,4,4,4,5,4,4,4,4,4),
    listOf(1,1,1,2,0,2,1,1,1,2,0,2,3,3,4,2,2,2,3,3,4,4,2,4,5,4,4,4,4,4,5,4,4,4,4,4),
    listOf(0,0,1,1,1,1,2,1,1,1,1,1,2,3,3,1,1,1,3,3,3,5,5,4,3,3,5,5,5,4,5,5,5,5,5,4),
    listOf(3,1,0,0,0,2,3,1,0,3,3,2,3,3,3,3,4,2,3,3,3,3,4,5,3,4,4,4,4,5,3,3,4,4,5,5),
    listOf(3,0,0,1,5,2,3,3,3,1,5,2,4,4,3,3,5,2,4,4,3,5,5,2,4,4,5,5,5,5,5,5,5,5,5,5)
)

/** The hidden pets are checked against the unique solution, even on an empty board. */
class ReglasPetdoku {
    val lado = 6
    var nivel = 0; private set
    val marcas = MutableList(36) { VACIA }
    val regiones get() = NIVELES_PETDOKU[nivel]
    val mascotas get() = marcas.indices.filter { marcas[it] == MASCOTA }
    val vidas get() = 3 - marcas.count { it == ERROR }
    val victoria get() = mascotas.size == lado
    val terminada get() = victoria || vidas == 0

    /** Single tap adds/removes a note; confirmed pets and red errors stay visible. */
    fun tocar(i: Int): Boolean {
        if (i !in marcas.indices || terminada || marcas[i] == MASCOTA || marcas[i] == ERROR) return false
        marcas[i] = if (marcas[i] == DESCARTADA) VACIA else DESCARTADA
        return true
    }

    /** One incorrect discovery costs one life; the same known error is not charged again. */
    fun descubrir(i: Int): Boolean {
        if (i !in marcas.indices || terminada || marcas[i] == MASCOTA || marcas[i] == ERROR) return false
        marcas[i] = if (i in soluciones[nivel]) MASCOTA else ERROR
        return true
    }

    fun reiniciar() { marcas.fill(VACIA) }
    fun pintarDescarte(i: Int, borrar: Boolean): Boolean {
        if (i !in marcas.indices || terminada || marcas[i] == MASCOTA || marcas[i] == ERROR) return false
        val valor = if (borrar) VACIA else DESCARTADA
        if (marcas[i] == valor) return false
        marcas[i] = valor
        return true
    }
    fun siguiente() { nivel = (nivel + 1) % NIVELES_PETDOKU.size; reiniciar() }

    fun restaurar(n: Int, m: List<Int>, vidasGuardadas: Int = 3): Boolean {
        if (n !in NIVELES_PETDOKU.indices || m.size != 36 || m.any { it !in VACIA..ERROR } ||
            vidasGuardadas !in 0..3 || m.count { it == ERROR } != 3 - vidasGuardadas ||
            m.indices.any { m[it] == ERROR && it in soluciones[n] }) return false
        nivel = n
        // Old saves allowed unverified pets: preserve those guesses as ordinary notes.
        m.forEachIndexed { i, v -> marcas[i] = if (v == MASCOTA && i !in soluciones[n]) DESCARTADA else v }
        return true
    }

    companion object {
        // Estados de cada casilla; los valores se conservan en el guardado JSON
        const val VACIA = 0
        const val MASCOTA = 1
        const val DESCARTADA = 2
        const val ERROR = 3

        private val soluciones: List<Set<Int>> by lazy {
            NIVELES_PETDOKU.map { regiones ->
                val columnas = IntArray(6) { -1 }
                fun buscar(fila: Int, usadas: Set<Int>): Boolean {
                    if (fila == 6) return true
                    for (columna in 0..5) {
                        val region = regiones[fila * 6 + columna]
                        if (region in usadas || (0 until fila).any { columnas[it] == columna } ||
                            (fila > 0 && abs(columnas[fila - 1] - columna) <= 1)) continue
                        columnas[fila] = columna
                        if (buscar(fila + 1, usadas + region)) return true
                    }
                    return false
                }
                check(buscar(0, emptySet())) { "Nivel Petdoku sin solución" }
                columnas.mapIndexed { fila, columna -> fila * 6 + columna }.toSet()
            }
        }
    }
}
