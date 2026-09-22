package com.example.prstamolabctma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

@Entity(tableName = "solicitudes_prestamo")
data class SolicitudPrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val fotoEvidenciaUri: String? = null
)

fun SolicitudPrestamoEntity.toDomain(): SolicitudPrestamo = SolicitudPrestamo(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = EstadoSolicitud.valueOf(estado)
)

fun SolicitudPrestamo.toEntity(fotoEvidenciaUri: String? = null): SolicitudPrestamoEntity = SolicitudPrestamoEntity(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado.name,
    fotoEvidenciaUri = fotoEvidenciaUri
)