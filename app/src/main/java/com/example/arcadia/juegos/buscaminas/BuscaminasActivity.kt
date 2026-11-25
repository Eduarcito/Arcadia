package com.example.arcadia.juegos.buscaminas

import android.os.Bundle
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.CasillaArcade
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.ui.coloresJuego
import com.example.arcadia.databinding.ActivityBuscaminasBinding

class BuscaminasActivity : PartidaArcadeActivity() {
    private val modelo: BuscaminasViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivityBuscaminasBinding.inflate(layoutInflater).root);modelo.cargar(guardado)
        pantalla.tablero.alTocar={ actuar(it,false) }
        pantalla.tablero.alMantener={ actuar(it,true) }
        mostrar();modelo.guardar(guardado)
    }
    private fun actuar(i:Int,bandera:Boolean) {
        val cambio = modelo.actuar(i, bandera)
        if(cambio){ respuesta();modelo.guardar(guardado);mostrar() }
    }
    private fun mostrar() {
        val juego=modelo.juego
        val pistas = resources.coloresJuego(R.array.pistas_buscaminas)
        pantalla.tablero.mostrar(8,List(64){i->
            when {
                juego.terminada && i in juego.minas -> CasillaArcade(if(i in juego.banderas)"⚑" else "✹",getColor(if(i==juego.explosion) R.color.mina_explosion else R.color.mina_revelada),descripcion=getString(R.string.mina_descripcion))
                i in juego.banderas -> CasillaArcade(if(juego.terminada && i !in juego.minas)"×" else "⚑",getColor(R.color.bandera_buscaminas),descripcion=getString(R.string.bandera_descripcion))
                i in juego.abiertas -> {val n=juego.alrededor(i);CasillaArcade(if(n==0)"" else n.toString(),getColor(R.color.casilla_abierta_buscaminas),pistas[n],descripcion=if(n==0)getString(R.string.casilla_segura_descripcion) else getString(R.string.minas_vecinas_descripcion,n))}
                else -> CasillaArcade("",getColor(R.color.casilla_cerrada_buscaminas),descripcion=getString(R.string.casilla_cerrada_descripcion))
            }
        },!juego.terminada)
        estadisticas(getString(R.string.arcade_mines_flags,juego.banderas.size),getString(R.string.arcade_mines_open,juego.abiertas.size))
        pantalla.mostrarEstado(if(juego.victoria)R.string.arcade_mines_won else if(juego.terminada)R.string.arcade_mines_lost else R.string.arcade_mines_start)
    }
    override fun reiniciar() { modelo.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() { modelo.guardar(guardado);super.onPause() }
}
