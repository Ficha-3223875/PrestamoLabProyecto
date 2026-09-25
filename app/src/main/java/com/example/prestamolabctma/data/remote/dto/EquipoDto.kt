package com.example.prestamolabctma.data.remote.dto

import com.example.prestamolabctma.data.local.EquipoEntity
import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) para deserialización desde la API REST (Semana 8)
 */
data class EquipoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("estado") val estado: String
)

data class SolicitudDto(
    @SerializedName("id") val id: Int,
    @SerializedName("equipoId") val equipoId: Int,
    @SerializedName("ambienteDestino") val ambienteDestino: String,
    @SerializedName("proposito") val proposito: String,
    @SerializedName("duracionHoras") val duracionHoras: Int,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String = ""
)

// --- MAPEADORES DTO -> ENTITY Y DTO -> DOMINIO ---
fun EquipoDto.toEntity() = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria,
    descripcion = descripcion,
    estado = estado
)

fun EquipoDto.toDomain() = Equipo(
    id = id,
    nombre = nombre,
    categoria = CategoriaEquipo.valueOf(categoria),
    descripcion = descripcion,
    estado = EstadoEquipo.valueOf(estado)
)
