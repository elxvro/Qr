package com.elxvro.scan.pro

import com.elxvro.scan.billing.ProEntitlement
import com.elxvro.scan.qrcard.LogoMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrGenerationPolicyTest {
    @Test
    fun freeExportCannotExceedNineHundredPixels() {
        val options = QrGenerationPolicy.resolve(
            requestedSize = 2048,
            requestedMargin = 1,
            logoMode = LogoMode.ELXVRO,
            entitlement = ProEntitlement.Free
        )

        assertEquals(900, options.size)
    }

    @Test
    fun proExportAllowsTwoThousandFortyEightPixels() {
        val options = QrGenerationPolicy.resolve(
            requestedSize = 2048,
            requestedMargin = 4,
            logoMode = LogoMode.CUSTOM,
            entitlement = ProEntitlement.Pro
        )

        assertEquals(2048, options.size)
    }

    @Test
    fun anyEmbeddedLogoForcesHighErrorCorrectionAndSafeMargin() {
        val options = QrGenerationPolicy.resolve(
            requestedSize = 900,
            requestedMargin = 1,
            logoMode = LogoMode.ELXVRO,
            entitlement = ProEntitlement.Free
        )

        assertTrue(options.highErrorCorrection)
        assertEquals(4, options.margin)
    }

    @Test
    fun noLogoDoesNotForceHighErrorCorrection() {
        val options = QrGenerationPolicy.resolve(
            requestedSize = 1200,
            requestedMargin = 4,
            logoMode = LogoMode.NONE,
            entitlement = ProEntitlement.Pro
        )

        assertFalse(options.highErrorCorrection)
    }
}
