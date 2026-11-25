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

internal fun crearVariantesKenKen(): List<RompecabezasKenKen> {
    return listOf(
        RompecabezasKenKen(
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
        ),
        RompecabezasKenKen(
            tamano = 4,
            jaulas = listOf(
                JaulaKenKen(listOf(0, 1), 3, OperacionKenKen.DIVISION),
                JaulaKenKen(listOf(2, 3), 2, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(4, 5), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(6, 9, 10), 6, OperacionKenKen.SUMA),
                JaulaKenKen(listOf(7, 11), 12, OperacionKenKen.MULTIPLICACION),
                JaulaKenKen(listOf(8, 12), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(13), 4, OperacionKenKen.VALOR),
                JaulaKenKen(listOf(14, 15), 1, OperacionKenKen.RESTA)
            )
        ),
        RompecabezasKenKen(
            tamano = 4,
            jaulas = listOf(
                JaulaKenKen(listOf(0, 4), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(1, 5), 6, OperacionKenKen.MULTIPLICACION),
                JaulaKenKen(listOf(2, 3), 4, OperacionKenKen.DIVISION),
                JaulaKenKen(listOf(6, 7), 7, OperacionKenKen.SUMA),
                JaulaKenKen(listOf(8), 4, OperacionKenKen.VALOR),
                JaulaKenKen(listOf(9, 10), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(11, 15), 6, OperacionKenKen.MULTIPLICACION),
                JaulaKenKen(listOf(12, 13, 14), 8, OperacionKenKen.SUMA)
            )
        ),
        RompecabezasKenKen(
            tamano = 4,
            jaulas = listOf(
                JaulaKenKen(listOf(0, 1), 6, OperacionKenKen.MULTIPLICACION),
                JaulaKenKen(listOf(2, 3), 3, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(4, 8), 2, OperacionKenKen.DIVISION),
                JaulaKenKen(listOf(5, 6), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(7), 1, OperacionKenKen.VALOR),
                JaulaKenKen(listOf(9, 10), 2, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(11, 15), 5, OperacionKenKen.SUMA),
                JaulaKenKen(listOf(12, 13, 14), 7, OperacionKenKen.SUMA)
            )
        ),
        RompecabezasKenKen(
            tamano = 4,
            jaulas = listOf(
                JaulaKenKen(listOf(0, 1, 2), 7, OperacionKenKen.SUMA),
                JaulaKenKen(listOf(3, 7), 2, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(4), 3, OperacionKenKen.VALOR),
                JaulaKenKen(listOf(5, 6), 2, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(8, 12), 1, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(9, 13), 2, OperacionKenKen.RESTA),
                JaulaKenKen(listOf(10, 11), 12, OperacionKenKen.MULTIPLICACION),
                JaulaKenKen(listOf(14, 15), 2, OperacionKenKen.DIVISION)
            )
        )
    )
}
