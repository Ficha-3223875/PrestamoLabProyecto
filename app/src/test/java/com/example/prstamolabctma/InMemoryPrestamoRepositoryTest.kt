package com.example.prstamolabctma

import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryPrestamoRepositoryTest {

    private lateinit var repository: InMemoryPrestamoRepository

    @Before
    fun setup() {
        repository = InMemoryPrestamoRepository()
    }

    @Test
    fun `verificar que se muestren los equipos disponibles`() = runTest {
        val equipos = repository.obtenerEquipos().first()
        val disponibles = equipos.filter { it.estado == EstadoEquipo.DISPONIBLE }

        assertTrue("Debe haber equipos disponibles", disponibles.isNotEmpty())
        assertEquals(3, disponibles.size)
    }

    @Test
    fun `verificar que la informacion de cada equipo sea correcta`() = runTest {
        val equipo = repository.obtenerEquipo(1).first()

        assertTrue(equipo != null)
        assertEquals("Multímetro Digital", equipo?.nombre)
        assertEquals(com.example.prstamolabctma.model.CategoriaEquipo.ELECTRONICA, equipo?.categoria)
    }

    @Test
    fun `verificar que los equipos no disponibles no aparezcan como disponibles`() = runTest {
        val equipos = repository.obtenerEquipos().first()
        val equipoPrestado = equipos.find { it.id == 3 }

        assertTrue(equipoPrestado?.estado != EstadoEquipo.DISPONIBLE)
    }

    @Test
    fun `verificar que cada equipo aparezca de manera individual`() = runTest {
        val equipos = repository.obtenerEquipos().first()
        val ids = equipos.map { it.id }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `verificar el comportamiento cuando no hay equipos registrados`() = runTest {
        val emptyRepo = object : com.example.prstamolabctma.data.repository.PrestamoRepository {
            override fun obtenerEquipos() = flowOf(emptyList<com.example.prstamolabctma.model.Equipo>())
            override fun obtenerEquipo(id: Int) = flowOf(null)
            override fun obtenerSolicitudes() = flowOf(emptyList<SolicitudPrestamo>())
            override fun obtenerSolicitud(id: Int) = flowOf(null)
            override suspend fun crearSolicitud(solicitud: SolicitudPrestamo) = Result.success(Unit)
            override suspend fun cancelarSolicitud(id: Int) = Result.success(Unit)
        }

        assertTrue(emptyRepo.obtenerEquipos().first().isEmpty())
    }

    @Test
    fun `verificar que se pueda registrar una solicitud valida`() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "Laboratorio 1",
            proposito = "Práctica de electrónica básica",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(solicitud)

        assertTrue("La solicitud debería crearse con éxito", resultado.isSuccess)

        val solicitudes = repository.obtenerSolicitudes().first()
        assertEquals(1, solicitudes.size)
        assertEquals(EstadoSolicitud.SOLICITADA, solicitudes[0].estado)

        val equipoActualizado = repository.obtenerEquipo(1).first()
        assertEquals(EstadoEquipo.RESERVADO, equipoActualizado?.estado)
    }

    @Test
    fun `verificar que no se creen solicitudes duplicadas para el mismo equipo`() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 2,
            ambienteDestino = "Laboratorio 2",
            proposito = "Práctica de audiovisual",
            duracionHoras = 3,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)
        val resultadoDuplicado = repository.crearSolicitud(solicitud)

        assertTrue("La segunda solicitud debería fallar", resultadoDuplicado.isFailure)
        assertEquals(1, repository.obtenerSolicitudes().first().size)
    }

    @Test
    fun `verificar que se pueda cancelar una solicitud SOLICITADA`() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 4,
            ambienteDestino = "Sala 3",
            proposito = "Uso de tableta para diseño",
            duracionHoras = 1,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)
        val idSolicitud = repository.obtenerSolicitudes().first()[0].id

        val resultadoCancelacion = repository.cancelarSolicitud(idSolicitud)

        assertTrue("La cancelación debería ser exitosa", resultadoCancelacion.isSuccess)

        val solicitudCancelada = repository.obtenerSolicitud(idSolicitud).first()
        assertEquals(EstadoSolicitud.CANCELADA, solicitudCancelada?.estado)

        val equipoLiberado = repository.obtenerEquipo(4).first()
        assertEquals(EstadoEquipo.DISPONIBLE, equipoLiberado?.estado)
    }
}
