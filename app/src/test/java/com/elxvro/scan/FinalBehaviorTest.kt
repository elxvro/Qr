package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinalBehaviorTest {
    private val items = listOf(
        ScanItem("1", "https://elxvro.com", "QR Code", "QR", 300L, true),
        ScanItem("2", "8691234567890", "EAN-13", "Barkod", 200L, false),
        ScanItem("3", "Hello World", "QR Code", "QR", 100L, false)
    )

    @Test
    fun history_search_is_case_insensitive_across_fields() {
        assertEquals(listOf("1"), HistoryLogic.searchAndFilter(items, HistoryFilter.ALL, "ELXVRO", true).map { it.id })
        assertEquals(listOf("2"), HistoryLogic.searchAndFilter(items, HistoryFilter.ALL, "ean-13", true).map { it.id })
        assertEquals(listOf("2"), HistoryLogic.searchAndFilter(items, HistoryFilter.ALL, "barkod", true).map { it.id })
    }

    @Test
    fun history_search_respects_filter_and_sort_direction() {
        assertEquals(listOf("3", "1"), HistoryLogic.searchAndFilter(items, HistoryFilter.QR, "", false).map { it.id })
        assertEquals(listOf("1"), HistoryLogic.searchAndFilter(items, HistoryFilter.FAVORITES, "", true).map { it.id })
    }

    @Test
    fun scan_deduplicator_blocks_same_value_inside_cooldown() {
        val gate = ScanDeduplicator(1500L)
        assertTrue(gate.shouldAccept("abc", 1000L))
        assertFalse(gate.shouldAccept("abc", 2000L))
        assertTrue(gate.shouldAccept("abc", 2500L))
    }

    @Test
    fun scan_deduplicator_allows_different_value_immediately() {
        val gate = ScanDeduplicator(1500L)
        assertTrue(gate.shouldAccept("abc", 1000L))
        assertTrue(gate.shouldAccept("xyz", 1100L))
    }
}
