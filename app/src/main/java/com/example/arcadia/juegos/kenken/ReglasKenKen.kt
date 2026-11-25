package com.example.arcadia.juegos.kenken

enum class ResultadoComprobacionKenKen {
    SIN_COMPROBAR,
    INCOMPLETO,
    INCORRECTO,
    CORRECTO
}

internal fun comprobarTablero(
    valores: List<Int>,
    rompecabezas: RompecabezasKenKen
): ResultadoComprobacionKenKen {
    val tamano = rompecabezas.tamano
    if (valores.size != tamano * tamano) {
        return ResultadoComprobacionKenKen.INCORRECTO
    }
    if (valores.any { it == 0 }) {
        return ResultadoComprobacionKenKen.INCOMPLETO
    }
    if (valores.any { it !in 1..tamano }) {
        return ResultadoComprobacionKenKen.INCORRECTO
    }

    for (indice in 0 until tamano) {
        val fila = mutableSetOf<Int>()
        val columna = mutableSetOf<Int>()
        for (posicion in 0 until tamano) {
            fila.add(valores[indice * tamano + posicion])
            columna.add(valores[posicion * tamano + indice])
        }
        if (fila.size != tamano || columna.size != tamano) {
            return ResultadoComprobacionKenKen.INCORRECTO
        }
    }

    for (jaula in rompecabezas.jaulas) {
        if (!cumpleJaula(jaula.casillas.map { valores[it] }, jaula.objetivo, jaula.operacion)) {
            return ResultadoComprobacionKenKen.INCORRECTO
        }
    }
    return ResultadoComprobacionKenKen.CORRECTO
}

internal fun cumpleJaula(
    valores: List<Int>,
    objetivo: Int,
    operacion: OperacionKenKen
): Boolean {
    if (valores.isEmpty() || valores.any { it <= 0 }) {
        return false
    }
    return when (operacion) {
        OperacionKenKen.VALOR -> valores.size == 1 && valores[0] == objetivo
        OperacionKenKen.SUMA -> valores.sum() == objetivo
        OperacionKenKen.MULTIPLICACION -> {
            var producto = 1
            for (valor in valores) {
                producto *= valor
            }
            producto == objetivo
        }
        OperacionKenKen.RESTA, OperacionKenKen.DIVISION -> {
            if (valores.size != 2) {
                false
            } else {
                val mayor = maxOf(valores[0], valores[1])
                val menor = minOf(valores[0], valores[1])
                if (operacion == OperacionKenKen.RESTA) {
                    mayor - menor == objetivo
                } else {
                    mayor % menor == 0 && mayor / menor == objetivo
                }
            }
        }
    }
}
