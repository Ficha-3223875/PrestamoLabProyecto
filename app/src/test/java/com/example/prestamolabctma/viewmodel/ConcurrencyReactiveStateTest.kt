package com.example.prestamolabctma.viewmodel

import com.example.prestamolabctma.data.InMemoryPrestamoRepository
import com.example.prestamolabctma.data.PrestamoRepository
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConcurrencyReactiveStateTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Fake Repositorio para probar catálogo vacío
    private class VacioRepository : PrestamoRepository {
        override fun listarEquipos(): List<Equipo> = emptyList()
        override fun obtenerEquipo(id: Int): Equipo? = null
        override fun listarSolicitudes(): List<SolicitudPrestamo> = emptyList()
        override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = null
        override suspend fun crearSolicitud(
            equipoId: Int,
            ambienteDestino: String,
            proposito: String,
            duracionHoras: Int
        ): Result<SolicitudPrestamo> = Result.failure(IllegalStateException("Sin equipos"))
        override suspend fun cancelarSolicitud(solicitudId: Int): Result<Unit> = Result.failure(IllegalStateException("Sin solicitudes"))
    }

    @Test
    fun testVerificarEstadoPantallaContenidoConEquiposDisponibles() {
        val repo = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repo)

        val estado = viewModel.uiState.value.estadoPantalla
        assertTrue("El estado inicial de pantalla debe ser Contenido cuando hay equipos", estado is EstadoPantalla.Contenido)
        val contenido = estado as EstadoPantalla.Contenido
        assertEquals(5, contenido.datos.size)
    }

    @Test
    fun testVerificarEstadoPantallaVacioCuandoNoHayEquipos() {
        val repo = VacioRepository()
        val viewModel = PrestamoViewModel(repo)

        val estado = viewModel.uiState.value.estadoPantalla
        assertTrue("El estado de pantalla debe ser Vacío cuando no hay equipos", estado is EstadoPantalla.Vacio)
        val vacio = estado as EstadoPantalla.Vacio
        assertEquals("No hay equipos registrados.", vacio.mensaje)
    }

    @Test
    fun testVerificarTransicionDeEstadoOperacionAlCrearSolicitud() = runBlocking {
        val repo = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repo)

        assertEquals(EstadoOperacion.Inactiva, viewModel.uiState.value.estadoOperacion)

        val exito = viewModel.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "Lab Concurrencia",
            proposito = "Práctica de corrutinas y StateFlow",
            duracionHoras = 3
        )

        assertTrue(exito)
        assertTrue("El estado de operación debe culminar en Exitosa", viewModel.uiState.value.estadoOperacion is EstadoOperacion.Exitosa)
        val exitosa = viewModel.uiState.value.estadoOperacion as EstadoOperacion.Exitosa
        assertEquals("Solicitud creada correctamente.", exitosa.mensaje)
    }

    @Test
    fun testVerificarEstadoOperacionFallidaConDatosInvalidos() = runBlocking {
        val repo = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repo)

        viewModel.crearSolicitud(
            equipoId = 1,
            ambienteDestino = "", // Destino inválido
            proposito = "Invalido",
            duracionHoras = 10
        )

        assertTrue("El estado de operación debe culminar en Fallida", viewModel.uiState.value.estadoOperacion is EstadoOperacion.Fallida)
        val fallida = viewModel.uiState.value.estadoOperacion as EstadoOperacion.Fallida
        assertEquals("El ambiente destino es obligatorio.", fallida.mensaje)
    }

    @Test
    fun testVerificarTransicionDeEstadoOperacionAlCancelarSolicitud() = runBlocking {
        val repo = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repo)

        viewModel.crearSolicitud(1, "Lab A", "Práctica de corrutinas en Android", 2)
        val solicitudCreada = viewModel.uiState.value.solicitudes.first()

        viewModel.cancelarSolicitud(solicitudCreada.id)

        assertTrue("El estado de operación debe pasar a Exitosa al cancelar", viewModel.uiState.value.estadoOperacion is EstadoOperacion.Exitosa)
        val exitosa = viewModel.uiState.value.estadoOperacion as EstadoOperacion.Exitosa
        assertEquals("Solicitud cancelada.", exitosa.mensaje)
    }
}
