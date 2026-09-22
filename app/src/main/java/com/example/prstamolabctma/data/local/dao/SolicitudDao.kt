package com.example.prstamolabctma.data.local.dao

import androidx.room.*
import com.example.prstamolabctma.data.local.entity.SolicitudEntity

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudEntity>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerSolicitud(id: Int): SolicitudEntity?

    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity): Unit
}