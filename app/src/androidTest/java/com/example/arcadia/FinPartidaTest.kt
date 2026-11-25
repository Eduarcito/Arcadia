package com.example.arcadia

import android.app.Activity
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.snake.SnakeActivity
import org.hamcrest.Matchers.startsWith
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FinPartidaTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val saves get() = context.getSharedPreferences("arcade_games", Context.MODE_PRIVATE)
    private val settings get() = context.getSharedPreferences("arcade_settings", Context.MODE_PRIVATE)
    private var anteriores = emptyMap<String, String?>()
    private var sonidoAnterior: Boolean? = null
    private val lleno = listOf(8,16,8,16,16,8,16,8,8,16,8,16,16,8,16,8)

    @Before fun preparar() {
        anteriores = listOf("2048", "mines", "snake").associateWith { saves.getString(it, null) }
        sonidoAnterior = if (settings.contains("sound")) settings.getBoolean("sound", true) else null
        settings.edit().putBoolean("sound", false).commit()
    }
    @After fun restaurar() {
        saves.edit().apply { anteriores.forEach { (clave, valor) -> if (valor == null) remove(clave) else putString(clave, valor) } }.commit()
        settings.edit().apply { if (sonidoAnterior == null) remove("sound") else putBoolean("sound", sonidoAnterior!!) }.commit()
    }
    private fun guardar(clave: String, json: JSONObject) { saves.edit().putString(clave, json.toString()).commit() }
    private fun estado(clave: String) = JSONObject(saves.getString(clave, "{}")!!)
    private fun juego2048(celdas: List<Int> = lleno) = JSONObject().put("cells", JSONArray(celdas)).put("score", 0).put("best", 64)
    private fun minas(golpe: Int = -1, abiertas: List<Int> = listOf(63)) = JSONObject()
        .put("mines", JSONArray((0..9).toList())).put("open", JSONArray(abiertas)).put("flags", JSONArray())
        .put("started", true).put("hit", golpe)
    private fun snake(fin: Boolean) = JSONObject().put("body", JSONArray(listOf(13,12,11)))
        .put("direction", 1).put("food", 30).put("score", 0).put("best", 80).put("over", fin).put("started", true)
    private fun comprobarModal() { onView(withText(R.string.arcade_defeat_title)).inRoot(isDialog()).check(matches(isDisplayed())) }
    private fun reintentar() { onView(withText(R.string.arcade_retry)).inRoot(isDialog()).perform(click()) }
    private fun <T : Activity> volverAlMenu(tipo: Class<T>) {
        ActivityScenario.launch(tipo).use {
            comprobarModal()
            onView(withText(R.string.arcade_main_menu)).inRoot(isDialog()).perform(click())
            onView(withContentDescription("Jugar a 2048")).check(matches(isDisplayed()))
        }
    }

    @Test fun ultimoMovimiento2048AbreModalYReintentaConRecord() {
        guardar("2048", juego2048(lleno.take(12) + listOf(8,16,8,0)))
        ActivityScenario.launch(Juego2048Activity::class.java).use { scenario ->
            onView(withId(R.id.board)).perform( swipeRight())
            comprobarModal(); scenario.recreate(); comprobarModal(); reintentar()
            assertEquals(0L, estado("2048").getLong("score"))
            assertEquals(64L, estado("2048").getLong("best"))
            assertEquals(2, (0..15).count { estado("2048").getJSONArray("cells").getInt(it) != 0 })
        }
    }
    @Test fun derrota2048InclusoConFicha2048VuelveAlMenu() {
        guardar("2048", juego2048(listOf(2048) + lleno.drop(1)))
        volverAlMenu(Juego2048Activity::class.java)
    }
    @Test fun explotarMinaAbreModalYReintenta() {
        guardar("mines", minas())
        ActivityScenario.launch(BuscaminasActivity::class.java).use { scenario ->
            onView(withContentDescription(startsWith("Fila 1, columna 1:"))).perform(click())
            comprobarModal(); scenario.recreate(); comprobarModal(); reintentar()
            assertEquals(-1, estado("mines").getInt("hit"))
            assertFalse(estado("mines").getBoolean("started"))
            assertEquals(0, estado("mines").getJSONArray("mines").length())
        }
    }
    @Test fun derrotaGuardadaBuscaminasVuelveAlMenu() {
        guardar("mines", minas(0)); volverAlMenu(BuscaminasActivity::class.java)
    }
    @Test fun choqueSnakeAbreModalYReintentaSinArrancarSolo() {
        guardar("snake", snake(false))
        ActivityScenario.launch(SnakeActivity::class.java).use { scenario ->
            onView(withId(R.id.board)).perform( swipeRight())
            Thread.sleep(400)
            comprobarModal(); scenario.recreate(); comprobarModal(); reintentar()
            assertFalse(estado("snake").getBoolean("over")); assertFalse(estado("snake").getBoolean("started"))
            assertEquals(80, estado("snake").getInt("best"))
            val cuerpo = estado("snake").getJSONArray("body").toString()
            Thread.sleep(500)
            assertEquals(cuerpo, estado("snake").getJSONArray("body").toString())
        }
    }
    @Test fun derrotaGuardadaSnakeVuelveAlMenu() {
        guardar("snake", snake(true)); volverAlMenu(SnakeActivity::class.java)
    }
    @Test fun victoriaBuscaminasNoMuestraDerrota() {
        guardar("mines", minas(abiertas = (10..63).toList()))
        ActivityScenario.launch(BuscaminasActivity::class.java).use {
            onView(withId(R.id.status)).check(matches(withText(R.string.arcade_mines_won)))
            onView(withText(R.string.arcade_defeat_title)).check(doesNotExist())
        }
    }
    @Test fun victoriaSnakeNoMuestraDerrota() {
        val cuerpo = (0..13).flatMap { fila -> (if (fila % 2 == 0) (0..13).toList() else (13 downTo 0).toList()).map { fila * 14 + it } }
        guardar("snake", snake(true).put("body", JSONArray(cuerpo)).put("food", -1))
        ActivityScenario.launch(SnakeActivity::class.java).use {
            onView(withId(R.id.status)).check(matches(withText(R.string.arcade_snake_win)))
            onView(withText(R.string.arcade_defeat_title)).check(doesNotExist())
        }
    }
    @Test fun victoria2048PermiteContinuarSinMostrarDerrota() {
        guardar("2048", juego2048(listOf(1024,1024) + List(14) { 0 }))
        ActivityScenario.launch(Juego2048Activity::class.java).use { scenario ->
            onView(withId(R.id.board)).perform( swipeLeft())
            onView(withText(R.string.arcade_continue)).inRoot(isDialog()).check(matches(isDisplayed()))
            scenario.recreate()
            onView(withText(R.string.arcade_continue)).inRoot(isDialog()).perform(click())
            assertTrue(estado("2048").getBoolean("win"))
            onView(withText(R.string.arcade_defeat_title)).check(doesNotExist())
        }
    }
}
