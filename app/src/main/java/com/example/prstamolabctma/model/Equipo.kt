package com.example.prstamolabctma.model

data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    var estado: EstadoEquipo
)