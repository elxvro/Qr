package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualCardLibraryV36Test {
    @Test
    fun eachTierHasTenVisualCategoriesAndFiveCardsPerCategory() {
        listOf(FixedCardTier.FREE, FixedCardTier.PRO).forEach { tier ->
            val cards = FixedCardLibrary.forTier(tier)
            val groups = cards.groupBy { it.category }
            assertEquals(10, groups.size)
            groups.forEach { (_, items) -> assertEquals(5, items.size) }
        }
    }

    @Test
    fun freeAndProUseDifferentVisualSceneFamilies() {
        val freeScenes = FixedCardLibrary.free.map { it.scene }.toSet()
        val proScenes = FixedCardLibrary.pro.map { it.scene }.toSet()
        assertTrue(freeScenes.intersect(proScenes).isEmpty())
    }

    @Test
    fun allCardsCarryActualVisualSceneMetadata() {
        FixedCardLibrary.all.forEach { card ->
            assertTrue(card.category.isNotBlank())
            assertTrue(card.sceneVariant in 0..4)
        }
    }

    @Test
    fun hundredCardsStillHaveUniqueVisualFingerprints() {
        assertEquals(100, FixedCardLibrary.all.map { it.visualFingerprint }.toSet().size)
    }
}
