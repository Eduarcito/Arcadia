package com.example.arcadia.ajustes

import android.os.Bundle
import com.example.arcadia.R
import com.example.arcadia.compartido.estado.CLAVE_MUSICA
import com.example.arcadia.compartido.estado.CLAVE_SONIDO
import com.example.arcadia.compartido.estado.CLAVE_VIBRACION
import com.example.arcadia.compartido.estado.CLAVE_VOLUMEN
import com.example.arcadia.compartido.estado.CLAVE_VOLUMEN_MUSICA
import com.example.arcadia.compartido.estado.CLAVE_TEMA
import com.example.arcadia.compartido.estado.TEMA_CLARO
import com.example.arcadia.compartido.estado.TEMA_OSCURO
import com.example.arcadia.compartido.estado.TEMA_SISTEMA
import com.example.arcadia.compartido.estado.VOLUMEN_INICIAL
import com.example.arcadia.compartido.estado.temaGuardado
import com.example.arcadia.compartido.ui.ArcadeActivity
import com.example.arcadia.databinding.ActivityArcadeSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider

class AjustesActivity : ArcadeActivity() {
    private fun mostrarVolumen(binding: com.example.arcadia.databinding.ActivityArcadeSettingsBinding) {
        binding.volumeValue.text = getString(R.string.arcade_volume_value, binding.volume.value.toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArcadeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        margenes(binding.root)
        androidx.core.view.ViewCompat.setAccessibilityHeading(binding.title, true)
        binding.back.setOnClickListener { finish() }

        binding.volume.value = preferencias.getInt(CLAVE_VOLUMEN, VOLUMEN_INICIAL).toFloat()
        mostrarVolumen(binding)
        binding.volume.addOnChangeListener { _, valor, usuario ->
            if (usuario) {
                preferencias.edit().putInt(CLAVE_VOLUMEN, valor.toInt()).apply()
                mostrarVolumen(binding)
            }
        }
        binding.volume.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                actualizarAudio()
                respuesta()
            }
        })

        val volumenMusicaInicial = preferencias.getInt(CLAVE_VOLUMEN_MUSICA, preferencias.getInt(CLAVE_VOLUMEN, VOLUMEN_INICIAL))
        binding.volumeMusic.value = volumenMusicaInicial.toFloat()
        binding.volumeMusicValue.text = getString(R.string.arcade_volume_value, volumenMusicaInicial)
        binding.volumeMusic.addOnChangeListener { _, valor, usuario ->
            if (usuario) {
                preferencias.edit().putInt(CLAVE_VOLUMEN_MUSICA, valor.toInt()).apply()
                binding.volumeMusicValue.text = getString(R.string.arcade_volume_value, valor.toInt())
            }
        }
        binding.volumeMusic.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                actualizarAudio()
            }
        })

        val interruptores = listOf(
            Triple(binding.sound, CLAVE_SONIDO, true),
            Triple(binding.music, CLAVE_MUSICA, false),
            Triple(binding.vibration, CLAVE_VIBRACION, true)
        )
        for ((interruptor, clave, inicial) in interruptores) {
            interruptor.isChecked = preferencias.getBoolean(clave, inicial)
            interruptor.setOnCheckedChangeListener { _, valor ->
                preferencias.edit().putBoolean(clave, valor).apply()
                actualizarAudio()
            }
        }

        val botonesTema = mapOf(
            binding.themeSystem.id to TEMA_SISTEMA,
            binding.themeLight.id to TEMA_CLARO,
            binding.themeDark.id to TEMA_OSCURO
        )
        binding.theme.check(
            botonesTema.entries.first { it.value == temaGuardado(preferencias) }.key
        )
        binding.theme.addOnButtonCheckedListener { _, boton, marcado ->
            if (marcado) {
                preferencias.edit().putInt(CLAVE_TEMA, botonesTema.getValue(boton)).apply()
                recreate()
            }
        }

        binding.reset.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.arcade_settings_reset_title)
                .setMessage(R.string.arcade_settings_reset_message)
                .setNegativeButton(R.string.arcade_cancel, null)
                .setPositiveButton(R.string.arcade_settings_reset) { _, _ ->
                    preferencias.edit().clear().apply()
                    recreate()
                }
                .show()
        }
    }
}
