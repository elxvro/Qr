package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class QrCardDesignColorPolicyTest {
    @Test
    fun manualCardAccentTextAndQrColorsOverridePresetTheme() {
        val resolved = QrCardDesignColorPolicy.resolve(
            preset = QrCardDesignPreset.CLASSIC_LUXURY,
            cardBackgroundArgb = 0xFF112233.toInt(),
            accentArgb = 0xFFCC8844.toInt(),
            textArgb = 0xFFF0F0F0.toInt(),
            qrForegroundArgb = 0xFF101010.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt()
        )

        assertEquals(0xFF112233.toInt(), resolved.backgroundArgb)
        assertEquals(0xFFCC8844.toInt(), resolved.accentArgb)
        assertEquals(0xFFF0F0F0.toInt(), resolved.titleArgb)
        assertEquals(0xFF101010.toInt(), resolved.qrForegroundArgb)
        assertEquals(0xFFFFFFFF.toInt(), resolved.qrBackgroundArgb)
        assertNotEquals(resolved.titleArgb, resolved.bodyArgb)
    }
}
