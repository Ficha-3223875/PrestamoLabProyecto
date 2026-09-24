package com.example.prstamolabctma

import com.example.prstamolabctma.data.remote.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Apunta al servidor simulado local
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun test_obtener_equipos_remotos_exitoso() = runBlocking {
        // Respuesta JSON simulada que devolvería el servidor
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "nombre": "Multímetro Digital",
                    "categoria": "ELECTRONICA",
                    "estado": "DISPONIBLE"
                }
            ]
        """

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = apiService.obtenerEquiposRemotos()

        assertNotNull(response)
        assertEquals(true, response.isSuccessful)

        val equipos = response.body()
        assertNotNull(equipos)
        assertEquals(1, equipos?.size)
        assertEquals("Multímetro Digital", equipos?.first()?.nombre)
    }

    @Test
    fun test_crear_solicitud_remota_exitoso() = runBlocking {
        val jsonResponse = """
            {
                "id": 10,
                "equipoId": 1,
                "ambienteDestino": "Lab 202",
                "proposito": "Práctica de mediciones eléctricas",
                "duracionHoras": 3,
                "estado": "SOLICITADA"
            }
        """

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(201))

        // Solicitud de prueba enviada al DTO
        val solicitudDto = com.example.prstamolabctma.data.remote.dto.SolicitudDto(
            id = 0,
            equipoId = 1,
            ambienteDestino = "Lab 202",
            proposito = "Práctica de mediciones eléctricas",
            duracionHoras = 3,
            estado = "SOLICITADA"
        )

        val response = apiService.crearSolicitudRemota(solicitudDto)

        assertNotNull(response)
        assertEquals(true, response.isSuccessful)
        assertEquals(10, response.body()?.id)
        assertEquals("Lab 202", response.body()?.ambienteDestino)
    }
}