package com.example.arcadia.juegos.numberconnect

import android.os.Bundle
import androidx.activity.viewModels
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.PartidaArcadeActivity
import com.example.arcadia.databinding.ActivityNumberConnectBinding
import com.example.arcadia.databinding.TableroNumberConnectBinding
import com.example.arcadia.databinding.PrevisualizacionConnectBinding

class NumberConnectActivity : PartidaArcadeActivity() {
    private val modelo: NumberConnectViewModel by viewModels()
    private val guardado by lazy { getSharedPreferences("arcade_games",MODE_PRIVATE) }
    private lateinit var tablero: TableroNumberConnectView
    private lateinit var previsualizacion: PrevisualizacionConnectView
    override val partidaPerdida get()=modelo.juego.terminado
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        preparar(ActivityNumberConnectBinding.inflate(layoutInflater).root);modelo.cargar(guardado)
        tablero=TableroNumberConnectBinding.inflate(layoutInflater).root
        previsualizacion=PrevisualizacionConnectBinding.inflate(layoutInflater).root
        pantalla.usarPantallaCompleta(tablero, previsualizacion=previsualizacion)
        tablero.puedeAgregar=modelo::puedeAgregar
        tablero.alCombinar={ camino -> if(modelo.combinar(camino)){respuesta();modelo.guardar(guardado);mostrar()} }
        tablero.alSeleccionar={ camino -> previsualizacion.mostrar(modelo.previsualizar(camino)) }
        mostrar();modelo.guardar(guardado)
    }
    private fun mostrar(){
        previsualizacion.mostrar(null)
        tablero.mostrar(modelo.juego.celdas,!partidaPerdida)
        estadisticas(getString(R.string.connect_score,modelo.juego.puntos),getString(R.string.connect_best,modelo.record))
        pantalla.mostrarEstado(if(partidaPerdida)R.string.connect_over else R.string.connect_hint)
        comprobarDerrota()
    }
    override fun reiniciar(){modelo.reiniciar();modelo.guardar(guardado);mostrar()}
    override fun onPause(){tablero.cancelarSeleccion();modelo.guardar(guardado);super.onPause()}
}
