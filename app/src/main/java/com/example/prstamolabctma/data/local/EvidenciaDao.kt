package com.example.prstamolabctma.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Query("SELECT * FROM evidencias WHERE solicitudId = :solicitudId ORDER BY id DESC")
    fun getEvidenciasPorSolicitudFlow(solicitudId: Int): Flow<List<EvidenciaEntity>>

    @Query("SELECT * FROM evidencias WHERE solicitudId = :solicitudId ORDER BY id DESC")
    suspend fun getEvidenciasPorSolicitud(solicitudId: Int): List<EvidenciaEntity>

    @Query("SELECT * FROM evidencias WHERE id = :id")
    suspend fun getEvidenciaById(id: Int): EvidenciaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidencia(evidencia: EvidenciaEntity): Long

    @Query("UPDATE evidencias SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoEvidencia(id: Int, estado: String)

    @Query("DELETE FROM evidencias WHERE id = :id")
    suspend fun deleteEvidencia(id: Int)
}
