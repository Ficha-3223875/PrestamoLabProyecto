package com.example.prstamolabctma.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidencias")
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val solicitudId: Int,
    val uri: String,
    val tipoMime: String,
    val tamanoBytes: Long,
    val estado: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)
