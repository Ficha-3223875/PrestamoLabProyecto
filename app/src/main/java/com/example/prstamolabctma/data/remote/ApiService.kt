package com.example.prstamolabctma.data.remote

import com.example.prstamolabctma.data.remote.dto.EquipoDto
import com.example.prstamolabctma.data.remote.dto.SolicitudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("equipos")
    suspend fun obtenerEquiposRemotos(): Response<List<EquipoDto>>

    @GET("solicitudes")
    suspend fun obtenerSolicitudesRemotas(): Response<List<SolicitudDto>>

    @POST("solicitudes")
    suspend fun crearSolicitudRemota(@Body solicitud: SolicitudDto): Response<SolicitudDto>
}