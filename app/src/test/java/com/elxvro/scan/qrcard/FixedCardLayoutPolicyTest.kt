package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedCardLayoutPolicyTest {
    @Test
    fun everyLayoutStaysInsideLandscapeCardWithoutQrTextOverlap() {
        FixedCardLayoutVariant.entries.forEach { variant ->
            val layout = FixedCardLayoutPolicy.resolve(1586, 1000, variant)
            assertTrue(layout.brandRect.inside(1586, 1000))
            assertTrue(layout.titleRect.inside(1586, 1000))
            assertTrue(layout.qrRect.inside(1586, 1000))
            assertTrue(layout.ctaRect.inside(1586, 1000))
            assertFalse(layout.qrRect.overlaps(layout.titleRect))
        }
    }

    @Test
    fun everyLayoutStaysInsidePortraitCardWithoutQrTextOverlap() {
        FixedCardLayoutVariant.entries.forEach { variant ->
            val layout = FixedCardLayoutPolicy.resolve(900, 1600, variant)
            assertTrue(layout.brandRect.inside(900, 1600))
            assertTrue(layout.titleRect.inside(900, 1600))
            assertTrue(layout.qrRect.inside(900, 1600))
            assertTrue(layout.ctaRect.inside(900, 1600))
            assertFalse(layout.qrRect.overlaps(layout.titleRect))
        }
    }

    @Test
    fun layoutsAreGeometricallyDistinct() {
        val fingerprints = FixedCardLayoutVariant.entries.map { variant ->
            FixedCardLayoutPolicy.resolve(1200, 1200, variant).toString()
        }.toSet()
        assertEquals(FixedCardLayoutVariant.entries.size, fingerprints.size)
    }

    private fun LayoutRect.inside(width: Int, height: Int): Boolean =
        left >= 0f && top >= 0f && right <= width && bottom <= height &&
            this.width > 0f && this.height > 0f
}
