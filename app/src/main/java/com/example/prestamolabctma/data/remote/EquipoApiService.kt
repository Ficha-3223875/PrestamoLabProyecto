package com.example.prestamolabctma.data.remote

import com.example.prestamolabctma.data.remote.dto.EquipoDto
import com.example.prestamolabctma.data.remote.dto.SolicitudDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaz de servicio Retrofit para comunicación con la API REST (Semana 8)
 */
interface EquipoApiService {

    @GET("api/equipos")
    suspend fun obtenerEquipos(
        @Header("Authorization") tokenAuth: String = "Bearer token_demo_ctma"
    ): List<EquipoDto>

    @GET("api/solicitudes")
    suspend fun obtenerSolicitudes(
        @Header("Authorization") tokenAuth: String = "Bearer token_demo_ctma"
    ): List<SolicitudDto>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(
        @Header("Authorization") tokenAuth: String = "Bearer token_demo_ctma",
        @Body solicitud: SolicitudDto
    ): SolicitudDto
}
