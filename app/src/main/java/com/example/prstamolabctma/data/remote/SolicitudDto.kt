package com.example.prstamolabctma.data.remote

import com.google.gson.annotations.SerializedName

data class SolicitudDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("equipoId") val equipoId: Int,
    @SerializedName("ambienteDestino") val ambienteDestino: String,
    @SerializedName("proposito") val proposito: String,
    @SerializedName("duracionHoras") val duracionHoras: Int,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaCreacion") val fechaCreacion: Long = System.currentTimeMillis()
)
