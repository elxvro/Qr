package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Test

class FreeQrBrandPresetV35Test {
    @Test
    fun freeGeneratorOffersFiftyUniqueQrCards() {
        assertEquals(50, FreeQrBrandPresetCatalog.all.size)
        assertEquals(50, FreeQrBrandPresetCatalog.all.map { it.backgroundPresetId }.toSet().size)
    }
}
