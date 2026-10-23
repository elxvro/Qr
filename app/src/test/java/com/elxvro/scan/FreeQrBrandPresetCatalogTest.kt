package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeQrBrandPresetCatalogTest {
    @Test
    fun offersSingleElxvroPromotionalCard() {
        assertEquals(1, FreeQrBrandPresetCatalog.all.size)
        val preset = FreeQrBrandPresetCatalog.default
        assertEquals("ELXVRO", preset.brand)
        assertEquals("ELXVRO Özel Kart", preset.label)
        assertTrue(preset.title.contains("ELXVRO", ignoreCase = true))
        assertTrue(preset.description.contains("elxvro.com", ignoreCase = true))
        assertTrue(preset.cta.contains("ELXVRO", ignoreCase = true))
    }

    @Test
    fun freeCardPromotesElxvroScanAndWebsite() {
        val preset = FreeQrBrandPresetCatalog.default
        assertTrue(preset.title.contains("Scan", ignoreCase = true))
        assertTrue(preset.description.contains("QR", ignoreCase = true))
        assertTrue(preset.description.contains("elxvro.com", ignoreCase = true))
    }
}
