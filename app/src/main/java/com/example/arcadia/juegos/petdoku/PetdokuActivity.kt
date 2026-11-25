package com.example.arcadia.juegos.petdoku

import android.os.Bundle
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.CasillaArcade
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.ui.coloresJuego
import com.example.arcadia.databinding.ActivityPetdokuBinding
import com.example.arcadia.compartido.estado.ARCHIVO_PARTIDAS

class PetdokuActivity : PartidaArcadeActivity() {
    private val modelo: PetdokuViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences(ARCHIVO_PARTIDAS,MODE_PRIVATE) }
    private var borrando = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivityPetdokuBinding.inflate(layoutInflater).root);modelo.cargar(guardado)
        pantalla.tablero.alTocar={ tocar(it,false) }
        pantalla.tablero.alDobleTocar={ tocar(it,true) }
        pantalla.tablero.alIniciarTrazo = { borrando = modelo.juego.marcas[it] == ReglasPetdoku.DESCARTADA }
        pantalla.tablero.alPintar = {
            if (modelo.pintarDescarte(it, borrando)) { modelo.guardar(guardado); mostrar() }
        }
        pantalla.alSiguienteNivel { pantalla.tablero.cancelarToquesPendientes();modelo.siguiente();modelo.guardar(guardado);mostrar() }
        mostrar();modelo.guardar(guardado)
    }
    private fun tocar(i:Int,descubrir:Boolean) {
        val cambio = modelo.tocar(i, descubrir)
        if(cambio){respuesta();modelo.guardar(guardado);mostrar()}
    }
    private fun mostrar() {
        val juego=modelo.juego
        val colores = resources.coloresJuego(R.array.regiones_petdoku)
        pantalla.tablero.mostrar(6,List(36){i->
            val error = juego.marcas[i] == ReglasPetdoku.ERROR
            val descripcion = when (juego.marcas[i]) {
                ReglasPetdoku.MASCOTA -> R.string.mascota_descubierta_descripcion
                ReglasPetdoku.DESCARTADA -> R.string.casilla_descartada_descripcion
                ReglasPetdoku.ERROR -> R.string.error_petdoku_descripcion
                else -> R.string.casilla_arcade_vacia
            }
            CasillaArcade(when(juego.marcas[i]){ReglasPetdoku.DESCARTADA,ReglasPetdoku.ERROR->"×";else->""},colores[juego.regiones[i]],icono=if(juego.marcas[i]==ReglasPetdoku.MASCOTA) R.drawable.ic_mascota else 0,
                tinta=getColor(if(error) R.color.tinta_error_petdoku else R.color.tinta_casilla),
                borde=error,descripcion=getString(R.string.region_petdoku_descripcion,juego.regiones[i]+1,getString(descripcion)))
        },!juego.terminada)
        val corazones = List(3) { if (it < juego.vidas) "♥" else "♡" }.joinToString(" ")
        estadisticas(getString(R.string.arcade_pet_level,juego.nivel+1),getString(R.string.arcade_pet_lives,corazones))
        pantalla.describirEstadisticaDerecha(getString(R.string.arcade_pet_lives_description,juego.vidas))
        pantalla.mostrarSiguienteNivel(juego.victoria)
        pantalla.mostrarEstado(when {
            juego.vidas == 0 -> getString(R.string.arcade_pet_lost)
            juego.victoria -> getString(R.string.arcade_pet_won)
            else -> getString(R.string.arcade_pet_progress,juego.mascotas.size) + "\n" + getString(if(juego.vidas<3) R.string.arcade_pet_error else R.string.arcade_pet_hint)
        })
    }
    override fun reinicioPierdeProgreso() = !modelo.juego.terminada && modelo.juego.marcas.any { it != 0 }
    override fun reiniciar() { pantalla.tablero.cancelarToquesPendientes();modelo.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() { pantalla.tablero.cancelarToquesPendientes();modelo.guardar(guardado);super.onPause() }
}
