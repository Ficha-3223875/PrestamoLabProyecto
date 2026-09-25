package com.example.prstamolabctma.util

import androidx.biometric.BiometricManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BiometricHelperTest {

    @Test
    fun isBiometricStatusSuccessDevuelveTrueParaBiometricSuccess() {
        assertTrue(BiometricHelper.isBiometricStatusSuccess(BiometricManager.BIOMETRIC_SUCCESS))
    }

    @Test
    fun isBiometricStatusSuccessDevuelveFalseParaBiometricError() {
        assertFalse(BiometricHelper.isBiometricStatusSuccess(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE))
        assertFalse(BiometricHelper.isBiometricStatusSuccess(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE))
        assertFalse(BiometricHelper.isBiometricStatusSuccess(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED))
    }
}
