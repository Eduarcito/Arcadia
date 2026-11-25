package com.example.arcadia.juegos.kenken

enum class OperacionKenKen {
    VALOR,
    SUMA,
    RESTA,
    MULTIPLICACION,
    DIVISION
}

data class JaulaKenKen(
    val casillas: List<Int>,
    val objetivo: Int,
    val operacion: OperacionKenKen
)

data class RompecabezasKenKen(
    val tamano: Int,
    val jaulas: List<JaulaKenKen>
)

internal fun crearRompecabezasDePrueba(): RompecabezasKenKen {
    return RompecabezasKenKen(
        tamano = 4,
        jaulas = listOf(
            JaulaKenKen(listOf(0, 4), 2, OperacionKenKen.DIVISION),
            JaulaKenKen(listOf(1, 2, 5), 8, OperacionKenKen.SUMA),
            JaulaKenKen(listOf(3, 7), 3, OperacionKenKen.RESTA),
            JaulaKenKen(listOf(6, 10, 11), 8, OperacionKenKen.MULTIPLICACION),
            JaulaKenKen(listOf(8, 9), 1, OperacionKenKen.RESTA),
            JaulaKenKen(listOf(12, 13), 4, OperacionKenKen.DIVISION),
            JaulaKenKen(listOf(14, 15), 5, OperacionKenKen.SUMA)
        )
    )
}
