package com.example.prstamolabctma.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    fun getEquiposFlow(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos")
    suspend fun getEquiposList(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getEquipoById(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipos(equipos: List<EquipoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    @Query("UPDATE equipos SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoEquipo(id: Int, estado: String)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int
}
