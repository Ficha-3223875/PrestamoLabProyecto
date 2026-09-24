package com.example.prstamolabctma

import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.common.UiState
import com.example.prstamolabctma.viewmodel.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PrestamoViewModel
    private lateinit var fakeRepository: FakePrestamoRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakePrestamoRepository()
        viewModel = PrestamoViewModel(fakeRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `crearSolicitud con destino vacio debe mostrar error`() = runTest {
        viewModel.crearSolicitud(1, "", "Propósito válido con más de diez caracteres", 2)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("El ambiente o destino es obligatorio.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `crearSolicitud con proposito muy corto debe mostrar error`() = runTest {
        viewModel.crearSolicitud(1, "Lab A", "Corto", 2)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `crearSolicitud con proposito muy largo debe mostrar error`() = runTest {
        val propositoLargo = "a".repeat(181)
        viewModel.crearSolicitud(1, "Lab A", propositoLargo, 2)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `crearSolicitud con duracion fuera de rango debe mostrar error`() = runTest {
        viewModel.crearSolicitud(1, "Lab A", "Propósito válido de longitud suficiente", 9)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("La duración debe estar entre 1 y 8 horas.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `crearSolicitud valida debe actualizar el estado correctamente`() = runTest {
        viewModel.crearSolicitud(1, "Laboratorio Central", "Práctica de laboratorio semestral", 4)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Solicitud creada correctamente.", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.solicitudes.isNotEmpty())
    }

    @Test
    fun `verificar estado de lista de equipos como Content cuando hay equipos`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.equiposState.value
        assertTrue("El estado debería ser Content", state is UiState.Content)
        assertEquals(1, (state as UiState.Content).data.size)
    }

    @Test
    fun `verificar estado de lista de solicitudes como Empty cuando no hay solicitudes`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.solicitudesState.value
        assertTrue("El estado debería ser Empty", state is UiState.Empty)
    }

    @Test
    fun `cancelar solicitud debe actualizar el mensaje de exito`() = runTest {
        viewModel.crearSolicitud(1, "Destino", "Propósito para cancelación", 2)
        testDispatcher.scheduler.advanceUntilIdle()

        val id = viewModel.uiState.value.solicitudes[0].id

        viewModel.cancelarSolicitud(id)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Solicitud cancelada correctamente.", viewModel.uiState.value.mensaje)
    }
}

class FakePrestamoRepository : PrestamoRepository {
    private val equiposList = mutableListOf(
        Equipo(1, "Equipo 1", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE)
    )
    private val solicitudesList = mutableListOf<SolicitudPrestamo>()
    private var nextId = 1

    private val equiposFlow = MutableStateFlow<List<Equipo>>(equiposList.toList())
    private val solicitudesFlow = MutableStateFlow<List<SolicitudPrestamo>>(solicitudesList.toList())

    override fun obtenerEquipos(): Flow<List<Equipo>> = equiposFlow

    override fun obtenerEquipo(id: Int): Flow<Equipo?> {
        return equiposFlow.map { list -> list.find { it.id == id } }
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> = solicitudesFlow

    override fun obtenerSolicitud(id: Int): Flow<SolicitudPrestamo?> {
        return solicitudesFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val nueva = solicitud.copy(id = nextId++)
        solicitudesList.add(nueva)
        equiposFlow.value = equiposList.toList()
        solicitudesFlow.value = solicitudesList.toList()
        return Result.success(Unit)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudesList.indexOfFirst { it.id == id }
        if (index != -1) {
            solicitudesList[index] = solicitudesList[index].copy(estado = EstadoSolicitud.CANCELADA)
            solicitudesFlow.value = solicitudesList.toList()
            return Result.success(Unit)
        }
        return Result.failure(Exception("Not found"))
    }
}
