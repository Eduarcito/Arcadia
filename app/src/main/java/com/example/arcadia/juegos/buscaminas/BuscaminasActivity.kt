package com.example.arcadia.juegos.buscaminas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.arcadia.compartido.ui.aplicarMargenesDelSistema
import com.example.arcadia.databinding.ActivityBuscaminasBinding

class BuscaminasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityBuscaminasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarMargenesDelSistema()
        binding.btnVolver.setOnClickListener { finish() }
    }
}
