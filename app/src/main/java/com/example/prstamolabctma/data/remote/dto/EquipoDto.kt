package com.example.prstamolabctma.data.remote.dto

import com.example.prstamolabctma.data.local.entity.EquipoEntity
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo

data class EquipoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
)

// DTO -> Modelo de Dominio
fun EquipoDto.toDomain(): Equipo {
    return Equipo(
        id = id,
        nombre = nombre,
        categoria = runCatching { CategoriaEquipo.valueOf(categoria) }.getOrDefault(CategoriaEquipo.OTRO),
        estado = runCatching { EstadoEquipo.valueOf(estado) }.getOrDefault(EstadoEquipo.DISPONIBLE)
    )
}

// DTO -> Entidad de Room
fun EquipoDto.toEntity(): EquipoEntity {
    return EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        estado = estado
    )
}

// Modelo de Dominio -> DTO
fun Equipo.toDto(): EquipoDto {
    return EquipoDto(
        id = id,
        nombre = nombre,
        categoria = categoria.name,
        estado = estado.name
    )
}