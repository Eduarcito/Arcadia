package com.example.arcadia.juegos.kenken

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.arcadia.compartido.ui.aplicarMargenesDelSistema
import com.example.arcadia.databinding.ActivityKenkenBinding

class KenKenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityKenkenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarMargenesDelSistema()
        binding.encabezado.alVolver { finish() }
    }
}
