package com.example.prstamolabctma.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File

data class MetadatosEvidencia(
    val uri: String,
    val tipoMime: String,
    val tamanoBytes: Long
)

object EvidenciaValidator {
    const val MAX_BYTES_PERMITIDOS = 5 * 1024 * 1024L // 5 MB

    fun esMimeImagenValida(tipoMime: String?): Boolean {
        if (tipoMime.isNullOrBlank()) return false
        return tipoMime.startsWith("image/")
    }

    fun esTamanoValido(tamanoBytes: Long): Boolean {
        return tamanoBytes in 1..MAX_BYTES_PERMITIDOS
    }

    fun validarMetadatos(tipoMime: String?, tamanoBytes: Long): Result<Unit> {
        if (!esMimeImagenValida(tipoMime)) {
            return Result.failure(Exception("Formato no soportado. Debe ser una imagen (JPG, PNG, WEBP)."))
        }
        if (!esTamanoValido(tamanoBytes)) {
            return Result.failure(Exception("El archivo excede el tamaño máximo permitido de 5 MB."))
        }
        return Result.success(Unit)
    }

    fun validarEvidencia(context: Context, uri: Uri): Result<MetadatosEvidencia> {
        val contentResolver = context.contentResolver

        val tipoMime = contentResolver.getType(uri) ?: when (uri.toString().substringAfterLast('.', "")) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }

        var tamanoBytes = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                tamanoBytes = cursor.getLong(sizeIndex)
            }
        }

        if (tamanoBytes == 0L) {
            runCatching {
                contentResolver.openInputStream(uri)?.use { stream ->
                    tamanoBytes = stream.available().toLong()
                }
            }
        }

        val validacionMetadatos = validarMetadatos(tipoMime, tamanoBytes)
        if (validacionMetadatos.isFailure) {
            return Result.failure(validacionMetadatos.exceptionOrNull()!!)
        }

        return Result.success(
            MetadatosEvidencia(
                uri = uri.toString(),
                tipoMime = tipoMime,
                tamanoBytes = tamanoBytes
            )
        )
    }

    fun crearUriFotoCamara(context: Context): Uri {
        val directorioFotos = File(context.cacheDir, "fotos")
        if (!directorioFotos.exists()) {
            directorioFotos.mkdirs()
        }
        val archivoFoto = File(directorioFotos, "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivoFoto
        )
    }
}
