package com.example.prstamolabctma.data.remote

import retrofit2.http.*

interface PrestamoApiService {
    @GET("equipos")
    suspend fun getEquipos(): List<EquipoDto>

    @GET("solicitudes")
    suspend fun getSolicitudes(): List<SolicitudDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): SolicitudDto

    @PUT("solicitudes/{id}/cancelar")
    suspend fun cancelarSolicitud(@Path("id") id: Int): SolicitudDto
}
