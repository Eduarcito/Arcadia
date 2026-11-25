package com.example.arcadia.arcade

import com.example.arcadia.compartido.estado.Direccion

import com.example.arcadia.juegos.juego2048.*
import com.example.arcadia.juegos.buscaminas.ReglasBuscaminas
import com.example.arcadia.juegos.petdoku.*
import com.example.arcadia.juegos.snake.ReglasSnake
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class ReglasArcadeTest {
    private fun tablero2048(vararg valores:Int)=Reglas2048(Random(10)).apply { restaurar(valores.toList()+List(16-valores.size){0},0) }

    @Test fun inicio2048TieneDosFichas() {
        val g=Reglas2048();assertEquals(2,g.celdas.count{it>0});assertEquals(0L,g.puntos)
        assertTrue(g.celdas.all{it in listOf(0,2,4)})
    }
    @Test fun fusion2048NoEncadenaLaMismaFicha() {
        val g=tablero2048(2,2,4,0);g.mover(Direccion.IZQUIERDA)
        assertEquals(listOf(4,4),g.celdas.take(2));assertEquals(4L,g.puntos)
    }
    @Test fun cuatroFichasProducenDosPares() {
        val g=tablero2048(2,2,2,2);g.mover(Direccion.IZQUIERDA)
        assertEquals(listOf(4,4),g.celdas.take(2));assertEquals(8L,g.puntos);assertEquals(3,g.celdas.count{it>0})
    }
    @Test fun fusion2048EliminaEspaciosYRespetaElSentido() {
        val g=tablero2048(2,0,2,2);g.mover(Direccion.DERECHA)
        assertEquals(listOf(2,4),g.celdas.subList(2,4))
        assertEquals(4L,g.puntos)
    }
    @Test fun movimientosVerticales2048() {
        for(d in listOf(Direccion.ARRIBA,Direccion.ABAJO)) {
            val g=Reglas2048();g.restaurar(List(16){if(it%4==0)2 else 0},0);g.mover(d)
            val indices=if(d==Direccion.ARRIBA)listOf(0,4) else listOf(8,12)
            assertTrue(indices.all{g.celdas[it]==4});assertEquals(8L,g.puntos)
        }
    }
    @Test fun movimientoInvalidoNoAgregaFicha() {
        val g=tablero2048(2,4,8,16);val antes=g.celdas
        assertFalse(g.mover(Direccion.IZQUIERDA));assertEquals(antes,g.celdas);assertEquals(0L,g.puntos)
    }
    @Test fun victoriaYFin2048() {
        val g=tablero2048(1024,1024);g.mover(Direccion.IZQUIERDA);assertTrue(g.victoria);assertEquals(2048L,g.puntos)
        g.restaurar(listOf(2,4,2,4,4,2,4,2,2,4,2,4,4,2,4,2),0);assertTrue(g.terminado)
        g.restaurar(listOf(2,4,2,4,2,8,4,2,4,2,8,4,2,4,2,8),0);assertFalse(g.terminado)
    }
    @Test fun partidasAleatorias2048ConservanSumaSalvoFichaNueva() {
        val azar=Random(9);val g=Reglas2048(azar)
        repeat(600){
            val suma=g.celdas.sumOf{it.toLong()};val cambio=g.mover(Direccion.entries[azar.nextInt(4)])
            assertTrue(g.celdas.sumOf{it.toLong()}-suma in if(cambio)listOf(2L,4L)else listOf(0L))
            if(g.terminado)g.reiniciar()
        }
    }
    @Test fun guardado2048InvalidoSeRechaza() { assertFalse(Reglas2048().restaurar(List(16){3},0)) }

    @Test fun primerToqueSiempreTieneZonaSegura() {
        for(i in 0..63){val g=ReglasBuscaminas(Random(i));g.abrir(i)
            assertEquals(10,g.minas.size);assertTrue(g.minas.intersect((g.vecinas(i)+i).toSet()).isEmpty());assertEquals(-1,g.explosion)
        }
    }
    @Test fun banderasBloqueanAperturaYSePuedenQuitar() {
        val g=ReglasBuscaminas();g.marcar(10);assertFalse(g.abrir(10));assertFalse(g.iniciada)
        g.marcar(10);assertTrue(g.abrir(10));assertTrue(g.iniciada)
    }
    @Test fun limiteDeBanderas() { val g=ReglasBuscaminas();repeat(10){assertTrue(g.marcar(it))};assertFalse(g.marcar(11)) }
    @Test fun victoriaMinaYReinicio() {
        val g=ReglasBuscaminas(Random(2));g.abrir(27)
        for(i in 0..63)if(i !in g.minas)g.abrir(i)
        assertTrue(g.victoria);assertEquals(54,g.abiertas.size);assertFalse(g.marcar(0))
        g.reiniciar();g.abrir(0);g.abrir(g.minas.first());assertTrue(g.terminada);assertFalse(g.victoria)
        g.reiniciar();assertFalse(g.iniciada);assertTrue(g.minas.isEmpty())
    }
    @Test fun expansionDeCerosYConteosCorrectos() {
        val g=ReglasBuscaminas(Random(3));g.abrir(27)
        assertTrue(g.vecinas(27).all{it in g.abiertas})
        for(i in 0..63)assertEquals(g.vecinas(i).count{it in g.minas},g.alrededor(i))
    }
    @Test fun restauracionBuscaminasConservaEstado() {
        val a=ReglasBuscaminas(Random(4));a.abrir(0);a.marcar(a.minas.first())
        val b=ReglasBuscaminas();assertTrue(b.restaurar(a.minas,a.abiertas,a.banderas,a.iniciada,a.explosion))
        assertEquals(a.minas,b.minas);assertEquals(a.abiertas,b.abiertas);assertEquals(a.banderas,b.banderas)
    }

    private fun soluciones(regiones:List<Int>):List<List<Int>> {
        val salida=mutableListOf<List<Int>>()
        fun buscar(columnas:List<Int>) {
            val fila=columnas.size
            if(fila==6){salida.add(columnas.mapIndexed{r,c->r*6+c});return}
            for(c in 0..5) {
                if(c in columnas || (fila>0 && kotlin.math.abs(c-columnas.last())<=1))continue
                if(columnas.indices.any{regiones[it*6+columnas[it]]==regiones[fila*6+c]})continue
                buscar(columnas+c)
            }
        }
        buscar(emptyList());return salida
    }
    @Test fun todosLosNivelesPetdokuTienenSolucionUnicaYRegionesConectadas() {
        assertEquals(12,NIVELES_PETDOKU.size)
        for(regiones in NIVELES_PETDOKU){
            assertEquals(1,soluciones(regiones).size)
            for(region in 0..5){
                val conjunto=regiones.indices.filter{regiones[it]==region}.toSet();val vistos=mutableSetOf(conjunto.first())
                var anteriores=0
                while(anteriores!=vistos.size){anteriores=vistos.size
                    vistos.addAll(conjunto.filter{p->vistos.any{q->kotlin.math.abs(p/6-q/6)+kotlin.math.abs(p%6-q%6)==1}})
                }
                assertEquals(conjunto,vistos)
            }
        }
    }
    @Test fun petdokuUnToqueSoloAlternaNotasSinGastarVidas() {
        val g=ReglasPetdoku()
        repeat(4) { assertTrue(g.tocar(0));assertEquals(if(it%2==0)2 else 0,g.marcas[0]);assertEquals(3,g.vidas) }
    }
    @Test fun petdokuCompruebaLaSolucionInclusoSinMascotasPrevias() {
        for(n in NIVELES_PETDOKU.indices) {
            val solucion=soluciones(NIVELES_PETDOKU[n]).single()
            for(i in 0..35) {
                val g=ReglasPetdoku();repeat(n){g.siguiente()}
                g.tocar(i);assertTrue(g.descubrir(i))
                assertEquals(if(i in solucion)1 else 3,g.marcas[i])
                assertEquals(if(i in solucion)3 else 2,g.vidas)
                assertFalse(g.tocar(i));assertFalse(g.descubrir(i))
                assertFalse(g.pintarDescarte(i,true));assertFalse(g.pintarDescarte(i,false))
            }
        }
    }
    @Test fun petdokuTresErroresTerminanLaPartidaYReinicioDevuelveVidas() {
        val g=ReglasPetdoku();val malas=(0..35).filter{it !in soluciones(g.regiones).single()}
        malas.take(3).forEachIndexed { i,pos->g.descubrir(pos);assertEquals(2-i,g.vidas) }
        assertTrue(g.terminada);assertFalse(g.victoria)
        assertFalse(g.descubrir(4));assertFalse(g.tocar(5));assertFalse(g.pintarDescarte(5,false))
        g.reiniciar();assertEquals(3,g.vidas);assertFalse(g.terminada);assertTrue(g.marcas.all{it==0})
    }
    @Test fun petdokuRestauraErroresVidasYConvierteMascotasAntiguasIncorrectasEnNotas() {
        val g=ReglasPetdoku();g.descubrir(0);g.descubrir(4);g.tocar(1)
        val copia=ReglasPetdoku();assertTrue(copia.restaurar(0,g.marcas.toList(),g.vidas))
        assertEquals(g.marcas,copia.marcas);assertEquals(2,copia.vidas)
        assertFalse(copia.restaurar(0,g.marcas.toList(),3))
        val antiguas=MutableList(36){0};antiguas[0]=1;antiguas[4]=1
        assertTrue(copia.restaurar(0,antiguas));assertEquals(2,copia.marcas[0]);assertEquals(1,copia.marcas[4]);assertEquals(3,copia.vidas)
        val falsa=MutableList(36){0};falsa[4]=3
        assertFalse(copia.restaurar(0,falsa,2))
    }
    @Test fun petdokuGanaSoloConSolucionYAvanza() {
        val g=ReglasPetdoku()
        repeat(12) { n ->
            val solucion=soluciones(g.regiones).single()
            g.descubrir((0..35).first{it !in solucion})
            solucion.forEach{g.descubrir(it)}
            assertTrue(g.victoria);assertEquals(2,g.vidas);assertFalse(g.tocar(0))
            g.siguiente();assertEquals((n+1)%12,g.nivel);assertTrue(g.marcas.all{it==0});assertEquals(3,g.vidas)
        }
    }
    @Test fun petdokuReinicioConservaNivel() { val g=ReglasPetdoku();g.siguiente();g.tocar(0);g.reiniciar();assertEquals(1,g.nivel);assertTrue(g.marcas.all{it==0}) }

    @Test fun snakeNoPuedeGirarHaciaAtrasNiEncadenarReversa() {
        val g=ReglasSnake();assertFalse(g.girar(Direccion.IZQUIERDA));g.girar(Direccion.ARRIBA)
        assertFalse(g.girar(Direccion.IZQUIERDA));g.paso();assertEquals(90,g.cuerpo.first())
    }
    @Test fun snakeComeCreceYPuntua() {
        val g=ReglasSnake(Random(3));assertTrue(g.restaurar(listOf(104,103,102),1,105,0,false));g.paso()
        assertEquals(4,g.cuerpo.size);assertEquals(10,g.puntos);assertTrue(g.comida !in g.cuerpo)
    }
    @Test fun snakeChoqueConParedYCuerpo() {
        val g=ReglasSnake();g.restaurar(listOf(13,12,11),1,30,0,false);g.paso();assertTrue(g.terminada)
        g.restaurar(listOf(30,31,45,44,43,29),1,100,0,false);g.paso();assertTrue(g.terminada)
    }
    @Test fun snakePuedeEntrarEnLaColaQueSeRetira() {
        val g=ReglasSnake();g.restaurar(listOf(30,31,45,44),2,100,0,false);g.paso()
        assertFalse(g.terminada);assertEquals(44,g.cuerpo.first())
    }
    @Test fun snakeReinicioYGuardadoInvalido() {
        val g=ReglasSnake();assertFalse(g.restaurar(listOf(0,20,21),1,30,0,false))
        repeat(20){g.paso()};assertTrue(g.terminada);g.reiniciar();assertFalse(g.terminada);assertEquals(3,g.cuerpo.size)
    }
    @Test fun snakePeligroAdelanteDetectaPared() {
        val g=ReglasSnake();g.restaurar(listOf(13,12,11),1,30,0,false)
        assertTrue(g.peligroAdelante())
    }
    @Test fun snakePeligroAdelanteDetectaCuerpo() {
        val g=ReglasSnake();g.restaurar(listOf(30,31,45,44,43,29),1,100,0,false)
        assertTrue(g.peligroAdelante())
    }
    @Test fun snakePeligroAdelanteRespetaColaQueSeRetira() {
        val g=ReglasSnake();g.restaurar(listOf(30,31,45,44),2,100,0,false)
        assertFalse(g.peligroAdelante())
    }
    @Test fun snakePeligroAdelanteSigueLaDireccionPendiente() {
        val g=ReglasSnake();g.restaurar(listOf(13,12,11),1,30,0,false)
        assertTrue(g.peligroAdelante())
        g.girar(Direccion.ABAJO)
        assertFalse(g.peligroAdelante())
    }
    @Test fun snakeSinPeligroEnCaminoLibre() {
        val g=ReglasSnake()
        assertFalse(g.peligroAdelante())
    }
    @Test fun snakeDireccionFrutaEnCasillas() {
        val g=ReglasSnake();g.restaurar(listOf(104,103,102),1,105,0,false)
        assertEquals(1 to 0,g.direccionFruta())
        g.restaurar(listOf(104,103,102),1,0,0,false)
        assertEquals(-6 to -7,g.direccionFruta())
    }
}
