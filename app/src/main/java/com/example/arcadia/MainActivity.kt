package com.example.arcadia

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.ajustes.AjustesActivity
import com.example.arcadia.databinding.ActivityMainBinding
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.kenken.KenKenActivity
import com.example.arcadia.juegos.petdoku.PetdokuActivity
import com.example.arcadia.juegos.snake.SnakeActivity

class MainActivity : ArcadeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root); margenes(binding.root)
        androidx.core.view.ViewCompat.setAccessibilityHeading(binding.brand, true)
        binding.settings.setOnClickListener { binding.drawer.openDrawer(GravityCompat.START) }
        binding.menuGames.setOnClickListener { binding.drawer.closeDrawer(GravityCompat.START) }
        binding.menuSettings.setOnClickListener {
            binding.drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this,AjustesActivity::class.java))
        }
        val cerrarMenu = onBackPressedDispatcher.addCallback(this, false) { binding.drawer.closeDrawer(GravityCompat.START) }
        binding.drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: android.view.View) { cerrarMenu.isEnabled = true }
            override fun onDrawerClosed(drawerView: android.view.View) { cerrarMenu.isEnabled = false }
        })
        val juegos = listOf(
            binding.tarjeta2048 to Juego2048Activity::class.java,
            binding.tarjetaBuscaminas to BuscaminasActivity::class.java,
            binding.tarjetaPetdoku to PetdokuActivity::class.java,
            binding.tarjetaSnake to SnakeActivity::class.java,
            binding.tarjetaKenken to KenKenActivity::class.java
        )
        for ((tarjeta, destino) in juegos) {
            tarjeta.alJugar {
                respuesta()
                startActivity(Intent(this, destino))
            }
        }
    }
}
