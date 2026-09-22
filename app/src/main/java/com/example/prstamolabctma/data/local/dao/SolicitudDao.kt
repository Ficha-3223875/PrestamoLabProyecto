package com.example.prstamolabctma.data.local.dao

import androidx.room.*
import com.example.prstamolabctma.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow // 👈 Importar Flow

@Dao
interface SolicitudDao {
    // 👈 Cambiado a Flow para que la UI se entere de los cambios automáticamente (Semana 7)
    @Query("SELECT * FROM solicitudes")
    fun obtenerSolicitudes(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerSolicitud(id: Int): SolicitudEntity?

    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity): Unit
}