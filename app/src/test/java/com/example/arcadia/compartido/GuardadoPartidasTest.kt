package com.example.arcadia.compartido

import android.content.Context
import android.content.SharedPreferences
import com.example.arcadia.compartido.estado.Direccion
import com.example.arcadia.juegos.buscaminas.BuscaminasViewModel
import com.example.arcadia.juegos.juego2048.Juego2048ViewModel
import com.example.arcadia.juegos.petdoku.PetdokuViewModel
import com.example.arcadia.juegos.snake.SnakeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GuardadoPartidasTest {
    private lateinit var preferencias: SharedPreferences

    @Before
    fun preparar() {
        preferencias = RuntimeEnvironment.getApplication()
            .getSharedPreferences("test_partidas", Context.MODE_PRIVATE)
        preferencias.edit().clear().commit()
    }

    @Test
    fun `2048 guarda y restaura la partida completa`() {
        val original = Juego2048ViewModel()
        original.mover(Direccion.IZQUIERDA)
        original.mover(Direccion.ARRIBA)
        original.mover(Direccion.DERECHA)
        original.guardar(preferencias)

        val restaurado = Juego2048ViewModel()
        restaurado.cargar(preferencias)
        assertEquals(original.juego.celdas, restaurado.juego.celdas)
        assertEquals(original.juego.puntos, restaurado.juego.puntos)
        assertEquals(original.record, restaurado.record)
    }

    @Test
    fun `2048 escribe las claves del formato guardado`() {
        val vm = Juego2048ViewModel()
        vm.mover(Direccion.IZQUIERDA)
        vm.guardar(preferencias)
        val json = preferencias.getString("2048", "")!!
        assertTrue(json.contains("\"cells\""))
        assertTrue(json.contains("\"score\""))
        assertTrue(json.contains("\"best\""))
        assertTrue(json.contains("\"win\""))
    }

    @Test
    fun `buscaminas guarda y restaura minas abiertas y banderas`() {
        val original = BuscaminasViewModel()
        original.actuar(0, false)
        original.actuar(5, false)
        original.actuar(9, true)
        original.guardar(preferencias)

        val restaurado = BuscaminasViewModel()
        restaurado.cargar(preferencias)
        assertEquals(original.juego.minas, restaurado.juego.minas)
        assertEquals(original.juego.abiertas, restaurado.juego.abiertas)
        assertEquals(original.juego.banderas, restaurado.juego.banderas)
        assertEquals(original.juego.iniciada, restaurado.juego.iniciada)
    }

    @Test
    fun `las claves de guardado son las del formato inmutable`() {
        assertEquals("2048", Juego2048ViewModel().clave)
        assertEquals("mines", BuscaminasViewModel().clave)
        assertEquals("petdoku", PetdokuViewModel().clave)
        assertEquals("snake", SnakeViewModel().clave)
    }

    @Test
    fun `buscaminas conserva la clave flagMode en el formato`() {
        val vm = BuscaminasViewModel()
        vm.actuar(0, false)
        vm.guardar(preferencias)
        assertTrue(preferencias.getString("mines", "")!!.contains("\"flagMode\""))
    }

    @Test
    fun `petdoku guarda y restaura marcas vidas y nivel`() {
        val original = PetdokuViewModel()
        original.tocar(0, false)
        original.tocar(7, false)
        original.pintarDescarte(3, false)
        original.guardar(preferencias)

        val restaurado = PetdokuViewModel()
        restaurado.cargar(preferencias)
        assertEquals(original.juego.marcas.toList(), restaurado.juego.marcas.toList())
        assertEquals(original.juego.vidas, restaurado.juego.vidas)
        assertEquals(original.juego.nivel, restaurado.juego.nivel)
    }

    @Test
    fun `snake guarda y restaura cuerpo direccion y puntos`() {
        val original = SnakeViewModel()
        original.empezada = true
        original.corriendo = true
        original.girar(Direccion.ABAJO)
        repeat(4) { original.paso() }
        original.guardar(preferencias)

        val restaurado = SnakeViewModel()
        restaurado.cargar(preferencias)
        assertEquals(original.juego.cuerpo, restaurado.juego.cuerpo)
        assertEquals(original.juego.direccion, restaurado.juego.direccion)
        assertEquals(original.juego.puntos, restaurado.juego.puntos)
        assertEquals(original.juego.comida, restaurado.juego.comida)
        assertEquals(original.record, restaurado.record)
        assertEquals(original.empezada, restaurado.empezada)
    }

    @Test
    fun `json corrupto no rompe la carga y deja el juego inicial`() {
        preferencias.edit().putString("snake", "esto no es json{{{").commit()
        val vm = SnakeViewModel()
        vm.cargar(preferencias)
        assertEquals(0, vm.juego.puntos)
        assertTrue(!vm.empezada)
    }

    @Test
    fun `cargar solo restaura una vez`() {
        val original = SnakeViewModel()
        original.empezada = true
        original.girar(Direccion.ABAJO)
        original.paso()
        original.guardar(preferencias)

        val restaurado = SnakeViewModel()
        restaurado.cargar(preferencias)
        preferencias.edit().clear().commit()
        restaurado.cargar(preferencias)
        assertEquals(original.juego.cuerpo, restaurado.juego.cuerpo)
    }
}
