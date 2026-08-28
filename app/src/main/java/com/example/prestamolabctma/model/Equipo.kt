package com.example.prestamolabctma.model

enum class CategoriaEquipo {
    ELECTRONICA,
    COMPUTO,
    MEDICION,
    AUDIOVISUAL,
    HERRAMIENTA
}

enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val descripcion: String,
    val estado: EstadoEquipo
)