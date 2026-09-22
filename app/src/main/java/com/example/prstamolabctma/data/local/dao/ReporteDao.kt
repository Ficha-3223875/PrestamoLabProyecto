package com.example.prstamolabctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prstamolabctma.data.local.entity.ReporteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReporte(reporte: ReporteEntity)

    @Query("SELECT * FROM reportes ORDER BY id DESC")
    fun obtenerTodosLosReportes(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id LIMIT 1")
    suspend fun obtenerReportePorId(id: Int): ReporteEntity?

    @Query("DELETE FROM reportes WHERE id = :id")
    suspend fun eliminarReportePorId(id: Int)
}