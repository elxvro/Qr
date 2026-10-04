package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class QrCardImagePlacementPolicyTest {
    @Test
    fun fixedBackgroundKeepsPhotoInsideItsDesignedPanel() {
        val preset = QrCardDesignCatalog
            .forAspectAndMode(QrCardAspectPreset.WIDE, QrCardBackgroundMode.FIXED_BACKGROUND)
            .single()
        val layout = QrCardV3LayoutPolicy.resolve(1600, 900, preset)

        assertEquals(layout.imageRect, QrCardImagePlacementPolicy.resolve(1600, 900, preset, layout))
        assertNotEquals(LayoutRect(0f, 0f, 1600f, 900f), layout.imageRect)
    }

    @Test
    fun changingBackgroundAlwaysFillsTheWholeCard() {
        val sizes = listOf(
            Triple(QrCardAspectPreset.SQUARE, 1200, 1200),
            Triple(QrCardAspectPreset.WIDE, 1600, 900),
            Triple(QrCardAspectPreset.CARD, 1586, 1000),
            Triple(QrCardAspectPreset.PORTRAIT, 960, 1200),
            Triple(QrCardAspectPreset.STORY, 675, 1200)
        )
        sizes.forEach { (aspect, width, height) ->
            val preset = QrCardDesignCatalog
                .forAspectAndMode(aspect, QrCardBackgroundMode.FULL_BACKGROUND)
                .single()
            val layout = QrCardV3LayoutPolicy.resolve(width, height, preset)

            assertEquals(
                LayoutRect(0f, 0f, width.toFloat(), height.toFloat()),
                QrCardImagePlacementPolicy.resolve(width, height, preset, layout)
            )
        }
    }
}
