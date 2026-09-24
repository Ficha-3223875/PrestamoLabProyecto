package com.example.prstamolabctma.viewmodel

import android.app.Application
import com.example.prstamolabctma.data.local.EquipoEntity
import com.example.prstamolabctma.data.preferences.PreferencesRepository
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.repository.FakeEquipoDao
import com.example.prstamolabctma.repository.FakeSolicitudDao
import com.example.prstamolabctma.repository.RoomPrestamoRepository
import com.example.prstamolabctma.ui.state.OperationState
import com.example.prstamolabctma.ui.state.UiState
import com.example.prstamolabctma.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakePreferencesRepository : PreferencesRepository {
    private val _filtro = MutableStateFlow<CategoriaEquipo?>(null)
    override val categoriaFiltroFlow: Flow<CategoriaEquipo?> = _filtro.asStateFlow()

    override suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
        _filtro.value = categoria
    }
}

class PrestamoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PrestamoViewModel
    private lateinit var equipoDao: FakeEquipoDao
    private lateinit var solicitudDao: FakeSolicitudDao
    private lateinit var preferencesRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        equipoDao = FakeEquipoDao()
        solicitudDao = FakeSolicitudDao()
        preferencesRepo = FakePreferencesRepository()

        val repo = RoomPrestamoRepository(equipoDao, solicitudDao, ioDispatcher = mainDispatcherRule.testDispatcher)
        val dummyApplication = Application()

        viewModel = PrestamoViewModel(dummyApplication, repo, preferencesRepo)
    }

    @Test
    fun equiposUiStateIniciaEnVacioOContenido() = runTest {
        val state = viewModel.equiposUiState.value
        assertTrue(state is UiState.Vacio || state is UiState.Cargando || state is UiState.Contenido)
    }

    @Test
    fun crearSolicitudValidaEmiteOperacionExitosa() = runTest {
        equipoDao.insertEquipo(
            EquipoEntity(1, "Multímetro", "ELECTRONICA", "DISPONIBLE")
        )

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "Laboratorio 1",
            proposito = "Medición de voltajes en circuito de potencia",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        viewModel.crearSolicitud(solicitud)

        val opState = viewModel.operacionState.value
        assertTrue(opState is OperationState.Exitosa)
        assertEquals("Solicitud creada con éxito", (opState as OperationState.Exitosa).mensaje)
    }

    @Test
    fun crearSolicitudDestinoVacioEmiteOperacionFallida() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "",
            proposito = "Medición de voltajes en circuito de potencia",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        viewModel.crearSolicitud(solicitud)

        val opState = viewModel.operacionState.value
        assertTrue(opState is OperationState.Fallida)
        assertEquals("El destino es obligatorio", (opState as OperationState.Fallida).mensaje)
    }

    @Test
    fun resetearEstadoOperacionRegresaAInactiva() = runTest {
        viewModel.crearSolicitud(
            SolicitudPrestamo(0, 1, "", "Propósito válido largo", 2, EstadoSolicitud.SOLICITADA)
        )
        assertTrue(viewModel.operacionState.value is OperationState.Fallida)

        viewModel.resetearEstadoOperacion()
        assertTrue(viewModel.operacionState.value is OperationState.Inactiva)
    }
}
