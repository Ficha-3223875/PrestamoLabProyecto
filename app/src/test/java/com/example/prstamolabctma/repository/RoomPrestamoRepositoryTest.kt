package com.example.prstamolabctma.repository

import com.example.prstamolabctma.data.local.EquipoDao
import com.example.prstamolabctma.data.local.EquipoEntity
import com.example.prstamolabctma.data.local.SolicitudDao
import com.example.prstamolabctma.data.local.SolicitudEntity
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeEquipoDao : EquipoDao {
    val equipos = mutableListOf<EquipoEntity>()

    override fun getEquiposFlow() = flowOf(equipos.toList())
    override suspend fun getEquiposList() = equipos.toList()
    override suspend fun getEquipoById(id: Int) = equipos.find { it.id == id }
    override suspend fun insertEquipos(equipos: List<EquipoEntity>) { this.equipos.addAll(equipos) }
    override suspend fun insertEquipo(equipo: EquipoEntity) { this.equipos.add(equipo) }
    override suspend fun updateEstadoEquipo(id: Int, estado: String) {
        val index = equipos.indexOfFirst { it.id == id }
        if (index != -1) {
            equipos[index] = equipos[index].copy(estado = estado)
        }
    }
    override suspend fun getCount() = equipos.size
}

class FakeSolicitudDao : SolicitudDao {
    val solicitudes = mutableListOf<SolicitudEntity>()

    override fun getSolicitudesFlow() = flowOf(solicitudes.toList())
    override suspend fun getSolicitudesList() = solicitudes.toList()
    override suspend fun getSolicitudById(id: Int) = solicitudes.find { it.id == id }
    override suspend fun insertSolicitud(solicitud: SolicitudEntity): Long {
        val newId = if (solicitud.id == 0) solicitudes.size + 1 else solicitud.id
        val entity = solicitud.copy(id = newId)
        solicitudes.add(entity)
        return newId.toLong()
    }
    override suspend fun updateEstadoSolicitud(id: Int, estado: String) {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index != -1) {
            solicitudes[index] = solicitudes[index].copy(estado = estado)
        }
    }
    override suspend fun countSolicitudesActivasPorEquipo(equipoId: Int, estado: String): Int {
        return solicitudes.count { it.equipoId == equipoId && it.estado == estado }
    }
}

class RoomPrestamoRepositoryTest {

    private lateinit var equipoDao: FakeEquipoDao
    private lateinit var solicitudDao: FakeSolicitudDao
    private lateinit var repository: RoomPrestamoRepository

    @Before
    fun setUp() {
        equipoDao = FakeEquipoDao()
        solicitudDao = FakeSolicitudDao()
        repository = RoomPrestamoRepository(equipoDao, solicitudDao)

        runTest {
            equipoDao.insertEquipos(
                listOf(
                    EquipoEntity(1, "Multímetro", "ELECTRONICA", "DISPONIBLE"),
                    EquipoEntity(2, "Laptop", "INFORMATICA", "DISPONIBLE")
                )
            )
        }
    }

    @Test
    fun crearSolicitudValidaExito() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "Laboratorio 1",
            proposito = "Medición de voltajes en circuito",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isSuccess)

        val equipo = repository.obtenerEquipo(1)
        assertEquals("RESERVADO", equipo?.estado?.name)
    }

    @Test
    fun crearSolicitudDestinoVacioFalla() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "",
            proposito = "Medición de voltajes en circuito",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isFailure)
        assertEquals("El destino es obligatorio", result.exceptionOrNull()?.message)
    }
}
