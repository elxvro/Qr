package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeQrBrandPresetV35Test {
    @Test
    fun freeGeneratorUsesOnlyElxvroPromotionalCard() {
        assertEquals(1, FreeQrBrandPresetCatalog.all.size)
        val preset = FreeQrBrandPresetCatalog.all.single()
        assertEquals("elxvro_intro", preset.id)
        assertEquals("ELXVRO", preset.category)
        assertTrue(preset.description.contains("elxvro.com", ignoreCase = true))
    }
}
