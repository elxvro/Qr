package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCardReferenceLayoutPolicyTest {
    private val aspects = listOf(
        QrCardAspectPreset.SQUARE,
        QrCardAspectPreset.WIDE,
        QrCardAspectPreset.CARD,
        QrCardAspectPreset.PORTRAIT,
        QrCardAspectPreset.STORY
    )

    @Test
    fun everyAspectUsesSingleReferenceCompositionWithoutOverlap() {
        aspects.forEach { aspect ->
            val ratio = aspect.resolve(QrCardTemplate.MINIMAL)
            val width = if (ratio >= 1f) 1600 else (1600 * ratio).toInt()
            val height = if (ratio >= 1f) (1600 / ratio).toInt() else 1600
            val layout = QrCardReferenceLayoutPolicy.resolve(width, height, aspect)

            assertTrue(layout.brandRect.isInside(width, height))
            assertTrue(layout.titleRect.isInside(width, height))
            assertTrue(layout.qrRect.isInside(width, height))
            assertTrue(layout.ctaRect.isInside(width, height))
            assertFalse(layout.qrRect.overlaps(layout.titleRect))
            assertFalse(layout.qrRect.overlaps(layout.ctaRect))
        }
    }

    @Test
    fun landscapeRatiosKeepTextLeftAndQrRight() {
        listOf(QrCardAspectPreset.WIDE, QrCardAspectPreset.CARD).forEach { aspect ->
            val ratio = aspect.resolve(QrCardTemplate.MINIMAL)
            val width = 1600
            val height = (1600 / ratio).toInt()
            val layout = QrCardReferenceLayoutPolicy.resolve(width, height, aspect)

            assertTrue(layout.titleRect.right <= layout.qrRect.left)
        }
    }

    @Test
    fun portraitRatiosKeepTextAboveQr() {
        listOf(QrCardAspectPreset.PORTRAIT, QrCardAspectPreset.STORY).forEach { aspect ->
            val ratio = aspect.resolve(QrCardTemplate.MINIMAL)
            val height = 1600
            val width = (1600 * ratio).toInt()
            val layout = QrCardReferenceLayoutPolicy.resolve(width, height, aspect)

            assertTrue(layout.titleRect.bottom <= layout.qrRect.top)
        }
    }

    private fun LayoutRect.isInside(width: Int, height: Int): Boolean =
        left >= 0f && top >= 0f && right <= width && bottom <= height && this.width > 0f && this.height > 0f
}
