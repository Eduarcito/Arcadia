package com.example.arcadia.compartido.ui

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.arcadia.MainActivity
import com.example.arcadia.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class PartidaArcadeActivity : ArcadeActivity() {
    protected lateinit var pantalla: PantallaPartidaView
    protected open val partidaPerdida: Boolean get() = false
    private var activa = false
    private var dialogoDerrota: AlertDialog? = null

    protected fun preparar(vista: PantallaPartidaView) {
        pantalla = vista
        setContentView(pantalla)
        margenes(pantalla)
        pantalla.alVolver { finish() }
        pantalla.alReiniciar { confirmar { reiniciar() } }
    }

    protected abstract fun reiniciar()

    protected fun comprobarDerrota() {
        if (!activa || !partidaPerdida || dialogoDerrota != null || isFinishing) return
        pantalla.tablero.cancelarToquesPendientes()
        dialogoDerrota = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.arcade_defeat_title)
            .setMessage(R.string.arcade_defeat_message)
            .setCancelable(false)
            .setPositiveButton(R.string.arcade_retry) { _, _ -> reiniciar() }
            .setNegativeButton(R.string.arcade_main_menu) { _, _ ->
                startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                finish()
            }
            .create().also { dialogo ->
                dialogo.setOnDismissListener { if (dialogoDerrota === dialogo) dialogoDerrota = null }
                dialogo.show()
            }
    }

    override fun onResume() { super.onResume(); activa = true; comprobarDerrota() }

    override fun onPause() {
        activa = false
        dialogoDerrota?.dismiss(); dialogoDerrota = null
        super.onPause()
    }

    protected fun confirmar(accion: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.arcade_restart_title)
            .setMessage(R.string.arcade_restart_message)
            .setNegativeButton(R.string.arcade_cancel, null)
            .setPositiveButton(R.string.arcade_restart) { _, _ -> accion() }
            .show()
    }

    protected fun estadisticas(izquierda: String, derecha: String) {
        pantalla.mostrarEstadisticas(izquierda, derecha)
    }
}
