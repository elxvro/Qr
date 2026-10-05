package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedCardCompositionV37Test {
    @Test
    fun fixedCardsUseOnlyCuratedProfessionalCompositions() {
        val allowed = setOf(
            FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT,
            FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT,
            FixedCardLayoutVariant.CENTER_STACK,
            FixedCardLayoutVariant.EDITORIAL,
            FixedCardLayoutVariant.SIGNATURE
        )
        FixedCardLibrary.all.forEach { card ->
            assertTrue(card.layout in allowed)
        }
    }

    @Test
    fun qrIsLargeAndSeparatedFromCopyAndCtaInAllSupportedRatios() {
        val sizes = listOf(
            1586 to 1000,
            1200 to 1200,
            960 to 1200,
            675 to 1200
        )
        val used = FixedCardLibrary.all.map { it.layout }.toSet()
        sizes.forEach { (width, height) ->
            used.forEach { variant ->
                val layout = FixedCardLayoutPolicy.resolve(width, height, variant)
                val short = minOf(width, height).toFloat()
                assertTrue(layout.qrRect.width >= short * 0.36f)
                assertFalse(layout.qrRect.overlaps(layout.titleRect))
                assertFalse(layout.qrRect.overlaps(layout.ctaRect))
                assertFalse(layout.titleRect.overlaps(layout.ctaRect))
            }
        }
    }

    @Test
    fun everyVisualCategoryKeepsOneConsistentComposition() {
        listOf(FixedCardTier.FREE, FixedCardTier.PRO).forEach { tier ->
            FixedCardLibrary.categories(tier).forEach { category ->
                assertEquals(1, FixedCardLibrary.byCategory(tier, category).map { it.layout }.toSet().size)
            }
        }
    }
}
