package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Test

class QrCardTemplateVisualDefaultsTest {
    @Test
    fun templatesUseDistinctAccentColors() {
        val accents = QrCardTemplate.entries.map { it.defaults().accentArgb }

        assertEquals(QrCardTemplate.entries.size, accents.toSet().size)
    }
}
