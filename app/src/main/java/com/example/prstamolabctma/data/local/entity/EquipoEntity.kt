package com.example.prstamolabctma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val estado: String
)

fun EquipoEntity.toDomain(): Equipo = Equipo(
    id = id,
    nombre = nombre,
    categoria = CategoriaEquipo.valueOf(categoria),
    estado = EstadoEquipo.valueOf(estado)
)

fun Equipo.toEntity(): EquipoEntity = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria.name,
    estado = estado.name
)