package com.example.prestamolabctma.data

import com.example.prestamolabctma.data.local.SolicitudPrestamoEntity
import com.example.prestamolabctma.data.local.Mappers.toDomain
import com.example.prestamolabctma.data.remote.EntornoRed
import com.example.prestamolabctma.data.remote.NetworkConfig
import com.example.prestamolabctma.data.remote.RemoteDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenciaMediaNetworkTest {

    @Test
    fun testVerificarPersistenciaDeUriSinBitmapNiBase64() {
        val entity = SolicitudPrestamoEntity(
            id = 1,
            equipoId = 2,
            ambienteDestino = "Aula 201",
            proposito = "Práctica de laboratorio",
            duracionHoras = 3,
            estado = "SOLICITADA",
            fechaRegistro = "2026-09-25",
            evidenciaUri = "content://media/external/images/media/12345",
            estadoEvidencia = "Local"
        )

        val dominio = entity.toDomain()

        assertEquals("content://media/external/images/media/12345", dominio.evidenciaUri)
        assertEquals("Local", dominio.estadoEvidencia)
        assertTrue("No debe almacenar datos pesados de Bitmap ni Base64", !dominio.evidenciaUri!!.startsWith("data:image"))
    }

    @Test
    fun testVerificarEstadosDeSubidaDeEvidenciaResiliente() = runBlocking {
        val remoteDS = RemoteDataSource()
        remoteDS.simularFalloRed = false

        val resExito = remoteDS.subirEvidenciaRemota(1, "content://media/external/images/media/12345")

        assertTrue(resExito.isSuccess)
        assertEquals("https://api.ctma.sena.edu.co/evidencias/1/foto.jpg", resExito.getOrNull())

        // Simular fallo de red
        remoteDS.simularFalloRed = true
        val resFallo = remoteDS.subirEvidenciaRemota(1, "content://media/external/images/media/12345")

        assertTrue(resFallo.isFailure)
        assertTrue(resFallo.exceptionOrNull()?.message?.contains("Fallo de red") == true)
    }

    @Test
    fun testVerificarManejoDeErroresEnEvidencia() = runBlocking {
        val remoteDS = RemoteDataSource()

        // URI Vacía
        val resVacio = remoteDS.subirEvidenciaRemota(1, "")
        assertTrue(resVacio.isFailure)
        assertEquals("URI de evidencia no válida.", resVacio.exceptionOrNull()?.message)

        // Archivo que excede tamaño máximo (10 MB)
        val resGrande = remoteDS.subirEvidenciaRemota(1, "content://media/archivo_grande_excedido.jpg")
        assertTrue(resGrande.isFailure)
        assertEquals("El archivo excede el tamaño máximo permitido de 10 MB.", resGrande.exceptionOrNull()?.message)
    }

    @Test
    fun testVerificarConfiguracionDeEntornosDeRed() {
        NetworkConfig.entornoActual = EntornoRed.DESARROLLO
        assertEquals("http://10.0.2.2:8080/", NetworkConfig.obtenerBaseUrl())
        assertTrue(NetworkConfig.validarSeguridadEntorno())

        NetworkConfig.entornoActual = EntornoRed.PRODUCCION
        assertEquals("https://api.ctma.sena.edu.co/", NetworkConfig.obtenerBaseUrl())
        assertTrue("Producción exige HTTPS", NetworkConfig.validarSeguridadEntorno())
        assertTrue(NetworkConfig.esProduccion())
    }
}
