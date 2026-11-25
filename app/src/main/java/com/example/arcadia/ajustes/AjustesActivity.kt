package com.example.arcadia.ajustes

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.arcadia.R
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.databinding.ActivityArcadeSettingsBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider

class AjustesActivity : ArcadeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArcadeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root); margenes(binding.root)
        binding.back.setOnClickListener { finish() }
        val volumen = TextView(this).apply { setText(R.string.arcade_volume); textSize = 21f; setTextColor(getColor(R.color.arcade_ink)) }
        binding.options.addView(volumen)
        binding.options.addView(Slider(this).apply {
            valueFrom = 0f; valueTo = 100f; stepSize = 1f; value = preferencias.getInt("volume",45).toFloat()
            isTickVisible = false; thumbRadius = dp(14); trackHeight = dp(8)
            contentDescription = getString(R.string.arcade_volume)
            addOnChangeListener { _, valor, usuario -> if(usuario) preferencias.edit().putInt("volume",valor.toInt()).apply() }
            addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) { actualizarAudio(); respuesta() }
            })
        }, LinearLayout.LayoutParams(-1,dp(64)))
        listOf(Triple("sound",R.string.arcade_sound,true),Triple("music",R.string.arcade_music,false),
            Triple("vibration",R.string.arcade_vibration,true),Triple("dark",R.string.arcade_dark,true)).forEach { (clave,titulo,inicial) ->
            binding.options.addView(MaterialSwitch(this).apply {
                setText(titulo); textSize = 23f; setTextColor(getColor(R.color.arcade_ink)); minHeight = dp(76)
                isChecked = preferencias.getBoolean(clave,inicial)
                setOnCheckedChangeListener { _, valor ->
                    preferencias.edit().putBoolean(clave,valor).apply()
                    if(clave=="dark") recreate() else actualizarAudio()
                }
            },LinearLayout.LayoutParams(-1,-2))
        }
        binding.options.addView(TextView(this).apply {
            setText(R.string.arcade_settings_note); textSize = 14f; setTextColor(getColor(R.color.arcade_muted)); setPadding(0,dp(24),0,dp(24))
        })
        binding.options.addView(MaterialButton(this).apply {
            setText(R.string.arcade_settings_reset)
            setOnClickListener { preferencias.edit().clear().apply(); recreate() }
        },LinearLayout.LayoutParams(-1,-2))
    }
}
