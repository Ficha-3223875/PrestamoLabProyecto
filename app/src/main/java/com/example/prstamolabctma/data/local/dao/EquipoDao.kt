package com.example.prstamolabctma.data.local.dao

import androidx.room.*
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow // 👈 Importante: Importar Flow

@Dao
interface EquipoDao {
    // 👈 Cambiado a Flow para emisión reactiva automática (Semana 7)
    @Query("SELECT * FROM equipos")
    fun obtenerEquipos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerEquipo(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquipos(equipos: List<EquipoEntity>): List<Long>

    @Update
    suspend fun actualizarEquipo(equipo: EquipoEntity): Unit
}