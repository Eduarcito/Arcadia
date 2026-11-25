package com.example.arcadia.juegos.juego2048

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.arcadia.R
import com.example.arcadia.compartido.estado.Direccion
import com.example.arcadia.compartido.ui.CasillaArcade
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.ui.coloresJuego
import com.example.arcadia.databinding.ActivityJuego2048Binding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Juego2048Activity : PartidaArcadeActivity() {
    private val modelo: Juego2048ViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    override val partidaPerdida get() = modelo.juego.terminado
    private var dialogoVictoria: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivityJuego2048Binding.inflate(layoutInflater).root)
        pantalla.usarPantallaCompleta()
        modelo.cargar(guardado)
        pantalla.tablero.alDeslizar=::mover
        mostrar();modelo.guardar(guardado)
    }
    override fun onResume() { super.onResume(); victoria() }
    private fun mover(d: Direccion) {
        if(dialogoVictoria != null || partidaPerdida)return
        if(modelo.mover(d)) { respuesta();modelo.guardar(guardado);mostrar();victoria() }
    }
    private fun mostrar() {
        val juego=modelo.juego
        val colores = listOf(0,2,4,8,16,32,64,128,256,512,1024,2048)
            .zip(resources.coloresJuego(R.array.paleta_2048)).toMap()
        pantalla.tablero.mostrar(4,juego.celdas.map {
            CasillaArcade(if(it==0) "" else it.toString(), colores[it] ?: getColor(R.color.ficha_2048_superior))
        },false)
        estadisticas(getString(R.string.arcade_score,juego.puntos),getString(R.string.arcade_best,modelo.record))
        pantalla.mostrarEstado(if(juego.terminado)R.string.arcade_2048_over else if(juego.victoria)R.string.arcade_2048_win else R.string.arcade_2048_hint)
        comprobarDerrota()
    }
    private fun victoria() {
        if(!modelo.juego.victoria || modelo.victoriaVista || dialogoVictoria != null || partidaPerdida)return
        dialogoVictoria = MaterialAlertDialogBuilder(this).setTitle(R.string.arcade_2048_win).setMessage(R.string.arcade_2048_win_message)
            .setCancelable(false).setPositiveButton(R.string.arcade_continue) { _,_-> modelo.victoriaVista=true;modelo.guardar(guardado) }
            .create().also { dialogo ->
                dialogo.setOnDismissListener { if (dialogoVictoria === dialogo) dialogoVictoria = null }
                dialogo.show()
            }
    }
    override fun reinicioPierdeProgreso() = !modelo.juego.terminado && modelo.juego.puntos > 0
    override fun reiniciar() { modelo.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() {
        dialogoVictoria?.dismiss(); dialogoVictoria = null
        modelo.guardar(guardado);super.onPause()
    }
}
