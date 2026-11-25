package com.example.arcadia

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.arcadia.juegos.petdoku.PetdokuActivity
import org.hamcrest.Matchers.startsWith
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PetdokuFlujoTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val saves get() = context.getSharedPreferences("arcade_games", Context.MODE_PRIVATE)
    private val settings get() = context.getSharedPreferences("arcade_settings", Context.MODE_PRIVATE)
    private var partidaAnterior: String? = null
    private var sonidoAnterior: Boolean? = null
    private val solucion = setOf(4, 8, 17, 19, 27, 30)

    @Before fun preparar() {
        partidaAnterior = saves.getString("petdoku", null)
        sonidoAnterior = if (settings.contains("sound")) settings.getBoolean("sound", true) else null
        settings.edit().putBoolean("sound", false).commit()
    }
    @After fun restaurar() {
        saves.edit().apply { if (partidaAnterior == null) remove("petdoku") else putString("petdoku", partidaAnterior) }.commit()
        settings.edit().apply { if (sonidoAnterior == null) remove("sound") else putBoolean("sound", sonidoAnterior!!) }.commit()
    }
    private fun guardar(marcas: List<Int>, vidas: Int = 3) {
        saves.edit().putString("petdoku", JSONObject().put("level", 0)
            .put("marks", JSONArray(marcas)).put("lives", vidas).toString()).commit()
    }
    private fun estado() = JSONObject(saves.getString("petdoku", "{}")!!)
    private fun esperarAvance() {
        Thread.sleep(1300)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun comprobarIlustraciones(scenario: ActivityScenario<PetdokuActivity>, recurso: Int) {
        scenario.onActivity { actividad ->
            val ilustradas=mutableListOf<com.example.arcadia.compartido.ui.CasillaIlustradaView>()
            fun visitar(vista: android.view.View) {
                if(vista is com.example.arcadia.compartido.ui.CasillaIlustradaView && vista.recursoIlustracion!=0) ilustradas.add(vista)
                if(vista is android.view.ViewGroup) for(i in 0 until vista.childCount) visitar(vista.getChildAt(i))
            }
            visitar(actividad.window.decorView)
            assertEquals(4,ilustradas.size)
            assertTrue(ilustradas.all { it.recursoIlustracion==recurso && it.text.isEmpty() })
        }
    }

    @Test fun mascotasAlternanEntreNivelesYSeConservanAlRecrear() {
        val mascotas=com.example.arcadia.juegos.petdoku.MascotasPetdoku.todas
        assertEquals(7,mascotas.size)
        assertEquals(mascotas.size,mascotas.map { it.imagen }.distinct().size)
        com.example.arcadia.juegos.petdoku.NIVELES_PETDOKU.indices.forEach { nivel ->
            val mascota=com.example.arcadia.juegos.petdoku.MascotasPetdoku.paraNivel(nivel)
            if(nivel>0) assertNotEquals(com.example.arcadia.juegos.petdoku.MascotasPetdoku.paraNivel(nivel-1).imagen,mascota.imagen)
            val reglas=com.example.arcadia.juegos.petdoku.ReglasPetdoku()
            reglas.restaurar(nivel,List(36){0})
            for(i in 0..35) {
                reglas.reiniciar();reglas.descubrir(i)
                if(reglas.marcas[i]==1) break
            }
            saves.edit().putString("petdoku",JSONObject().put("level",nivel)
                .put("marks",JSONArray(reglas.marcas)).put("lives",3).toString()).commit()
            ActivityScenario.launch(PetdokuActivity::class.java).use { scenario ->
                comprobarIlustraciones(scenario,mascota.imagen)
                scenario.recreate()
                comprobarIlustraciones(scenario,mascota.imagen)
                assertEquals(nivel,estado().getInt("level"))
            }
        }
    }

    @Test fun ultimoAciertoAvanzaUnaVezYTableroCabeCompleto() {
        guardar(List(36) { if (it in solucion && it != 30) 1 else 0 })
        ActivityScenario.launch(PetdokuActivity::class.java).use { scenario ->
            onView(withId(R.id.board)).check(matches(isCompletelyDisplayed()))
            onView(withContentDescription(startsWith("Fila 1, columna 5:"))).check { vista, error ->
                if(error!=null) throw error
                val celda=vista as com.example.arcadia.compartido.ui.CasillaIlustradaView
                assertEquals(R.drawable.petdoku_perrito,celda.recursoIlustracion)
                assertEquals("",celda.text.toString())
            }
            val captura=InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            java.io.File(context.getExternalFilesDir(null),"petdoku_perrito.png").outputStream().use {
                captura.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)
            }
            captura.recycle()

            onView(withContentDescription(startsWith("Fila 6, columna 1:"))).perform(doubleClick())
            esperarAvance()
            assertEquals(1, estado().getInt("level"))
            scenario.onActivity { actividad ->
                val contenedor=actividad.findViewById<android.view.ViewGroup>(R.id.reglas_visuales)
                fun comprobar(vista:android.view.View) {
                    if(vista is com.example.arcadia.compartido.ui.CasillaIlustradaView && vista.recursoIlustracion!=0)
                        assertEquals(R.drawable.petdoku_gatito,vista.recursoIlustracion)
                    if(vista is android.view.ViewGroup) for(i in 0 until vista.childCount) comprobar(vista.getChildAt(i))
                }
                comprobar(contenedor)
            }

            assertEquals(3, estado().getInt("lives"))
            assertTrue((0..35).all { estado().getJSONArray("marks").getInt(it) == 0 })
            scenario.recreate(); esperarAvance()
            assertEquals(1, estado().getInt("level"))
        }
    }

    @Test fun victoriaGuardadaEsperaAlVolverYAvanza() {
        guardar(List(36) { if (it in solucion) 1 else 0 })
        ActivityScenario.launch(PetdokuActivity::class.java).use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
            esperarAvance(); assertEquals(0, estado().getInt("level"))
            scenario.moveToState(Lifecycle.State.RESUMED)
            esperarAvance(); assertEquals(1, estado().getInt("level"))
        }
    }

    @Test fun ultimoErrorAbreModalYReintentaElMismoNivel() {
        guardar(List(36) { if (it in 0..1) 3 else 0 }, 1)
        ActivityScenario.launch(PetdokuActivity::class.java).use { scenario ->
            onView(withContentDescription(startsWith("Fila 1, columna 3:"))).perform(doubleClick())
            onView(withText(R.string.petdoku_sin_vidas)).inRoot(isDialog()).check(matches(isDisplayed()))
            assertEquals(0, estado().getInt("lives"))
            scenario.recreate()
            onView(withText(R.string.petdoku_reintentar)).inRoot(isDialog()).perform(click())
            assertEquals(0, estado().getInt("level")); assertEquals(3, estado().getInt("lives"))
            onView(withId(R.id.board)).check(matches(isCompletelyDisplayed()))
        }
    }

    @Test fun derrotaGuardadaPermiteVolverAlCatalogo() {
        guardar(List(36) { if (it in 0..2) 3 else 0 }, 0)
        ActivityScenario.launch(PetdokuActivity::class.java).use {
            onView(withText(R.string.petdoku_menu_principal)).inRoot(isDialog()).perform(click())
            onView(withContentDescription("Jugar a PETDOKU")).check(matches(isDisplayed()))
            assertEquals(0, estado().getInt("lives"))
        }
    }
}
