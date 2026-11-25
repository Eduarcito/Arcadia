package com.example.arcadia.juegos.juego2048

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.arcade.CasillaArcade
import com.example.arcadia.arcade.PartidaArcadeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Juego2048Activity : PartidaArcadeActivity() {
    private val modelo: Juego2048ViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    private var dialogo=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(R.string.juego_2048,R.string.arcade_2048_help)
        modelo.cargar(guardado)
        binding.board.alDeslizar=::mover
        mostrar();modelo.guardar(guardado)
    }
    override fun onResume() { super.onResume(); victoria() }
    private fun mover(d: Direccion) {
        if(dialogo)return
        if(modelo.mover(d)) { respuesta();modelo.guardar(guardado);mostrar();victoria() }
    }
    private fun mostrar() {
        val juego=modelo.juego
        val colores=mapOf(0 to "#343039",2 to "#F1E7DA",4 to "#F1D79C",8 to "#FFBA80",16 to "#FF9A79",32 to "#F88384",64 to "#EE718B",128 to "#E9BF6E",256 to "#F1CA64",512 to "#EAC355",1024 to "#E9B845",2048 to "#FFDC70")
        binding.board.mostrar(4,juego.celdas.map { CasillaArcade(if(it==0) "" else it.toString(),Color.parseColor(colores[it]?:"#C1ED9A")) },false)
        estadisticas(getString(R.string.arcade_score,juego.puntos),getString(R.string.arcade_best,modelo.record))
        binding.status.setText(if(juego.terminado)R.string.arcade_2048_over else if(juego.victoria)R.string.arcade_2048_win else R.string.arcade_2048_hint)
    }
    private fun victoria() {
        if(!modelo.juego.victoria || modelo.victoriaVista || dialogo)return
        dialogo=true
        MaterialAlertDialogBuilder(this).setTitle(R.string.arcade_2048_win).setMessage(R.string.arcade_2048_win_message)
            .setCancelable(false).setPositiveButton(R.string.arcade_continue) { _,_-> modelo.victoriaVista=true;dialogo=false;modelo.guardar(guardado) }.show()
    }
    override fun reiniciar() { modelo.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() { modelo.guardar(guardado);super.onPause() }
}
