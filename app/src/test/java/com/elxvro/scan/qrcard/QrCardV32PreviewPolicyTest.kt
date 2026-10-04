package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardV32PreviewPolicyTest {
    @Test
    fun professionalCardAllowsBlankVisibleCopyWithoutLegacyFallback() {
        val model = QrCardModel(
            designPreset = QrCardDesignPreset.CLASSIC_EXECUTIVE,
            payload = "https://example.com",
            brandText = "",
            title = "",
            descriptionText = "",
            ctaText = ""
        )

        val sanitized = QrCardPreviewPolicy.sanitize(model)

        assertTrue(sanitized.brandText.isEmpty())
        assertTrue(sanitized.title.isEmpty())
        assertTrue(sanitized.descriptionText.isEmpty())
        assertTrue(sanitized.ctaText.isEmpty())
    }

    @Test
    fun legacyCardStillGetsItsExistingDefaultTitle() {
        val model = QrCardModel(
            payload = "https://example.com",
            title = ""
        )

        assertEquals("ELXVRO", QrCardPreviewPolicy.sanitize(model).title)
    }
}
