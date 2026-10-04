package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumFixedBackgroundCatalogTest {
    @Test
    fun exposesFortyEightPremiumFixedBackgrounds() {
        assertEquals(48, PremiumFixedBackgroundCatalog.all.size)
        assertEquals(48, PremiumFixedBackgroundCatalog.all.map { it.id }.toSet().size)
    }

    @Test
    fun everyBackgroundHasValidPremiumPaletteAndPattern() {
        PremiumFixedBackgroundCatalog.all.forEach { preset ->
            assertTrue(preset.id.isNotBlank())
            assertTrue(preset.label.isNotBlank())
            assertTrue(preset.startArgb != preset.endArgb)
            assertTrue(preset.accentArgb != preset.startArgb)
        }
    }

    @Test
    fun catalogUsesMultipleVisualFamiliesAndPatterns() {
        assertTrue(PremiumFixedBackgroundCatalog.all.map { it.family }.toSet().size >= 8)
        assertTrue(PremiumFixedBackgroundCatalog.all.map { it.pattern }.toSet().size >= 6)
    }
}
