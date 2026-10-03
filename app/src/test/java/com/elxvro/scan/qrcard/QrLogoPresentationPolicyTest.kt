package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrLogoPresentationPolicyTest {
    @Test
    fun logoScaleIsClampedToValidatorSafetyLimit() {
        assertEquals(
            QrCardValidator.MAX_LOGO_SCALE,
            QrLogoPresentationPolicy.safeScale(0.50f)
        )
    }

    @Test
    fun premiumLogoPlateIsMoreVisibleThanLegacyPlate() {
        assertTrue(QrLogoPresentationPolicy.BACKING_FACTOR > 1.26f)
        assertTrue(QrLogoPresentationPolicy.CORNER_FACTOR > 0f)
    }
}
