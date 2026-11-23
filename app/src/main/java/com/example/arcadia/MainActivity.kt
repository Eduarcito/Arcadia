package com.example.arcadia

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.arcadia.compartido.ui.aplicarMargenesDelSistema
import com.example.arcadia.databinding.ActivityMainBinding
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.kenken.KenKenActivity

class MainActivity : AppCompatActivity() {

    private lateinit var bindingPrincipal: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindingPrincipal = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingPrincipal.root)
        bindingPrincipal.root.aplicarMargenesDelSistema()

        bindingPrincipal.btnJugar2048.setOnClickListener {
            startActivity(Intent(this, Juego2048Activity::class.java))
        }
        bindingPrincipal.btnJugarBuscaminas.setOnClickListener {
            startActivity(Intent(this, BuscaminasActivity::class.java))
        }
        bindingPrincipal.btnJugarKenken.setOnClickListener {
            startActivity(Intent(this, KenKenActivity::class.java))
        }
    }
}
