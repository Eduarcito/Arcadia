package com.example.arcadia

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.action.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.arcadia.juegos.juego2048.Juego2048Activity
import com.example.arcadia.juegos.buscaminas.BuscaminasActivity
import com.example.arcadia.juegos.snake.SnakeActivity
import com.example.arcadia.juegos.numberconnect.NumberConnectActivity
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
class JuegosCompletosTest {
    private val instrumentation get()=InstrumentationRegistry.getInstrumentation()
    private val context get()=instrumentation.targetContext
    private val saves get()=context.getSharedPreferences("arcade_games",Context.MODE_PRIVATE)
    private val settings get()=context.getSharedPreferences("arcade_settings",Context.MODE_PRIVATE)
    private var anteriores=emptyMap<String,String?>()
    private var sonido:Boolean?=null
    @Before fun preparar(){
        anteriores=listOf("2048","mines","snake","number_connect").associateWith{saves.getString(it,null)}
        saves.edit().apply{anteriores.keys.forEach{remove(it)}}.commit()
        sonido=if(settings.contains("sound"))settings.getBoolean("sound",true) else null
        settings.edit().putBoolean("sound",false).commit()
    }
    @After fun restaurar(){
        saves.edit().apply{anteriores.forEach{(k,v)->if(v==null)remove(k)else putString(k,v)}}.commit()
        settings.edit().apply{if(sonido==null)remove("sound")else putBoolean("sound",sonido!!)}.commit()
    }
    private fun estado(clave:String)=JSONObject(saves.getString(clave,"{}")!!)
    private fun guardar(clave:String,json:JSONObject){saves.edit().putString(clave,json.toString()).commit()}
    private fun captura(nombre:String){
        val bitmap=instrumentation.uiAutomation.takeScreenshot()
        File(context.getExternalFilesDir(null),"$nombre.png").outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        bitmap.recycle()
    }
    private fun completo(){
        onView(withId(R.id.board)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.back)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.restart)).check(matches(isCompletelyDisplayed()))
    }
    private fun ficha(i:Int)=withContentDescription(startsWith("Fila ${i/5+1}, columna ${i%5+1}:"))
    private fun conectar(a:Int,b:Int) {
        fun posicion(i:Int)=CoordinatesProvider { view ->
            val p=IntArray(2);view.getLocationOnScreen(p)
            floatArrayOf(p[0]+view.width*(i%5+.5f)/5,p[1]+view.height*(i/5+.5f)/7)
        }
        onView(withId(R.id.board)).perform(GeneralSwipeAction(Swipe.SLOW,posicion(a),posicion(b),Press.FINGER))
    }
    @Test fun tableros2048YSnakeCabenSinDesplazamiento(){
        ActivityScenario.launch(Juego2048Activity::class.java).use { completo();captura("2048_completo") }
        ActivityScenario.launch(SnakeActivity::class.java).use { completo();captura("snake_completo") }
    }
    @Test fun minasTieneSelectorPistaUnicaYTableroCompleto(){
        ActivityScenario.launch(BuscaminasActivity::class.java).use { scenario ->
            completo()
            onView(withId(R.id.marcar)).perform(click())
            onView(withContentDescription(startsWith("Fila 1, columna 1:"))).perform(click())
            assertEquals(1,estado("mines").getJSONArray("flags").length())
            assertFalse(estado("mines").getBoolean("started"))
            onView(withId(R.id.descubrir)).perform(click())
            onView(withId(R.id.pista)).perform(click())
            assertTrue(estado("mines").getBoolean("hintUsed"));assertEquals(-1,estado("mines").getInt("hit"))
            onView(withId(R.id.pista)).check(matches(isNotEnabled()))
            scenario.recreate();completo();onView(withId(R.id.pista)).check(matches(isNotEnabled()))
            captura("buscaminas_completo")
        }
    }
    @Test fun victoriaMinasIncrementaRachaUnaVez(){
        guardar("mines",JSONObject().put("mines",JSONArray((0..9).toList())).put("open",JSONArray((10..62).toList()))
            .put("flags",JSONArray()).put("started",true).put("hit",-1).put("streak",2).put("bestStreak",2))
        ActivityScenario.launch(BuscaminasActivity::class.java).use { scenario ->
            onView(withContentDescription(startsWith("Fila 8, columna 8:"))).perform(click())
            assertEquals(3,estado("mines").getInt("streak"));assertEquals(3,estado("mines").getInt("bestStreak"))
            scenario.recreate();assertEquals(3,estado("mines").getInt("streak"))
            onView(withId(R.id.restart)).perform(click());onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
            assertEquals(3,estado("mines").getInt("streak"));assertFalse(estado("mines").getBoolean("hintUsed"))
        }
    }
    @Test fun numberConnectCombinaPorGestoYGuarda(){
        guardar("number_connect",JSONObject().put("cells",JSONArray(List(35){if(it in 30..31)8 else 2})).put("score",0))
        ActivityScenario.launch(NumberConnectActivity::class.java).use { scenario ->
            completo();conectar(30,31)
            assertEquals(16L,estado("number_connect").getLong("score"))
            assertEquals(16,estado("number_connect").getJSONArray("cells").getInt(31))
            val antes=estado("number_connect").toString();scenario.recreate()
            assertEquals(antes,estado("number_connect").toString());completo();captura("number_connect")
            onView(withId(R.id.restart)).perform(click());onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
            assertEquals(0,estado("number_connect").getInt("score"));assertEquals(16,estado("number_connect").getInt("best"))
        }
    }
    @Test fun numberConnectNoCombinaUnToqueNiNumerosDistintos(){
        guardar("number_connect",JSONObject().put("cells",JSONArray(List(35){if(it==31)8 else 2})).put("score",0))
        ActivityScenario.launch(NumberConnectActivity::class.java).use {
            onView(ficha(30)).perform(click());assertEquals(0,estado("number_connect").getInt("score"))
            conectar(30,31);assertEquals(0,estado("number_connect").getInt("score"))
        }
    }
    @Test fun previsualizacionSeActualizaAntesDeSoltarYAlRetroceder() {
        guardar("number_connect",JSONObject().put("cells",JSONArray(List(35){if(it in 30..32)8 else 2})).put("score",0))
        ActivityScenario.launch(NumberConnectActivity::class.java).use { scenario ->
            val inicio=android.os.SystemClock.uptimeMillis()
            fun tocar(accion:Int,indice:Int) {
                scenario.onActivity { actividad ->
                    val tablero=actividad.findViewById<android.view.View>(R.id.board)
                    val evento=android.view.MotionEvent.obtain(inicio,android.os.SystemClock.uptimeMillis(),accion,
                        tablero.width*(indice%5+.5f)/5,tablero.height*(indice/5+.5f)/7,0)
                    tablero.dispatchTouchEvent(evento);evento.recycle()
                }
            }
            tocar(android.view.MotionEvent.ACTION_DOWN,30)
            tocar(android.view.MotionEvent.ACTION_MOVE,31)
            tocar(android.view.MotionEvent.ACTION_MOVE,32)
            onView(withId(R.id.suma_connect)).check(matches(withText("8 + 8 + 8 = 24")))
            onView(withId(R.id.resultado_connect)).check(matches(withText("32")))
            completo();assertEquals(0,estado("number_connect").getInt("score"));captura("connect_previsualizacion")
            tocar(android.view.MotionEvent.ACTION_MOVE,31)
            onView(withId(R.id.suma_connect)).check(matches(withText("8 + 8 = 16")))
            onView(withId(R.id.resultado_connect)).check(matches(withText("16")))
            tocar(android.view.MotionEvent.ACTION_CANCEL,31)
            onView(withId(R.id.suma_connect)).check(matches(withText(R.string.connect_preview_vacio)))
            assertEquals(0,estado("number_connect").getInt("score"))
            tocar(android.view.MotionEvent.ACTION_DOWN,30)
            tocar(android.view.MotionEvent.ACTION_MOVE,31)
            tocar(android.view.MotionEvent.ACTION_MOVE,32)
            tocar(android.view.MotionEvent.ACTION_UP,32)
            assertEquals(32,estado("number_connect").getInt("score"))
            onView(withId(R.id.suma_connect)).check(matches(withText(R.string.connect_preview_vacio)))
        }
    }
    @Test fun numberConnectFinReintentoYMenu(){
        val fin=JSONObject().put("cells",JSONArray(List(35){1 shl (1+(it/5%2)*2+it%5%2)})).put("score",80)
        guardar("number_connect",fin)
        ActivityScenario.launch(NumberConnectActivity::class.java).use {
            onView(withText(R.string.arcade_retry)).inRoot(isDialog()).perform(click());completo()
            assertEquals(0,estado("number_connect").getInt("score"));assertEquals(80,estado("number_connect").getInt("best"))
        }
        guardar("number_connect",fin)
        ActivityScenario.launch(NumberConnectActivity::class.java).use {
            onView(withText(R.string.arcade_main_menu)).inRoot(isDialog()).perform(click())
            onView(withContentDescription("Jugar a NUMBERCONNECT")).perform(scrollTo(),click())
            onView(withText(R.string.arcade_defeat_title)).inRoot(isDialog()).check(matches(isDisplayed()))
        }
    }
}
