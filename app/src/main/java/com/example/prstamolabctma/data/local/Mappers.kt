package com.example.prstamolabctma.data.local

import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

fun EquipoEntity.toDomain(): Equipo {
    return Equipo(
        id = id,
        nombre = nombre,
        categoria = runCatching { CategoriaEquipo.valueOf(categoria) }.getOrDefault(CategoriaEquipo.OTRO),
        estado = runCatching { EstadoEquipo.valueOf(estado) }.getOrDefault(EstadoEquipo.DISPONIBLE)
    )
}

fun Equipo.toEntity(): EquipoEntity {
    return EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria.name,
        estado = estado.name
    )
}

fun SolicitudEntity.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = runCatching { EstadoSolicitud.valueOf(estado) }.getOrDefault(EstadoSolicitud.SOLICITADA)
    )
}

fun SolicitudPrestamo.toEntity(fechaCreacion: Long = System.currentTimeMillis()): SolicitudEntity {
    return SolicitudEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado.name,
        fechaCreacion = fechaCreacion
    )
}
