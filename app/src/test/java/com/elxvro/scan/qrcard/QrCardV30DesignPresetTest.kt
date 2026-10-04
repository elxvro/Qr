package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardV30DesignPresetTest {
    @Test
    fun catalogContainsTwoProfessionalDesignsForEachSupportedRatio() {
        val ratios = listOf(
            QrCardAspectPreset.SQUARE,
            QrCardAspectPreset.WIDE,
            QrCardAspectPreset.CARD,
            QrCardAspectPreset.PORTRAIT,
            QrCardAspectPreset.STORY
        )

        assertEquals(10, QrCardDesignPreset.entries.size)
        ratios.forEach { ratio ->
            assertEquals(2, QrCardDesignCatalog.forAspect(ratio).size)
        }
    }

    @Test
    fun everyDesignUsesDistinctTitleAndBodyColorHierarchy() {
        QrCardDesignPreset.entries.forEach { preset ->
            val theme = preset.theme()
            assertNotEquals(preset.name, theme.titleArgb, theme.bodyArgb)
            assertTrue(preset.name, theme.photoOverlayAlpha in 0f..0.72f)
        }
    }

    @Test
    fun storyPresetResolvesToNineBySixteen() {
        assertEquals(9f / 16f, QrCardAspectPreset.STORY.resolve(QrCardTemplate.MINIMAL))
        assertEquals("9:16", QrCardAspectPreset.STORY.label)
    }
}
