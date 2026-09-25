package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.data.PrestamoRepository
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Clase Fake para simular diferentes estados del backend (Semana 7: suspend) ---
    class FakePrestamoRepository : PrestamoRepository {
        var equiposInternos = mutableListOf<Equipo>()
        var solicitudesInternas = mutableListOf<SolicitudPrestamo>()
        
        var alCrearSolicitud: (() -> Unit)? = null

        override fun listarEquipos(): List<Equipo> = equiposInternos.toList()
        
        override fun obtenerEquipo(id: Int): Equipo? = equiposInternos.find { it.id == id }
        
        override fun listarSolicitudes(): List<SolicitudPrestamo> = solicitudesInternas.toList()
        
        override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudesInternas.find { it.id == id }

        override suspend fun crearSolicitud(
            equipoId: Int,
            ambienteDestino: String,
            proposito: String,
            duracionHoras: Int
        ): Result<SolicitudPrestamo> {
            alCrearSolicitud?.invoke()
            
            val nueva = SolicitudPrestamo(
                id = solicitudesInternas.size + 1,
                equipoId = equipoId,
                ambienteDestino = ambienteDestino,
                proposito = proposito,
                duracionHoras = duracionHoras,
                estado = EstadoSolicitud.SOLICITADA
            )
            solicitudesInternas.add(nueva)
            return Result.success(nueva)
        }

        override suspend fun cancelarSolicitud(solicitudId: Int): Result<Unit> {
            val idx = solicitudesInternas.indexOfFirst { it.id == solicitudId }
            if (idx != -1) {
                solicitudesInternas[idx] = solicitudesInternas[idx].copy(estado = EstadoSolicitud.CANCELADA)
            }
            return Result.success(Unit)
        }
    }

    // --- GRUPO: Guardar / Doble Pulsación / Idempotencia en ViewModel ---

    @Test
    fun testVerificarQueUnaPulsacionEnGuardarCreeUnaSolaSolicitud() = runBlocking {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Laptop", CategoriaEquipo.COMPUTO, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)

        val exito = viewModel.crearSolicitud(1, "Aula 102", "Clase de Programación Android", 2)
        
        assertTrue(exito)
        assertEquals("Debe haberse creado exactamente una solicitud", 1, viewModel.uiState.value.solicitudes.size)
    }

    @Test
    fun testVerificarQueDespuesDeGuardarSolamenteExistaUnaSolicitud() = runBlocking {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Laptop", CategoriaEquipo.COMPUTO, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)

        viewModel.crearSolicitud(1, "Aula 102", "Clase de Programación Android", 2)
        
        assertEquals(1, viewModel.uiState.value.solicitudes.size)
    }

    @Test
    fun testVerificarQueLaInformacionDeLaSolicitudSeMantengaCorrectamente() = runBlocking {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Laptop", CategoriaEquipo.COMPUTO, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)

        viewModel.crearSolicitud(1, "Aula 102", "Clase de Programación Android", 2)
        
        val solicitudCreada = viewModel.uiState.value.solicitudes.first()
        assertEquals("Aula 102", solicitudCreada.ambienteDestino)
        assertEquals("Clase de Programación Android", solicitudCreada.proposito)
        assertEquals(2, solicitudCreada.duracionHoras)
    }

    // --- GRUPO: Comportamiento del Catálogo ---

    @Test
    fun testVerificarQueSeMuestreElCatalogo() {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Kit Arduino", CategoriaEquipo.ELECTRONICA, "Desc", EstadoEquipo.DISPONIBLE))
            equiposInternos.add(Equipo(2, "Monitor", CategoriaEquipo.COMPUTO, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)
        
        assertNotNull(viewModel.uiState.value.equipos)
        assertEquals(2, viewModel.uiState.value.equipos.size)
    }

    @Test
    fun testVerificarQueCadaEquipoMuestreNombreCategoriaYEstado() {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Kit Arduino", CategoriaEquipo.ELECTRONICA, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)
        
        val equipo = viewModel.uiState.value.equipos.first()
        assertEquals("Kit Arduino", equipo.nombre)
        assertEquals(CategoriaEquipo.ELECTRONICA, equipo.categoria)
        assertEquals(EstadoEquipo.DISPONIBLE, equipo.estado)
    }

    @Test
    fun testVerificarQueSeMuestrenVariosEquiposCorrectamente() {
        val fakeRepo = FakePrestamoRepository().apply {
            equiposInternos.add(Equipo(1, "Kit Arduino", CategoriaEquipo.ELECTRONICA, "Desc", EstadoEquipo.DISPONIBLE))
            equiposInternos.add(Equipo(2, "Portátil Lenovo", CategoriaEquipo.COMPUTO, "Desc", EstadoEquipo.DISPONIBLE))
            equiposInternos.add(Equipo(3, "Multímetro", CategoriaEquipo.MEDICION, "Desc", EstadoEquipo.DISPONIBLE))
        }
        val viewModel = PrestamoViewModel(fakeRepo)
        
        assertEquals(3, viewModel.uiState.value.equipos.size)
        assertEquals("Kit Arduino", viewModel.uiState.value.equipos[0].nombre)
        assertEquals("Portátil Lenovo", viewModel.uiState.value.equipos[1].nombre)
        assertEquals("Multímetro", viewModel.uiState.value.equipos[2].nombre)
    }

    @Test
    fun testVerificarElComportamientoCuandoNoHayEquiposRegistrados() {
        val fakeRepo = FakePrestamoRepository()
        val viewModel = PrestamoViewModel(fakeRepo)
        
        assertTrue("El catálogo debe estar vacío", viewModel.uiState.value.equipos.isEmpty())
    }
}
