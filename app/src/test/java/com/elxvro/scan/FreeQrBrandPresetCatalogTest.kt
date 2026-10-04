package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeQrBrandPresetCatalogTest {
    @Test
    fun offersMultipleReadyElxvroBrandCards() {
        assertTrue(FreeQrBrandPresetCatalog.all.size >= 6)
        FreeQrBrandPresetCatalog.all.forEach { preset ->
            assertEquals("ELXVRO", preset.brand)
            assertTrue(preset.title.isNotBlank())
            assertTrue(preset.description.isNotBlank())
            assertTrue(preset.backgroundPresetId.isNotBlank())
        }
    }

    @Test
    fun includesElxvroDotComPromotionalCard() {
        assertTrue(
            FreeQrBrandPresetCatalog.all.any {
                it.title.contains("elxvro.com", ignoreCase = true) ||
                    it.description.contains("elxvro.com", ignoreCase = true)
            }
        )
    }
}
