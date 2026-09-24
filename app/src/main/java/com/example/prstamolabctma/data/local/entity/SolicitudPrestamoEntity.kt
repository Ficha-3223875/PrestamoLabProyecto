package com.example.prstamolabctma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

@Entity(tableName = "solicitudes")
data class SolicitudPrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val evidenciaUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)

fun SolicitudPrestamoEntity.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = try { EstadoSolicitud.valueOf(estado) } catch (_: Exception) { EstadoSolicitud.SOLICITADA },
        evidenciaUri = evidenciaUri,
        latitud = latitud,
        longitud = longitud
    )
}

fun SolicitudPrestamo.toEntity(): SolicitudPrestamoEntity {
    return SolicitudPrestamoEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado.name,
        evidenciaUri = evidenciaUri,
        latitud = latitud,
        longitud = longitud
    )
}