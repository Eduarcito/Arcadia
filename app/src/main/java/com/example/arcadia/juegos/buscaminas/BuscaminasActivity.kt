package com.example.arcadia.juegos.buscaminas

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.arcade.CasillaArcade
import com.example.arcadia.arcade.PartidaArcadeActivity

class BuscaminasActivity : PartidaArcadeActivity() {
    private val modelo: BuscaminasViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(R.string.juego_buscaminas,R.string.arcade_mines_help);modelo.cargar(guardado)
        binding.board.alTocar={ actuar(it,false) }
        binding.board.alMantener={ actuar(it,true) }
        mostrar();modelo.guardar(guardado)
    }
    private fun actuar(i:Int,bandera:Boolean) {
        val cambio=if(bandera)modelo.juego.marcar(i) else modelo.juego.abrir(i)
        if(cambio){ respuesta();modelo.guardar(guardado);mostrar() }
    }
    private fun mostrar() {
        val juego=modelo.juego
        val pistas=listOf("#30575A","#2866AE","#236B4D","#A33356","#7051A2","#8C542B","#1D686B","#4C4B53","#4C4B53")
        binding.board.mostrar(8,List(64){i->
            when {
                juego.terminada && i in juego.minas -> CasillaArcade(if(i in juego.banderas)"⚑" else "✹",Color.parseColor(if(i==juego.explosion)"#F2889C" else "#F5C3AC"),descripcion="Mina")
                i in juego.banderas -> CasillaArcade(if(juego.terminada && i !in juego.minas)"×" else "⚑",Color.parseColor("#F7D683"),descripcion="Bandera")
                i in juego.abiertas -> {val n=juego.alrededor(i);CasillaArcade(if(n==0)"" else n.toString(),Color.parseColor("#D5EBE6"),Color.parseColor(pistas[n]),descripcion=if(n==0)"Segura" else "$n minas vecinas")}
                else -> CasillaArcade("",Color.parseColor("#4E8B8C"),descripcion="Sin descubrir")
            }
        },!juego.terminada)
        estadisticas(getString(R.string.arcade_mines_flags,juego.banderas.size),getString(R.string.arcade_mines_open,juego.abiertas.size))
        binding.status.setText(if(juego.victoria)R.string.arcade_mines_won else if(juego.terminada)R.string.arcade_mines_lost else R.string.arcade_mines_start)
    }
    override fun reiniciar() { modelo.juego.reiniciar();modelo.modoBandera=false;modelo.guardar(guardado);mostrar() }
    override fun onPause() { modelo.guardar(guardado);super.onPause() }
}
