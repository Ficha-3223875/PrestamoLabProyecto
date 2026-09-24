package com.example.prstamolabctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos")
    fun getAllEquipos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    fun getEquipoById(id: Int): Flow<EquipoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipos(equipos: List<EquipoEntity>)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getEquipoCount(): Int

    @Query("UPDATE equipos SET estado = :nuevoEstado WHERE id = :equipoId")
    suspend fun updateEstadoEquipo(equipoId: Int, nuevoEstado: String)
}