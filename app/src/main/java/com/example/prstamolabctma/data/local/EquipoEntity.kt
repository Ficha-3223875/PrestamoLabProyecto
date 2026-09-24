package com.example.prstamolabctma.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
)
