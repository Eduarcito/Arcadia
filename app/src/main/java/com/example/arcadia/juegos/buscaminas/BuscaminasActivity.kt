package com.example.arcadia.juegos.buscaminas

import android.os.Bundle
import android.graphics.Color
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.CasillaArcade
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.compartido.ui.coloresJuego
import com.example.arcadia.databinding.ActivityBuscaminasBinding
import com.example.arcadia.databinding.HerramientasBuscaminasBinding

class BuscaminasActivity : PartidaArcadeActivity() {
    private val modelo: BuscaminasViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    override val partidaPerdida get() = modelo.juego.terminada && !modelo.juego.victoria
    private lateinit var controles: ControlesBuscaminasView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(ActivityBuscaminasBinding.inflate(layoutInflater).root);modelo.cargar(guardado)
        controles=HerramientasBuscaminasBinding.inflate(layoutInflater).root
        pantalla.usarPantallaCompleta(herramientas=controles)
        pantalla.tablero.estiloMinas=true
        controles.alCambiarModo { modelo.modoBandera=it;modelo.guardar(guardado) }
        controles.alPedirPista { if(modelo.pista()){respuesta();modelo.guardar(guardado);mostrar()} }
        pantalla.tablero.alTocar={ actuar(it,modelo.modoBandera) }
        pantalla.tablero.alMantener={ actuar(it,true) }
        mostrar();modelo.guardar(guardado)
    }
    private fun actuar(i:Int,bandera:Boolean) {
        val cambio = modelo.actuar(i, bandera)
        if(cambio){ respuesta();modelo.guardar(guardado);mostrar() }
    }
    private fun mostrar() {
        val juego=modelo.juego
        val pistas = resources.coloresJuego(R.array.pistas_minas_compacto)
        pantalla.tablero.mostrar(8,List(64){i->
            when {
                juego.terminada && i in juego.minas -> CasillaArcade("",getColor(if(i==juego.explosion) R.color.mina_explosion else R.color.mina_revelada),descripcion=getString(R.string.mina_descripcion),ilustracion=if(i in juego.banderas) R.drawable.ic_bandera else R.drawable.ic_mina)
                i in juego.banderas -> CasillaArcade(if(juego.terminada && i !in juego.minas)"×" else "",getColor(R.color.bandera_buscaminas),descripcion=getString(R.string.bandera_descripcion),ilustracion=if(juego.terminada && i !in juego.minas) 0 else R.drawable.ic_bandera)
                i in juego.abiertas -> {val n=juego.alrededor(i);CasillaArcade(if(n==0)"" else n.toString(),Color.TRANSPARENT,pistas[n],descripcion=if(n==0)getString(R.string.casilla_segura_descripcion) else getString(R.string.minas_vecinas_descripcion,n),plana=true)}
                else -> CasillaArcade("",Color.WHITE,descripcion=getString(R.string.casilla_cerrada_descripcion))
            }
        },!juego.terminada)
        estadisticas(getString(R.string.minas_racha,modelo.racha),getString(R.string.minas_record,modelo.recordRacha))
        controles.mostrar(juego.cantidadMinas-juego.banderas.size,modelo.modoBandera,!modelo.pistaUsada && !juego.terminada)
        pantalla.mostrarEstado(if(juego.victoria)R.string.arcade_mines_won else if(juego.terminada)R.string.arcade_mines_lost else R.string.arcade_mines_start)
        comprobarDerrota()
    }
    override fun reinicioPierdeProgreso() = !modelo.juego.terminada &&
        (modelo.juego.iniciada || modelo.juego.banderas.isNotEmpty())
    override fun reiniciar() { modelo.reiniciar();modelo.guardar(guardado);mostrar() }
    override fun onPause() { modelo.guardar(guardado);super.onPause() }
}
