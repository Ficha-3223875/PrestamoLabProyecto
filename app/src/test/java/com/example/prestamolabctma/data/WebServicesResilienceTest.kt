package com.example.prestamolabctma.data

import com.example.prestamolabctma.data.remote.RemoteDataSource
import com.example.prestamolabctma.data.remote.dto.EquipoDto
import com.example.prestamolabctma.data.remote.dto.toDomain
import com.example.prestamolabctma.data.remote.dto.toEntity
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.EstadoEquipo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WebServicesResilienceTest {

    @Test
    fun testVerificarTransformacionCorrectaDeDtoAEntityYDomain() {
        val dto = EquipoDto(
            id = 101,
            nombre = "Impresora 3D Creality",
            categoria = "HERRAMIENTA",
            descripcion = "Impresora FDM para prototipado rápido",
            estado = "DISPONIBLE"
        )

        val entidad = dto.toEntity()
        val dominio = dto.toDomain()

        assertEquals(101, entidad.id)
        assertEquals("Impresora 3D Creality", entidad.nombre)
        assertEquals("HERRAMIENTA", entidad.categoria)

        assertEquals(101, dominio.id)
        assertEquals(CategoriaEquipo.HERRAMIENTA, dominio.categoria)
        assertEquals(EstadoEquipo.DISPONIBLE, dominio.estado)
    }

    @Test
    fun testVerificarSincronizacionRemotaExitosaActualizaElCatalogo() = runBlocking {
        val remoteDS = RemoteDataSource()
        remoteDS.simularFalloRed = false

        val equiposRemotos = remoteDS.obtenerEquiposRemotos()

        assertNotNull(equiposRemotos)
        assertTrue(equiposRemotos.isNotEmpty())
        assertEquals(6, equiposRemotos.size)
        assertEquals("Osciloscopio Digital", equiposRemotos.last().nombre)
    }

    @Test
    fun testVerificarResilienciaYPercaDeCacheCuandoLaRedFalla() = runBlocking {
        val remoteDS = RemoteDataSource()
        remoteDS.simularFalloRed = true

        var excepcionCapturada: Exception? = null
        try {
            remoteDS.obtenerEquiposRemotos()
        } catch (e: Exception) {
            excepcionCapturada = e
        }

        assertNotNull("Debió capturarse la excepción de red", excepcionCapturada)
        assertTrue(excepcionCapturada?.message?.contains("Fallo de red simulado") == true)
    }
}
