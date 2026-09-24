package com.example.prstamolabctma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prstamolabctma.model.EstadoSolicitud

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val evidenciaUri: String? = null,         // 👈 Añadido para la Guía 9 (URI de la evidencia fotográfica)
    val dispositivoBluetooth: String? = null   // 👈 Añadido para la Guía 9 (Capacidad física adicional Bluetooth)
)