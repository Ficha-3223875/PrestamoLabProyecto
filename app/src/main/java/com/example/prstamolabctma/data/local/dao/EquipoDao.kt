package com.example.prstamolabctma.data.local.dao

import androidx.room.*
import com.example.prstamolabctma.data.local.entity.EquipoEntity

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    suspend fun obtenerEquipos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerEquipo(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipos(equipos: List<EquipoEntity>): List<Long>

    @Update
    suspend fun actualizarEquipo(equipo: EquipoEntity): Unit
}