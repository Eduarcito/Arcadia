package com.example.arcadia

import android.content.Intent
import android.os.Bundle
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.ajustes.AjustesActivity
import com.example.arcadia.databinding.ActivityMainBinding
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.kenken.KenKenActivity
import com.example.arcadia.juegos.petdoku.PetdokuActivity
import com.example.arcadia.juegos.snake.SnakeActivity
import com.example.arcadia.juegos.numberconnect.NumberConnectActivity

class MainActivity : ArcadeActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root); margenes(binding.root)
        androidx.core.view.ViewCompat.setAccessibilityHeading(binding.brand, true)
        binding.settings.setOnClickListener {
            startActivity(Intent(this,AjustesActivity::class.java))
        }
        val juegos = listOf(
            binding.tarjeta2048 to Juego2048Activity::class.java,
            binding.tarjetaBuscaminas to BuscaminasActivity::class.java,
            binding.tarjetaPetdoku to PetdokuActivity::class.java,
            binding.tarjetaSnake to SnakeActivity::class.java,
            binding.tarjetaKenken to KenKenActivity::class.java,
            binding.tarjetaConnect to NumberConnectActivity::class.java
        )
        for ((tarjeta, destino) in juegos) {
            tarjeta.alJugar {
                respuesta()
                startActivity(Intent(this, destino))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val partidas = getSharedPreferences("arcade_games", MODE_PRIVATE)
        fun json(clave: String) = partidas.getString(clave, null)?.let {
            runCatching { org.json.JSONObject(it) }.getOrNull()
        }
        json("2048")?.optLong("best", 0)?.takeIf { it > 0 }
            ?.let { binding.tarjeta2048.mostrarEstado(getString(R.string.catalogo_record, it)) }
        json("snake")?.optInt("best", 0)?.takeIf { it > 0 }
            ?.let { binding.tarjetaSnake.mostrarEstado(getString(R.string.catalogo_record, it)) }
        json("number_connect")?.optLong("best", 0)?.takeIf { it > 0 }
            ?.let { binding.tarjetaConnect.mostrarEstado(getString(R.string.catalogo_record, it)) }
        json("petdoku")?.optInt("level", -1)?.takeIf { it >= 0 }
            ?.let { binding.tarjetaPetdoku.mostrarEstado(getString(R.string.catalogo_nivel, it + 1)) }
        json("mines")?.optInt("bestStreak", 0)?.takeIf { it > 0 }
            ?.let { binding.tarjetaBuscaminas.mostrarEstado(getString(R.string.catalogo_racha, it)) }
    }
}
