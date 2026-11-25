package com.example.arcadia

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.arcadia.ajustes.AjustesActivity
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.petdoku.PetdokuActivity
import com.example.arcadia.juegos.snake.SnakeActivity
import org.hamcrest.Matchers.startsWith
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ArcadeFlowTest {
    private val instrumentation get()=InstrumentationRegistry.getInstrumentation()
    private val context get()=instrumentation.targetContext
    private val saves get()=context.getSharedPreferences("arcade_games",Context.MODE_PRIVATE)
    private val settings get()=context.getSharedPreferences("arcade_settings",Context.MODE_PRIVATE)
    @Before fun reset() { saves.edit().clear().commit();settings.edit().clear().putBoolean("sound",false).commit() }
    @After fun cleanup() { saves.edit().clear().commit() }
    private fun state(key:String)=JSONObject(saves.getString(key,"{}")!!)
    private fun cell(row:Int,col:Int)=withContentDescription(startsWith("Fila $row, columna $col:"))
    private fun screenshot(name:String) {
        instrumentation.waitForIdleSync()
        val bitmap=instrumentation.uiAutomation.takeScreenshot()
        File(context.getExternalFilesDir(null),"$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
    }
    @Test fun catalogoAbreLosCincoJuegos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            screenshot("catalogo")
            listOf("2048","BUSCAMINAS","PETDOKU","SNAKE","KENKEN").forEach { name->
                onView(withContentDescription("Jugar a $name")).perform(scrollTo(),click())
                screenshot(name.lowercase())
                androidx.test.espresso.Espresso.pressBack()
                onView(withId(R.id.settings)).check(matches(isDisplayed()))
            }
        }
    }
    @Test fun deslizar2048FusionaYConservaAlRecrear() {
        saves.edit().putString("2048",JSONObject().put("cells",JSONArray(listOf(2,2)+List(14){0})).put("score",0).toString()).commit()
        ActivityScenario.launch(Juego2048Activity::class.java).use { scenario->
            onView(withId(R.id.board)).perform(swipeLeft())
            assertEquals(4L,state("2048").getLong("score"))
            val before=state("2048").getJSONArray("cells").toString()
            scenario.recreate()
            assertEquals(before,state("2048").getJSONArray("cells").toString())
            onView(withId(R.id.restart)).perform(click())
            onView(withText(R.string.arcade_cancel)).perform(click())
            assertEquals(before,state("2048").getJSONArray("cells").toString())
            onView(withId(R.id.restart)).perform(click())
            onView(withId(android.R.id.button1)).perform(click())
            assertEquals(0L,state("2048").getLong("score"));assertEquals(4L,state("2048").getLong("best"))
        }
    }
    @Test fun victoria2048PermiteContinuar() {
        saves.edit().putString("2048",JSONObject().put("cells",JSONArray(listOf(1024,1024)+List(14){0})).put("score",0).toString()).commit()
        ActivityScenario.launch(Juego2048Activity::class.java).use {
            onView(withId(R.id.board)).perform(swipeLeft())
            onView(withId(android.R.id.button1)).check(matches(withText(R.string.arcade_continue))).perform(click())
            assertTrue(state("2048").getBoolean("win"))
            onView(withId(R.id.board)).perform(swipeDown())
            assertTrue(state("2048").getJSONArray("cells").toString().contains("2048"))
        }
    }
    @Test fun buscaminasBanderaPrimerToqueYGuardado() {
        ActivityScenario.launch(BuscaminasActivity::class.java).use { scenario->
            onView(cell(1,1)).perform(longClick())
            assertEquals(1,state("mines").getJSONArray("flags").length())
            onView(cell(1,1)).perform(longClick(),click())
            assertTrue(state("mines").getBoolean("started"));assertEquals(-1,state("mines").getInt("hit"))
            val before=state("mines").getJSONArray("open").toString();scenario.recreate()
            assertEquals(before,state("mines").getJSONArray("open").toString())
            screenshot("buscaminas_jugando")
        }
    }
    private fun esperarToque() {
        Thread.sleep(android.view.ViewConfiguration.getDoubleTapTimeout().toLong()+150)
        instrumentation.waitForIdleSync()
    }
    @Test fun petdokuDistingueToquesGuardaErroresYTerminaConTresFallos() {
        ActivityScenario.launch(PetdokuActivity::class.java).use { scenario->
            onView(cell(1,2)).perform(scrollTo(),click());esperarToque()
            assertEquals(2,state("petdoku").getJSONArray("marks").getInt(1));assertEquals(3,state("petdoku").getInt("lives"))
            onView(cell(1,2)).perform(click());esperarToque()
            assertEquals(0,state("petdoku").getJSONArray("marks").getInt(1))
            onView(cell(1,5)).perform(doubleClick());esperarToque()
            assertEquals(1,state("petdoku").getJSONArray("marks").getInt(4))
            onView(cell(1,1)).perform(doubleClick());esperarToque()
            assertEquals(3,state("petdoku").getJSONArray("marks").getInt(0));assertEquals(2,state("petdoku").getInt("lives"))
            onView(cell(1,1)).check { view, error ->
                if(error!=null)throw error
                assertEquals(android.graphics.Color.rgb(184,15,48),(view as android.widget.TextView).currentTextColor)
            }
            scenario.recreate();assertEquals(2,state("petdoku").getInt("lives"))
            assertEquals(3,state("petdoku").getJSONArray("marks").getInt(0))
            onView(cell(1,1)).perform(scrollTo(),doubleClick());esperarToque()
            assertEquals(2,state("petdoku").getInt("lives"))
            screenshot("petdoku_jugando")
            onView(cell(1,2)).perform(doubleClick());onView(cell(1,3)).perform(doubleClick());esperarToque()
            assertEquals(0,state("petdoku").getInt("lives"))
            onView(withText(R.string.petdoku_sin_vidas)).inRoot(isDialog()).check(matches(isDisplayed()))
            scenario.recreate()
            onView(withText(R.string.petdoku_sin_vidas)).inRoot(isDialog()).check(matches(isDisplayed()))
            onView(withText(R.string.petdoku_reintentar)).inRoot(isDialog()).perform(click())
            assertEquals(3,state("petdoku").getInt("lives"));assertEquals(0,state("petdoku").getJSONArray("marks").getInt(0))
        }
    }
    @Test fun snakeSePausaAlSalirYAlRecrear() {
        ActivityScenario.launch(SnakeActivity::class.java).use { scenario->
            onView(withId(R.id.board)).perform(swipeUp())
            Thread.sleep(500)
            scenario.moveToState(Lifecycle.State.CREATED)
            val body=state("snake").getJSONArray("body").toString()
            Thread.sleep(400)
            assertEquals(body,state("snake").getJSONArray("body").toString())
            scenario.moveToState(Lifecycle.State.RESUMED)
            onView(withId(R.id.status)).check(matches(withText(R.string.arcade_snake_paused)))
            scenario.recreate();assertEquals(body,state("snake").getJSONArray("body").toString())
            screenshot("snake_pausa")
        }
    }
    @Test fun ajustesPersistenYModoClaroFunciona() {
        ActivityScenario.launch(AjustesActivity::class.java).use {
            screenshot("ajustes")
            onView(withText(R.string.arcade_music)).perform(scrollTo(),click());assertTrue(settings.getBoolean("music",false))
            onView(withText(R.string.arcade_dark)).perform(scrollTo(),click());assertFalse(settings.getBoolean("dark",true))
            screenshot("ajustes_claro")
        }
        ActivityScenario.launch(MainActivity::class.java).use { screenshot("catalogo_claro") }
        settings.edit().clear().commit()
    }

    @Test fun menuIzquierdoAbreAjustesYUsaIconoAdaptativo() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.settings)).perform(click())
            onView(withId(R.id.menu_settings)).check(matches(isDisplayed()))
            screenshot("menu_lateral")
            onView(withId(R.id.menu_settings)).perform(click())
            onView(withText(R.string.arcade_volume)).check(matches(isDisplayed()))
            assertTrue(context.packageManager.getApplicationIcon(context.packageName) is android.graphics.drawable.AdaptiveIconDrawable)
        }
    }

    @Test fun petdokuArrastreMarcaVariasCasillasSinBorrarMascotas() {
        ActivityScenario.launch(PetdokuActivity::class.java).use {
            onView(cell(1,5)).perform(scrollTo(),doubleClick());esperarToque()
            val inicio = androidx.test.espresso.action.CoordinatesProvider { view ->
                val location=IntArray(2);view.getLocationOnScreen(location)
                floatArrayOf(location[0]+view.width*.10f,location[1]+view.height*.10f)
            }
            val fin = androidx.test.espresso.action.CoordinatesProvider { view ->
                val location=IntArray(2);view.getLocationOnScreen(location)
                floatArrayOf(location[0]+view.width*.90f,location[1]+view.height*.10f)
            }
            onView(withId(R.id.board)).perform(androidx.test.espresso.action.GeneralSwipeAction(
                androidx.test.espresso.action.Swipe.SLOW,inicio,fin,androidx.test.espresso.action.Press.FINGER))
            val marks=state("petdoku").getJSONArray("marks")
            assertEquals(1,marks.getInt(4))
            assertTrue((0..5).count { marks.getInt(it)==2 } >= 4)
            screenshot("petdoku_gestos")
        }
    }

    @Test fun snakeSePausaYContinuaTocandoElTableroSinBotones() {
        ActivityScenario.launch(SnakeActivity::class.java).use {
            onView(withId(R.id.board)).perform(swipeUp())
            assertTrue(state("snake").getBoolean("started"))
            onView(withId(R.id.board)).perform(click())
            onView(withId(R.id.status)).check(matches(withText(R.string.arcade_snake_paused)))
            onView(withId(R.id.board)).perform(click())
            onView(withId(R.id.status)).check(matches(withText(R.string.arcade_snake_running)))
        }
    }
}
