package com.example.prstamolabctma.data.remote.api

import com.example.prstamolabctma.data.remote.dto.EquipoDto
import com.example.prstamolabctma.data.remote.dto.SolicitudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PrestamoApiService {

    @GET("equipos")
    suspend fun getEquipos(): Response<List<EquipoDto>>

    @GET("equipos/{id}")
    suspend fun getEquipoById(@Path("id") id: Int): Response<EquipoDto>

    @GET("solicitudes")
    suspend fun getSolicitudes(): Response<List<SolicitudDto>>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @PUT("solicitudes/{id}/cancelar")
    suspend fun cancelarSolicitud(@Path("id") id: Int): Response<Unit>
}