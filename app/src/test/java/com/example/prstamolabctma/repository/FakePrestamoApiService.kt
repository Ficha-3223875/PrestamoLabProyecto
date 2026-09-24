package com.example.prstamolabctma.repository

import com.example.prstamolabctma.data.remote.EquipoDto
import com.example.prstamolabctma.data.remote.PrestamoApiService
import com.example.prstamolabctma.data.remote.SolicitudDto
import java.io.IOException

class FakePrestamoApiService : PrestamoApiService {
    var shouldFailWithNetworkError = false
    var shouldFailWithServerError = false

    val equiposRemotos = mutableListOf(
        EquipoDto(1, "Multímetro Remote", "ELECTRONICA", "DISPONIBLE"),
        EquipoDto(2, "Laptop Remote", "INFORMATICA", "DISPONIBLE"),
        EquipoDto(3, "Cámara Remote", "OTRO", "DISPONIBLE")
    )

    val solicitudesRemotas = mutableListOf<SolicitudDto>()

    override suspend fun getEquipos(): List<EquipoDto> {
        if (shouldFailWithNetworkError) throw IOException("Sin conexión a internet")
        if (shouldFailWithServerError) throw RuntimeException("HTTP 500 Internal Server Error")
        return equiposRemotos.toList()
    }

    override suspend fun getSolicitudes(): List<SolicitudDto> {
        if (shouldFailWithNetworkError) throw IOException("Sin conexión a internet")
        if (shouldFailWithServerError) throw RuntimeException("HTTP 500 Internal Server Error")
        return solicitudesRemotas.toList()
    }

    override suspend fun crearSolicitud(solicitud: SolicitudDto): SolicitudDto {
        if (shouldFailWithNetworkError) throw IOException("Sin conexión a internet")
        solicitudesRemotas.add(solicitud)
        return solicitud
    }

    override suspend fun cancelarSolicitud(id: Int): SolicitudDto {
        if (shouldFailWithNetworkError) throw IOException("Sin conexión a internet")
        val index = solicitudesRemotas.indexOfFirst { it.id == id }
        if (index != -1) {
            val cancelada = solicitudesRemotas[index].copy(estado = "CANCELADA")
            solicitudesRemotas[index] = cancelada
            return cancelada
        }
        throw RuntimeException("Solicitud no encontrada")
    }
}
