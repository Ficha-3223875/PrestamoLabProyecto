package com.example.prstamolabctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prstamolabctma.data.local.entity.SolicitudPrestamoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudPrestamoDao {

    @Query("SELECT * FROM solicitudes_prestamo")
    fun getAllSolicitudes(): Flow<List<SolicitudPrestamoEntity>>

    @Query("SELECT * FROM solicitudes_prestamo WHERE id = :id")
    fun getSolicitudById(id: Int): Flow<SolicitudPrestamoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitud(solicitud: SolicitudPrestamoEntity): Long

    @Update
    suspend fun updateSolicitud(solicitud: SolicitudPrestamoEntity)

    @Query("UPDATE solicitudes_prestamo SET estado = :nuevoEstado, fotoEvidenciaUri = :evidenciaUri WHERE id = :solicitudId")
    suspend fun registrarDevolucion(solicitudId: Int, nuevoEstado: String, evidenciaUri: String?)
}