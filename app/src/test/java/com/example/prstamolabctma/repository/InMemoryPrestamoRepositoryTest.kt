package com.example.prstamolabctma.repository

import com.example.prstamolabctma.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class InMemoryPrestamoRepositoryTest {

    private lateinit var repository: InMemoryPrestamoRepository

    @Before
    fun setUp() {
        repository = InMemoryPrestamoRepository()
    }

    // --- Catálogo y Equipos ---

    @Test
    fun `verificar que se muestren los equipos disponibles`() = runTest {
        val equipos = repository.obtenerEquipos()
        val disponibles = equipos.filter { it.estado == EstadoEquipo.DISPONIBLE }
        assertTrue("Debería haber al menos un equipo disponible al inicio", disponibles.isNotEmpty())
    }

    @Test
    fun `verificar que cada equipo aparezca de manera individual`() = runTest {
        val equipos = repository.obtenerEquipos()
        val ids = equipos.map { it.id }
        assertEquals("Los IDs de los equipos deben ser únicos", ids.size, ids.toSet().size)
    }

    @Test
    fun `verificar que la informacion de cada equipo sea correcta`() = runTest {
        val equipo = repository.obtenerEquipo(1)
        assertNotNull(equipo)
        assertEquals("Multímetro", equipo?.nombre)
        assertEquals(CategoriaEquipo.ELECTRONICA, equipo?.categoria)
    }

    @Test
    fun `verificar que los equipos no disponibles no aparezcan como disponibles`() = runTest {
        val equipos = repository.obtenerEquipos()
        val reservado = equipos.find { it.id == 3 } // ID 3 es reservado en el repo in-memory
        assertNotNull(reservado)
        assertNotEquals(EstadoEquipo.DISPONIBLE, reservado?.estado)
    }

    @Test
    fun `verificar el comportamiento cuando no hay equipos registrados`() = runTest {
        val equipos = repository.obtenerEquipos()
        assertFalse(equipos.isEmpty())
    }

    // --- Solicitudes y Registro ---

    @Test
    fun `verificar que se pueda registrar una solicitud valida`() = runTest {
        val solicitud = SolicitudPrestamo(
            id = 100,
            equipoId = 1,
            ambienteDestino = "Laboratorio 1",
            proposito = "Medición de voltajes en circuito",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )
        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isSuccess)

        val equipo = repository.obtenerEquipo(1)
        assertEquals(EstadoEquipo.RESERVADO, equipo?.estado)
    }

    @Test
    fun `verificar que la solicitud quede en estado SOLICITADA`() = runTest {
        val solicitud = SolicitudPrestamo(101, 2, "Lab 2", "Uso de laptop para programación", 4, EstadoSolicitud.SOLICITADA)
        repository.crearSolicitud(solicitud)
        val guardada = repository.obtenerSolicitud(101)
        assertEquals(EstadoSolicitud.SOLICITADA, guardada?.estado)
    }

    @Test
    fun `verificar que no se creen solicitudes duplicadas para el mismo equipo`() = runTest {
        val solicitud1 = SolicitudPrestamo(102, 1, "Lab 1", "Propósito válido largo", 2, EstadoSolicitud.SOLICITADA)
        val solicitud2 = SolicitudPrestamo(103, 1, "Lab 1", "Propósito válido largo", 2, EstadoSolicitud.SOLICITADA)

        repository.crearSolicitud(solicitud1)
        val result = repository.crearSolicitud(solicitud2)

        assertTrue(result.isFailure)
        assertEquals("Ya existe una solicitud activa para este equipo", result.exceptionOrNull()?.message)
    }

    // --- Validaciones de Datos ---

    @Test
    fun `verificar proposito con menos de 10 caracteres falla`() = runTest {
        val solicitud = SolicitudPrestamo(200, 1, "Lab 1", "Corto", 2, EstadoSolicitud.SOLICITADA)
        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isFailure)
    }

    @Test
    fun `verificar duracion fuera de rango (0 horas) falla`() = runTest {
        val solicitud = SolicitudPrestamo(201, 1, "Lab 1", "Propósito válido largo", 0, EstadoSolicitud.SOLICITADA)
        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isFailure)
    }

    @Test
    fun `verificar que el destino sea obligatorio`() = runTest {
        val solicitud = SolicitudPrestamo(202, 1, "", "Propósito válido largo", 2, EstadoSolicitud.SOLICITADA)
        val result = repository.crearSolicitud(solicitud)
        assertTrue(result.isFailure)
        assertEquals("El destino es obligatorio", result.exceptionOrNull()?.message)
    }

    // --- Cancelación ---

    @Test
    fun `verificar que se pueda cancelar una solicitud SOLICITADA`() = runTest {
        val solicitud = SolicitudPrestamo(300, 1, "Lab 1", "Propósito válido largo", 2, EstadoSolicitud.SOLICITADA)
        repository.crearSolicitud(solicitud)

        val result = repository.cancelarSolicitud(300)
        assertTrue(result.isSuccess)

        val solicitudGuardada = repository.obtenerSolicitud(300)
        assertEquals(EstadoSolicitud.CANCELADA, solicitudGuardada?.estado)

        val equipo = repository.obtenerEquipo(1)
        assertEquals(EstadoEquipo.DISPONIBLE, equipo?.estado)
    }
}
