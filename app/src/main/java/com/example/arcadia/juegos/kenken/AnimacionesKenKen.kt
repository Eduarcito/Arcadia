package com.example.arcadia.juegos.kenken

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

internal fun animarRespuestaKenKen(
    vista: View,
    escalaInicial: Float = 0.96f,
    duracion: Long = 160L
) {
    vista.animate().cancel()
    vista.alpha = 1f
    vista.scaleX = 1f
    vista.scaleY = 1f
    if (!ValueAnimator.areAnimatorsEnabled() || !vista.isAttachedToWindow) {
        return
    }

    vista.alpha = 0.65f
    vista.scaleX = escalaInicial
    vista.scaleY = escalaInicial
    vista.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setStartDelay(0L)
        .setDuration(duracion)
        .setInterpolator(DecelerateInterpolator())
        .start()
}
