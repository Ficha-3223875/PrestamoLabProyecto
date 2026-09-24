package com.example.prstamolabctma.repository

import com.example.prstamolabctma.data.local.EquipoEntity
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OfflineFirstRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var equipoDao: FakeEquipoDao
    private lateinit var solicitudDao: FakeSolicitudDao
    private lateinit var fakeApi: FakePrestamoApiService
    private lateinit var repository: RoomPrestamoRepository

    @Before
    fun setUp() {
        equipoDao = FakeEquipoDao()
        solicitudDao = FakeSolicitudDao()
        fakeApi = FakePrestamoApiService()

        repository = RoomPrestamoRepository(
            equipoDao = equipoDao,
            solicitudDao = solicitudDao,
            apiService = fakeApi,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun refrescarDatosExitosoActualizaRoom() = runTest(testDispatcher) {
        val result = repository.refrescarDatos()
        assertTrue(result.isSuccess)

        val equiposRoom = repository.obtenerEquipos()
        assertEquals(3, equiposRoom.size)
        assertEquals("Multímetro Remote", equiposRoom[0].nombre)
    }

    @Test
    fun refrescarDatosConErrorDeRedConservaCacheLocalRoom() = runTest(testDispatcher) {
        // Cargar caché local previo en Room
        equipoDao.insertEquipo(EquipoEntity(10, "Osciloscopio Local", "ELECTRONICA", "DISPONIBLE"))

        // Simular fallo de red en la API
        fakeApi.shouldFailWithNetworkError = true

        val result = repository.refrescarDatos()
        assertTrue(result.isFailure)
        assertEquals("Sin conexión a internet", result.exceptionOrNull()?.message)

        // Verificar que los datos locales en Room NO se borraron
        val equiposRoom = repository.obtenerEquipos()
        assertEquals(1, equiposRoom.size)
        assertEquals("Osciloscopio Local", equiposRoom[0].nombre)
    }
}
