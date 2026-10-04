package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardBackgroundModeTest {
    private val supportedAspects = listOf(
        QrCardAspectPreset.SQUARE,
        QrCardAspectPreset.WIDE,
        QrCardAspectPreset.CARD,
        QrCardAspectPreset.PORTRAIT,
        QrCardAspectPreset.STORY
    )

    @Test
    fun exposesExactlyTwoUserFacingCardTypes() {
        assertEquals(
            listOf(QrCardBackgroundMode.FIXED_BACKGROUND, QrCardBackgroundMode.FULL_BACKGROUND),
            QrCardBackgroundMode.entries
        )
        assertEquals("Sabit Arka Plan", QrCardBackgroundMode.FIXED_BACKGROUND.label)
        assertEquals("Değişen Arka Plan", QrCardBackgroundMode.FULL_BACKGROUND.label)
    }

    @Test
    fun everyAspectHasExactlyOnePresetPerCardType() {
        supportedAspects.forEach { aspect ->
            QrCardBackgroundMode.entries.forEach { mode ->
                val matches = QrCardDesignCatalog.forAspectAndMode(aspect, mode)
                assertEquals("${aspect.name}/${mode.name}", 1, matches.size)
                assertEquals(aspect, matches.single().aspectPreset)
                assertEquals(mode, matches.single().backgroundMode)
            }
        }
    }

    @Test
    fun fixedBackgroundUsesOneSharedPremiumThemeAcrossAllRatios() {
        val themes = supportedAspects
            .map { QrCardDesignCatalog.forAspectAndMode(it, QrCardBackgroundMode.FIXED_BACKGROUND).single().theme() }
            .map { listOf(it.backgroundArgb, it.accentArgb, it.titleArgb, it.bodyArgb) }
            .toSet()

        assertEquals(1, themes.size)
    }

    @Test
    fun fullBackgroundTypeUsesFullBleedPhotoAcrossAllRatios() {
        supportedAspects.forEach { aspect ->
            val preset = QrCardDesignCatalog
                .forAspectAndMode(aspect, QrCardBackgroundMode.FULL_BACKGROUND)
                .single()
            assertTrue(preset.fullBleedPhoto)
        }
    }
}
