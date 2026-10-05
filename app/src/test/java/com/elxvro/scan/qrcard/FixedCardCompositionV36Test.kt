package com.elxvro.scan.qrcard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedCardCompositionV36Test {
    @Test
    fun everyCompositionKeepsQrTextAndCtaSeparated() {
        listOf(
            1586 to 1000,
            1200 to 1200,
            960 to 1200,
            675 to 1200
        ).forEach { (width, height) ->
            FixedCardLayoutVariant.entries.forEach { variant ->
                val layout = FixedCardLayoutPolicy.resolve(width, height, variant)
                assertFalse(layout.qrRect.overlaps(layout.titleRect))
                assertFalse(layout.qrRect.overlaps(layout.ctaRect))
                assertTrue(layout.qrRect.width >= minOf(width, height) * 0.28f)
                assertTrue(layout.titleRect.width > 0f)
                assertTrue(layout.titleRect.height > 0f)
            }
        }
    }

    @Test
    fun layoutsExposeExplicitTextAlignmentInsteadOfGuessingFromAspect() {
        val alignments = FixedCardLayoutVariant.entries
            .map { FixedCardLayoutPolicy.resolve(1586, 1000, it).textAlignment }
            .toSet()
        assertTrue(alignments.contains(FixedCardTextAlignment.LEFT))
        assertTrue(alignments.contains(FixedCardTextAlignment.CENTER))
        assertTrue(alignments.contains(FixedCardTextAlignment.RIGHT))
    }
}
