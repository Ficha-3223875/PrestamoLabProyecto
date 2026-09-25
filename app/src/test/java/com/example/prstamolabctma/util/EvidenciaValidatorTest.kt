package com.example.prstamolabctma.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenciaValidatorTest {

    @Test
    fun validarMimeImagenValidaDevuelveTrue() {
        assertTrue(EvidenciaValidator.esMimeImagenValida("image/jpeg"))
        assertTrue(EvidenciaValidator.esMimeImagenValida("image/png"))
        assertTrue(EvidenciaValidator.esMimeImagenValida("image/webp"))
    }

    @Test
    fun validarMimeInvalidoDevuelveFalse() {
        assertFalse(EvidenciaValidator.esMimeImagenValida("application/pdf"))
        assertFalse(EvidenciaValidator.esMimeImagenValida("text/plain"))
        assertFalse(EvidenciaValidator.esMimeImagenValida(null))
    }

    @Test
    fun validarTamanoExcedidoDevuelveError() {
        val bytesExcedidos = 6 * 1024 * 1024L
        val res = EvidenciaValidator.validarMetadatos("image/jpeg", bytesExcedidos)
        assertTrue(res.isFailure)
        assertEquals("El archivo excede el tamaño máximo permitido de 5 MB.", res.exceptionOrNull()?.message)
    }

    @Test
    fun validarMetadatosCorrectosDevuelveExito() {
        val bytesValidos = 2 * 1024 * 1024L
        val res = EvidenciaValidator.validarMetadatos("image/jpeg", bytesValidos)
        assertTrue(res.isSuccess)
    }
}
