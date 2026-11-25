package com.example.arcadia

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.arcadia.arcade.ArcadeActivity
import com.example.arcadia.arcade.AjustesActivity
import com.example.arcadia.databinding.ActivityMainBinding
import com.example.arcadia.databinding.TarjetaArcadeBinding
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.kenken.KenKenActivity
import com.example.arcadia.juegos.petdoku.PetdokuActivity
import com.example.arcadia.juegos.snake.SnakeActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : ArcadeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root); margenes(binding.root)
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
        val nombres = listOf("2048", "BUSCA\nMINAS", "PETDOKU", "SNAKE", "KENKEN")
        val fondos = listOf("#3B293D", "#163E41", "#FFF0DB", "#182C2B", "#30273F")
        val tintas = listOf("#FFD270", "#9EEAE1", "#B02D6C", "#B5EC72", "#DAB9FF")
        val destinos = listOf(Juego2048Activity::class.java,BuscaminasActivity::class.java,PetdokuActivity::class.java,SnakeActivity::class.java,KenKenActivity::class.java)
        var fila: LinearLayout? = null
        nombres.forEachIndexed { i, nombre ->
            if(i%2==0) {
                fila = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; isBaselineAligned=false }
                binding.catalogRows.addView(fila,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(12) })
            }
            val contenido = TarjetaArcadeBinding.inflate(layoutInflater)
            contenido.title.text=nombre; contenido.title.setTextColor(Color.parseColor(tintas[i]))
            contenido.accent.setBackgroundColor(Color.parseColor(tintas[i]));contenido.play.setTextColor(Color.parseColor(tintas[i]));contenido.art.juego=i
            val tarjeta = MaterialCardView(this).apply {
                radius=dp(24).toFloat();strokeWidth=dp(3);strokeColor=Color.parseColor("#343237");cardElevation=0f
                setCardBackgroundColor(Color.parseColor(fondos[i]));rippleColor=ColorStateList.valueOf(0x338E529F)
                contentDescription=getString(R.string.catalogo_jugar_a,nombre.replace("\n",""))
                isClickable=true;isFocusable=true
                addView(contenido.root)
                setOnClickListener { respuesta();startActivity(Intent(this@MainActivity,destinos[i])) }
            }
            contenido.root.importantForAccessibility=android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            fila!!.addView(tarjeta,LinearLayout.LayoutParams(0,-2,1f).apply { if(i%2==0) marginEnd=dp(6) else marginStart=dp(6) })
            if(i==nombres.lastIndex && i%2==0) fila!!.addView(android.view.View(this),LinearLayout.LayoutParams(0,1,1f).apply{marginStart=dp(6)})
        }
    }
}
