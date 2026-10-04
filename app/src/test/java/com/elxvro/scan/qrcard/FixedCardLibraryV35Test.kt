package com.elxvro.scan.qrcard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedCardLibraryV35Test {
    @Test
    fun freeAndProEachExposeExactlyFiftyCards() {
        assertEquals(50, FixedCardLibrary.free.size)
        assertEquals(50, FixedCardLibrary.pro.size)
    }

    @Test
    fun allOneHundredCardsHaveUniqueIdsAndVisualFingerprints() {
        val all = FixedCardLibrary.free + FixedCardLibrary.pro
        assertEquals(100, all.map { it.id }.toSet().size)
        assertEquals(100, all.map { it.visualFingerprint }.toSet().size)
    }

    @Test
    fun freeAndProLibrariesDoNotReuseTheSameDesign() {
        val free = FixedCardLibrary.free.map { it.visualFingerprint }.toSet()
        val pro = FixedCardLibrary.pro.map { it.visualFingerprint }.toSet()
        assertTrue(free.intersect(pro).isEmpty())
    }

    @Test
    fun libraryUsesAtLeastTenDistinctLayouts() {
        val all = FixedCardLibrary.free + FixedCardLibrary.pro
        assertTrue(all.map { it.layout }.toSet().size >= 10)
    }

    @Test
    fun everyCardHasReadablePalette() {
        (FixedCardLibrary.free + FixedCardLibrary.pro).forEach { card ->
            assertTrue(card.label.isNotBlank())
            assertTrue(card.startArgb != card.endArgb)
            assertTrue(card.accentArgb != card.startArgb)
            assertTrue(card.titleArgb != card.startArgb)
        }
    }
}
