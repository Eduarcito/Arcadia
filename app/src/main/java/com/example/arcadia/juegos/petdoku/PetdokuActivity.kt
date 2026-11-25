package com.example.arcadia.juegos.petdoku

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import com.example.arcadia.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.CasillaArcade
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.ui.coloresJuego
import com.example.arcadia.databinding.ActivityPetdokuBinding

class PetdokuActivity : PartidaArcadeActivity() {
    private val modelo: PetdokuViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    private lateinit var reglasVisuales: ReglasVisualesPetdokuView
    private var borrando = false
    private val transiciones = Handler(Looper.getMainLooper())
    private var avancePendiente: Runnable? = null
    private var dialogoDerrota: AlertDialog? = null
    private var activa = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivityPetdokuBinding.inflate(layoutInflater).root);modelo.cargar(guardado)
        reglasVisuales = ReglasVisualesPetdokuView(this)
        pantalla.mostrarReglasVisuales(reglasVisuales)
        pantalla.compactarCabecera()
        pantalla.alSiguienteNivel { mostrarSelectorNivel() }
        pantalla.tablero.alTocar={ tocar(it,false) }
        pantalla.tablero.alDobleTocar={ tocar(it,true) }
        pantalla.tablero.alIniciarTrazo = { borrando = modelo.juego.marcas[it] == 2 }
        pantalla.tablero.alPintar = {
            if (modelo.pintarDescarte(it, borrando)) { modelo.guardar(guardado); mostrar() }
        }
        mostrar();modelo.guardar(guardado)
    }
    private fun tocar(i:Int,descubrir:Boolean) {
        val cambio = modelo.tocar(i, descubrir)
        if(cambio){respuesta();modelo.guardar(guardado);mostrar()}
    }
    private fun mostrar() {
        val juego=modelo.juego
        val mascota = MascotasPetdoku.paraNivel(juego.nivel)
        reglasVisuales.mostrarMascota(mascota.imagen)
        val colores = resources.coloresJuego(R.array.regiones_petdoku)
        pantalla.tablero.mostrar(6,List(36){i->
            val error = juego.marcas[i] == 3
            val descripcion = when (juego.marcas[i]) {
                1 -> R.string.mascota_descubierta_descripcion
                2 -> R.string.casilla_descartada_descripcion
                3 -> R.string.error_petdoku_descripcion
                else -> R.string.casilla_arcade_vacia
            }
            val region = juego.regiones[i]
            val mascara = (if (i < 6 || juego.regiones[i-6] != region) 1 else 0) or
                (if (i >= 30 || juego.regiones[i+6] != region) 2 else 0) or
                (if (i % 6 == 0 || juego.regiones[i-1] != region) 4 else 0) or
                (if (i % 6 == 5 || juego.regiones[i+1] != region) 8 else 0)
            CasillaArcade(when(juego.marcas[i]){2,3->"×";else->""},colores[region],
                tinta=getColor(if(error) R.color.tinta_error_petdoku else R.color.tinta_casilla),
                ilustracion=if(juego.marcas[i]==1) mascota.imagen else 0,
                borde=error,bordesRegion=mascara,descripcion=getString(R.string.region_petdoku_descripcion,juego.regiones[i]+1,if (juego.marcas[i] == 1) getString(R.string.petdoku_animal_descubierto, getString(mascota.nombre)) else getString(descripcion)))
        },!juego.terminada)
        val corazones = List(3) { if (it < juego.vidas) "♥" else "♡" }.joinToString(" ")
        estadisticas(getString(R.string.arcade_pet_level,juego.nivel+1),getString(R.string.arcade_pet_lives,corazones))
        pantalla.describirEstadisticaDerecha(getString(R.string.arcade_pet_lives_description,juego.vidas))
        pantalla.mostrarEstado(when {
            juego.vidas == 0 -> getString(R.string.arcade_pet_lost)
            juego.victoria -> getString(R.string.arcade_pet_won)
            else -> getString(R.string.arcade_pet_progress,juego.mascotas.size) + "\n" + getString(if(juego.vidas<3) R.string.arcade_pet_error else R.string.arcade_pet_hint)
        })
        resolverFinDePartida()
        pantalla.mostrarSiguienteNivel(true)
    }

    private fun mostrarSelectorNivel() {
        val niveles = Array(12) { getString(R.string.catalogo_nivel, it + 1) }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.petdoku_elegir_nivel)
            .setSingleChoiceItems(niveles, modelo.juego.nivel) { dialogo, cual ->
                dialogo.dismiss()
                if (cual != modelo.juego.nivel) {
                    cancelarAvance()
                    modelo.irANivel(cual); modelo.guardar(guardado); mostrar()
                }
            }
            .show()
    }

    private fun resolverFinDePartida() {
        if (!activa) return
        when {
            modelo.juego.victoria && avancePendiente == null -> {
                // Let the player see the completed board before loading the next puzzle.
                avancePendiente = Runnable {
                    avancePendiente = null
                    if (activa && modelo.juego.victoria) {
                        pantalla.tablero.cancelarToquesPendientes()
                        modelo.siguiente(); modelo.guardar(guardado); mostrar()
                    }
                }.also { transiciones.postDelayed(it, 900) }
            }
            modelo.juego.vidas == 0 && dialogoDerrota == null -> {
                pantalla.tablero.cancelarToquesPendientes()
                dialogoDerrota = MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.petdoku_sin_vidas)
                    .setMessage(R.string.petdoku_reintento_mensaje)
                    .setCancelable(false)
                    .setPositiveButton(R.string.petdoku_reintentar) { _, _ -> reiniciar() }
                    .setNegativeButton(R.string.petdoku_menu_principal) { _, _ ->
                        startActivity(Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                        finish()
                    }
                    .create().also { dialogo ->
                        dialogo.setOnDismissListener { if (dialogoDerrota === dialogo) dialogoDerrota = null }
                        dialogo.show()
                    }
            }
        }
    }

    private fun cancelarAvance() {
        avancePendiente?.let { transiciones.removeCallbacks(it) }
        avancePendiente = null
    }

    override fun reinicioPierdeProgreso() = !modelo.juego.terminada && modelo.juego.marcas.any { it != 0 }
    override fun reiniciar() {
        cancelarAvance(); pantalla.tablero.cancelarToquesPendientes()
        modelo.reiniciar(); modelo.guardar(guardado); mostrar()
    }
    override fun onResume() { super.onResume(); activa = true; resolverFinDePartida() }
    override fun onPause() {
        activa = false; cancelarAvance()
        dialogoDerrota?.dismiss(); dialogoDerrota = null
        pantalla.tablero.cancelarToquesPendientes();modelo.guardar(guardado);super.onPause()
    }
}
