package com.example.arcadia.juegos.petdoku

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.arcadia.R

data class MascotaPetdoku(@DrawableRes val imagen: Int, @StringRes val nombre: Int)

// El orden es fijo para conservar la mascota al reintentar o recuperar una partida
object MascotasPetdoku {
    val todas = listOf(
        MascotaPetdoku(R.drawable.petdoku_perrito, R.string.petdoku_animal_perrito),
        MascotaPetdoku(R.drawable.petdoku_gatito, R.string.petdoku_animal_gatito),
        MascotaPetdoku(R.drawable.petdoku_conejito, R.string.petdoku_animal_conejito),
        MascotaPetdoku(R.drawable.petdoku_zorrito, R.string.petdoku_animal_zorrito),
        MascotaPetdoku(R.drawable.petdoku_panda, R.string.petdoku_animal_panda),
        MascotaPetdoku(R.drawable.petdoku_tigre, R.string.petdoku_animal_tigre),
        MascotaPetdoku(R.drawable.petdoku_osito, R.string.petdoku_animal_osito)
    )

    fun paraNivel(nivel: Int): MascotaPetdoku = todas[nivel % todas.size]
}
