package com.example.arcadia.compartido.ui

import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import com.example.arcadia.R
import com.example.arcadia.compartido.estado.CLAVE_MUSICA
import com.example.arcadia.compartido.estado.CLAVE_SONIDO
import com.example.arcadia.compartido.estado.CLAVE_VIBRACION
import com.example.arcadia.compartido.estado.CLAVE_VOLUMEN
import com.example.arcadia.compartido.estado.CLAVE_VOLUMEN_MUSICA
import com.example.arcadia.compartido.estado.VOLUMEN_INICIAL

// Comparte el tema global y agrega audio y vibracion a las pantallas que los utilizan
open class ArcadeActivity : TemaArcadeActivity() {
    private var tono: ToneGenerator? = null

    override fun onStart() {
        super.onStart()
        pantallasActivas++
    }

    override fun onResume() {
        super.onResume()
        if (!temaVigente) return
        actualizarAudio()
    }

    protected fun actualizarAudio() {
        tono?.release(); tono = null
        if (preferencias.getBoolean(CLAVE_SONIDO, true)) {
            tono = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, preferencias.getInt(CLAVE_VOLUMEN, VOLUMEN_INICIAL)) }.getOrNull()
        }
        if (preferencias.getBoolean(CLAVE_MUSICA, false)) {
            if (musica == null) musica = MediaPlayer.create(applicationContext, R.raw.arcade_loop)?.apply { isLooping = true }
            val volumenMusica = preferencias.getInt(CLAVE_VOLUMEN_MUSICA, preferencias.getInt(CLAVE_VOLUMEN, VOLUMEN_INICIAL))
            val volumen = volumenMusica / 100f * 0.25f
            musica?.setVolume(volumen, volumen)
            if (musica?.isPlaying != true) musica?.start()
        } else { musica?.release(); musica = null }
    }

    protected fun respuesta() {
        tono?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        if (preferencias.getBoolean(CLAVE_VIBRACION, true)) window.decorView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    override fun onPause() {
        tono?.release(); tono = null
        super.onPause()
    }

    override fun onStop() {
        pantallasActivas--
        if (pantallasActivas == 0) musica?.pause()
        super.onStop()
    }

    private companion object {
        // Un solo reproductor para toda la aplicacion: la melodia no reinicia al cambiar de pantalla
        var musica: MediaPlayer? = null
        var pantallasActivas = 0
    }
}

