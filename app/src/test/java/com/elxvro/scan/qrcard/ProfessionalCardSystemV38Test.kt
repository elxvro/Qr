package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ProfessionalCardSystemV38Test {
    @Test
    fun freeAndProExposeFiftyDistinctProfessionalCardsEach() {
        assertEquals(50, ProfessionalCardCatalog.free.size)
        assertEquals(50, ProfessionalCardCatalog.pro.size)
        assertEquals(100, ProfessionalCardCatalog.all.map { it.id }.toSet().size)
        assertEquals(100, ProfessionalCardCatalog.all.map { it.visualFingerprint }.toSet().size)
    }

    @Test
    fun eachTierHasTenSceneFamiliesAndFiveRealCompositionsPerCategory() {
        listOf(ProfessionalCardTier.FREE, ProfessionalCardTier.PRO).forEach { tier ->
            val groups = ProfessionalCardCatalog.forTier(tier).groupBy { it.category }
            assertEquals(10, groups.size)
            groups.forEach { (_, cards) ->
                assertEquals(5, cards.size)
                assertEquals(5, cards.map { it.composition }.toSet().size)
                assertEquals(5, cards.map { it.artVariant }.toSet().size)
            }
        }
    }

    @Test
    fun freeAndProNeverReuseTheSameSceneFamily() {
        val freeScenes = ProfessionalCardCatalog.free.map { it.scene }.toSet()
        val proScenes = ProfessionalCardCatalog.pro.map { it.scene }.toSet()
        assertTrue(freeScenes.intersect(proScenes).isEmpty())
    }

    @Test
    fun qrCopyAndCtaStayInsideCardAndNeverCollide() {
        val sizes = listOf(
            1600 to 900,
            1586 to 1000,
            1200 to 1200,
            960 to 1200,
            675 to 1200
        )
        sizes.forEach { (width, height) ->
            ProfessionalCardComposition.entries.forEach { composition ->
                val layout = ProfessionalCardLayoutEngine.resolve(width, height, composition)
                val rects = listOf(
                    layout.panelRect,
                    layout.brandRect,
                    layout.titleRect,
                    layout.bodyRect,
                    layout.qrRect,
                    layout.ctaRect
                )
                rects.forEach { rect ->
                    assertTrue("left out of bounds for $composition", rect.left >= 0f)
                    assertTrue("top out of bounds for $composition", rect.top >= 0f)
                    assertTrue("right out of bounds for $composition", rect.right <= width.toFloat())
                    assertTrue("bottom out of bounds for $composition", rect.bottom <= height.toFloat())
                    assertTrue("invalid width for $composition", rect.width > 0f)
                    assertTrue("invalid height for $composition", rect.height > 0f)
                }
                val short = minOf(width, height).toFloat()
                assertTrue("QR too small for $composition", layout.qrRect.width >= short * 0.29f)
                assertTrue("QR must remain square for $composition", abs(layout.qrRect.width - layout.qrRect.height) < 0.5f)
                assertFalse("QR/title collision for $composition", layout.qrRect.overlaps(layout.titleRect))
                assertFalse("QR/body collision for $composition", layout.qrRect.overlaps(layout.bodyRect))
                assertFalse("QR/CTA collision for $composition", layout.qrRect.overlaps(layout.ctaRect))
                assertFalse("brand/CTA collision for $composition", layout.brandRect.overlaps(layout.ctaRect))
                assertFalse("title/CTA collision for $composition", layout.titleRect.overlaps(layout.ctaRect))
                assertFalse("body/CTA collision for $composition", layout.bodyRect.overlaps(layout.ctaRect))
                assertTrue("CTA must stay below QR for $composition", layout.ctaRect.top >= layout.qrRect.bottom)
                assertTrue(
                    "CTA should remain centered under QR for $composition",
                    abs(
                        ((layout.ctaRect.left + layout.ctaRect.right) / 2f) -
                            ((layout.qrRect.left + layout.qrRect.right) / 2f)
                    ) <= width * 0.08f
                )
            }
        }
    }
}
