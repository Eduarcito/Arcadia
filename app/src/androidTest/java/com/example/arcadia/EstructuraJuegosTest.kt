package com.example.arcadia

import android.content.Context
import android.content.res.Configuration
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.arcadia.compartido.ui.EncabezadoJuegoView
import com.example.arcadia.compartido.ui.IndicadoresJuegoView
import com.example.arcadia.compartido.ui.PantallaPartidaView
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.kenken.KenKenActivity
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.startsWith
import org.hamcrest.Matchers.not
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EstructuraJuegosTest {
    private val contexto get() =
        InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun buscaminasUsaFilasUniformesYConservaElGuardadoAlReabrir() {
        val preferencias = contexto.getSharedPreferences("arcade_games", Context.MODE_PRIVATE)
        val anterior = preferencias.getString("mines", null)
        preferencias.edit().remove("mines").commit()
        try {
            ActivityScenario.launch(BuscaminasActivity::class.java).use { escenario ->
                onView(withContentDescription(startsWith("Fila 1, columna 1:")))
                    .perform(scrollTo())
                escenario.onActivity { actividad ->
                    val pantalla = actividad.findViewById<android.view.ViewGroup>(android.R.id.content)
                        .getChildAt(0) as PantallaPartidaView
                    assertTrue(pantalla.findViewById<EncabezadoJuegoView>(R.id.encabezado) != null)
                    val filas = pantalla.tablero.getChildAt(0) as LinearLayout
                    assertEquals(LinearLayout.VERTICAL, filas.orientation)
                    assertEquals(8, filas.childCount)
                    for (indice in 0 until filas.childCount) {
                        val fila = filas.getChildAt(indice) as LinearLayout
                        assertEquals(LinearLayout.HORIZONTAL, fila.orientation)
                        assertEquals(8, fila.childCount)
                        for (columna in 0 until fila.childCount) {
                            val celda = fila.getChildAt(columna)
                            assertTrue(celda.width > 0)
                            assertTrue(celda.height > 0)
                            assertTrue(kotlin.math.abs(celda.width - celda.height) <= 1)
                        }
                    }
                }
                onView(withContentDescription(startsWith("Fila 1, columna 1:")))
                    .perform(click())
            }
            val guardado = JSONObject(preferencias.getString("mines", null).orEmpty())
            assertTrue(guardado.getBoolean("started"))
            ActivityScenario.launch(BuscaminasActivity::class.java).use {
                onView(withContentDescription(startsWith("Fila 1, columna 1:")))
                    .perform(scrollTo())
                assertEquals(
                    guardado.toString(),
                    JSONObject(preferencias.getString("mines", null).orEmpty()).toString()
                )
            }
        } finally {
            preferencias.edit().apply {
                if (anterior == null) remove("mines") else putString("mines", anterior)
            }.commit()
        }
    }

    @Test
    fun kenkenComparteComponentesYConservaModosRotacionYReinicio() {
        ActivityScenario.launch(KenKenActivity::class.java).use { escenario ->
            escenario.onActivity { actividad ->
                assertTrue(actividad.findViewById<EncabezadoJuegoView>(R.id.encabezado) != null)
                assertTrue(actividad.findViewById<IndicadoresJuegoView>(R.id.indicadores) != null)
            }
            val primeraCasilla = withContentDescription(startsWith("Fila 1, columna 1:"))
            onView(primeraCasilla).perform(scrollTo(), click())
            onView(withId(R.id.numero2)).perform(scrollTo(), click())
            onView(primeraCasilla).check(
                matches(withContentDescription(containsString(": 2.")))
            )
            escenario.recreate()
            onView(primeraCasilla).check(
                matches(withContentDescription(containsString(": 2.")))
            )
            onView(withId(R.id.numero1)).perform(scrollTo(), click())
            onView(primeraCasilla).check(
                matches(withContentDescription(containsString(": 1.")))
            )
            onView(withId(R.id.borrar)).perform(scrollTo(), click())
            onView(primeraCasilla).check(
                matches(withContentDescription(containsString(": vacía.")))
            )
            onView(withId(R.id.comprobar)).perform(scrollTo(), click())
            onView(withId(R.id.estado)).check(
                matches(withText(R.string.kenken_resultado_incompleto))
            )
            onView(withId(R.id.restart)).perform(click())
            onView(withId(R.id.estado)).check(
                matches(withText(R.string.kenken_instruccion_teclado))
            )
        }
    }

    @Test
    fun kenkenSigueElTemaGlobalAlAbrirYAlVolverSinPerderLaPartida() {
        val preferencias = contexto.getSharedPreferences("arcade_settings", Context.MODE_PRIVATE)
        val teniaModo = preferencias.contains("dark")
        val anterior = preferencias.getBoolean("dark", true)
        preferencias.edit().putBoolean("dark", true).commit()
        try {
            ActivityScenario.launch(KenKenActivity::class.java).use { escenario ->
                escenario.onActivity { actividad ->
                    assertEquals(
                        Configuration.UI_MODE_NIGHT_YES,
                        actividad.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    )
                }
                val primeraCasilla = withContentDescription(startsWith("Fila 1, columna 1:"))
                onView(primeraCasilla).perform(scrollTo(), click())
                onView(withId(R.id.numero2)).perform(scrollTo(), click())
                escenario.moveToState(Lifecycle.State.CREATED)
                preferencias.edit().putBoolean("dark", false).commit()
                escenario.moveToState(Lifecycle.State.RESUMED)
                onView(primeraCasilla).perform(scrollTo()).check(
                    matches(withContentDescription(containsString(": 2.")))
                )
                escenario.onActivity { actividad ->
                    assertEquals(
                        Configuration.UI_MODE_NIGHT_NO,
                        actividad.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    )
                }
            }
            ActivityScenario.launch(KenKenActivity::class.java).use { escenario ->
                escenario.onActivity { actividad ->
                    assertEquals(
                        Configuration.UI_MODE_NIGHT_NO,
                        actividad.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    )
                }
            }
        } finally {
            preferencias.edit().apply {
                if (teniaModo) putBoolean("dark", anterior) else remove("dark")
            }.commit()
        }
    }
}
