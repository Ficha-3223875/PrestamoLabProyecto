package com.example.prstamolabctma.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun getSolicitudesFlow(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    suspend fun getSolicitudesList(): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun getSolicitudById(id: Int): SolicitudEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitud(solicitud: SolicitudEntity): Long

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoSolicitud(id: Int, estado: String)

    @Query("SELECT COUNT(*) FROM solicitudes WHERE equipoId = :equipoId AND estado = :estado")
    suspend fun countSolicitudesActivasPorEquipo(equipoId: Int, estado: String): Int
}
