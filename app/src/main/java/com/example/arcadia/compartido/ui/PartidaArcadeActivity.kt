package com.example.arcadia.compartido.ui

import com.example.arcadia.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class PartidaArcadeActivity : ArcadeActivity() {
    protected lateinit var pantalla: PantallaPartidaView

    protected fun preparar(vista: PantallaPartidaView) {
        pantalla = vista
        setContentView(pantalla)
        margenes(pantalla)
        pantalla.alVolver { finish() }
        pantalla.alReiniciar { confirmar { reiniciar() } }
    }

    protected abstract fun reiniciar()

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
