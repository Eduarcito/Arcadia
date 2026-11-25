package com.example.arcadia.arcade

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.ForegroundColorSpan
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import com.example.arcadia.R
import com.example.arcadia.databinding.ActivityArcadeGameBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** New screens use a local theme; KenKen and its shared components are untouched. */
open class ArcadeActivity : AppCompatActivity() {
    protected val preferencias by lazy { getSharedPreferences("arcade_settings", Context.MODE_PRIVATE) }
    private var modoOscuro = true
    private var musica: MediaPlayer? = null
    private var tono: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Arcade)
        modoOscuro = preferencias.getBoolean("dark", true)
        delegate.localNightMode = if (modoOscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    protected fun margenes(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (modoOscuro != preferencias.getBoolean("dark", true)) { recreate(); return }
        actualizarBarras()
        actualizarAudio()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) actualizarBarras()
    }

    private fun actualizarBarras() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !modoOscuro
            isAppearanceLightNavigationBars = !modoOscuro
        }
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

    protected fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

abstract class PartidaArcadeActivity : ArcadeActivity() {
    protected lateinit var binding: ActivityArcadeGameBinding
    protected fun preparar(titulo: Int, instrucciones: Int) {
        binding = ActivityArcadeGameBinding.inflate(layoutInflater)
        setContentView(binding.root); margenes(binding.root)
        binding.title.setText(titulo); binding.instructions.setText(instrucciones)
        ViewCompat.setAccessibilityHeading(binding.title, true)
        val (acento, etiqueta, gesto) = when (titulo) {
            R.string.juego_2048 -> Triple("#EDBB63", R.string.arcade_2048_caption, R.string.arcade_gesture_swipe)
            R.string.juego_buscaminas -> Triple("#66D2C6", R.string.arcade_mines_caption, R.string.arcade_gesture_mines)
            R.string.arcade_pet_title -> Triple("#EE94BD", R.string.arcade_pet_caption, R.string.arcade_gesture_pet)
            else -> Triple("#B4EB7C", R.string.arcade_snake_caption, R.string.arcade_gesture_snake)
        }
        val oscuro = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val color = Color.parseColor(acento)
        val tinta = if (oscuro) color else androidx.core.graphics.ColorUtils.blendARGB(color, Color.BLACK, .48f)
        binding.heroCaption.setText(etiqueta); binding.heroCaption.setTextColor(tinta)
        binding.gestureHint.setText(gesto)
        binding.statLeft.setTextColor(tinta)
        listOf(binding.statLeft, binding.statRight, binding.instructionCard).forEach { view ->
            view.background = GradientDrawable().apply {
                cornerRadius = dp(20).toFloat()
                setColor(getColor(R.color.arcade_surface))
                setStroke(dp(1), androidx.core.graphics.ColorUtils.setAlphaComponent(color, 42))
            }
        }
        binding.back.setOnClickListener { finish() }
        binding.restart.setOnClickListener { confirmar { reiniciar() } }
    }
    protected abstract fun reiniciar()
    protected fun confirmar(accion: () -> Unit) {
        MaterialAlertDialogBuilder(this).setTitle(R.string.arcade_restart_title)
            .setMessage(R.string.arcade_restart_message).setNegativeButton(R.string.arcade_cancel, null)
            .setPositiveButton(R.string.arcade_restart) { _, _ -> accion() }.show()
    }
    protected fun boton(texto: Int, accion: () -> Unit): MaterialButton {
        return MaterialButton(this).apply {
            setText(texto); setOnClickListener { accion() }
            binding.controls.addView(this, LinearLayout.LayoutParams(-1, -2))
        }
    }
    protected fun estadisticas(izquierda: String, derecha: String) {
        fun formato(texto: String): SpannableString {
            return SpannableString(texto).apply {
                val fin = texto.indexOf('\n')
                if (fin > 0) {
                    setSpan(RelativeSizeSpan(.43f), 0, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(ForegroundColorSpan(getColor(R.color.arcade_muted)), 0, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        binding.statLeft.text = formato(izquierda)
        binding.statRight.text = formato(derecha)
    }
}
