package com.example.arcadia.juegos.petdoku

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import com.google.android.material.button.MaterialButton
import com.example.arcadia.R
import com.example.arcadia.arcade.CasillaArcade
import com.example.arcadia.arcade.PartidaArcadeActivity

class PetdokuActivity : PartidaArcadeActivity() {
    private val modelo: PetdokuViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    private var borrando = false
    private lateinit var siguiente: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(R.string.arcade_pet_title,R.string.arcade_pet_help);modelo.cargar(guardado)
        binding.board.alTocar={ tocar(it,false) }
        binding.board.alDobleTocar={ tocar(it,true) }
        binding.board.alIniciarTrazo = { borrando = modelo.juego.marcas[it] == 2 }
        binding.board.alPintar = {
            if (modelo.juego.pintarDescarte(it, borrando)) { modelo.guardar(guardado); mostrar() }
        }
        siguiente = boton(R.string.arcade_pet_next) { binding.board.cancelarToquesPendientes();modelo.juego.siguiente();modelo.guardar(guardado);mostrar() }
        mostrar();modelo.guardar(guardado)
    }
    private fun tocar(i:Int,descubrir:Boolean) {
        val cambio = if (descubrir) modelo.juego.descubrir(i) else modelo.juego.tocar(i)
        if(cambio){respuesta();modelo.guardar(guardado);mostrar()}
    }
    private fun mostrar() {
        val juego=modelo.juego
        val colores=listOf("#F2AAC8","#9AD7AF","#A6D9EF","#F3CD90","#C8B5EB","#F1B5A4")
        binding.board.mostrar(6,List(36){i->
            val error = juego.marcas[i] == 3
            CasillaArcade(when(juego.marcas[i]){1->"🐱";2,3->"×";else->""},Color.parseColor(colores[juego.regiones[i]]),
                tinta=if(error) Color.rgb(184,15,48) else Color.rgb(39,32,43),
                borde=error,descripcion="Región ${juego.regiones[i]+1}, "+when(juego.marcas[i]){1->"mascota descubierta";2->"descartada";3->"error, casilla sin mascota";else->"vacía"})
        },!juego.terminada)
        val corazones = List(3) { if (it < juego.vidas) "♥" else "♡" }.joinToString(" ")
        estadisticas(getString(R.string.arcade_pet_level,juego.nivel+1),getString(R.string.arcade_pet_lives,corazones))
        binding.statRight.contentDescription=getString(R.string.arcade_pet_lives_description,juego.vidas)
        siguiente.visibility = if (juego.victoria) View.VISIBLE else View.GONE
        binding.status.text=when {
            juego.vidas == 0 -> getString(R.string.arcade_pet_lost)
            juego.victoria -> getString(R.string.arcade_pet_won)
            else -> getString(R.string.arcade_pet_progress,juego.mascotas.size) + "\n" + getString(if(juego.vidas<3) R.string.arcade_pet_error else R.string.arcade_pet_hint)
        }
    }
    override fun reiniciar() { binding.board.cancelarToquesPendientes();modelo.juego.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() { binding.board.cancelarToquesPendientes();modelo.guardar(guardado);super.onPause() }
}
