package com.example.arcadia.juegos.juego2048

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.arcadia.compartido.ui.aplicarMargenesDelSistema
import com.example.arcadia.databinding.ActivityJuego2048Binding

class Juego2048Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityJuego2048Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarMargenesDelSistema()
        binding.btnVolver.setOnClickListener { finish() }
    }
}
