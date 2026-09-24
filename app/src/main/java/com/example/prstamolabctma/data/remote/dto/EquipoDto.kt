package com.example.prstamolabctma.data.remote.dto

import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo

data class EquipoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
) {
    fun toDomain(): Equipo {
        return Equipo(
            id = id,
            nombre = nombre,
            categoria = try {
                CategoriaEquipo.valueOf(categoria)
            } catch (e: Exception) {
                CategoriaEquipo.ELECTRONICA
            },
            estado = try {
                EstadoEquipo.valueOf(estado)
            } catch (e: Exception) {
                EstadoEquipo.DISPONIBLE
            }
        )
    }
}