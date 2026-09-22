package com.example.prstamolabctma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.EstadoEquipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo
)