package com.example.arcadia.compartido.ui

import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import com.example.arcadia.R

// Comparte el tema global y agrega audio y vibracion a las pantallas que los utilizan
open class ArcadeActivity : TemaArcadeActivity() {
    private var musica: MediaPlayer? = null
    private var tono: ToneGenerator? = null

    override fun onResume() {
        super.onResume()
        if (!temaVigente) return
        actualizarAudio()
    }

    protected fun actualizarAudio() {
        tono?.release(); tono = null
        if (preferencias.getBoolean("sound", true)) {
            tono = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, preferencias.getInt("volume", 45)) }.getOrNull()
        }
        if (preferencias.getBoolean("music", false)) {
            if (musica == null) musica = MediaPlayer.create(this, R.raw.arcade_loop)?.apply { isLooping = true }
            val volumen = preferencias.getInt("volume", 45) / 100f * 0.25f
            musica?.setVolume(volumen, volumen)
            musica?.start()
        } else { musica?.release(); musica = null }
    }

    protected fun respuesta() {
        tono?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        if (preferencias.getBoolean("vibration", true)) window.decorView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    override fun onPause() {
        musica?.release(); musica = null; tono?.release(); tono = null
        super.onPause()
    }

}

