package com.elxvro.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryLogicTest {
    private val items = listOf(
        ScanItem("1", "https://elxvro.com", "QR Code", "QR", 3L, false),
        ScanItem("2", "8691234567890", "EAN-13", "Barkod", 2L, true),
        ScanItem("3", "WIFI:S:ELXVRO;;", "QR Code", "QR", 1L, true)
    )

    @Test fun favorites_returns_only_favorites() {
        val result = HistoryLogic.filter(items, HistoryFilter.FAVORITES)
        assertEquals(listOf("2", "3"), result.map { it.id })
    }

    @Test fun qr_filter_returns_only_qr_items() {
        val result = HistoryLogic.filter(items, HistoryFilter.QR)
        assertEquals(listOf("1", "3"), result.map { it.id })
    }

    @Test fun toggling_favorite_changes_only_target_item() {
        val result = HistoryLogic.toggleFavorite(items, "1")
        assertTrue(result.first { it.id == "1" }.favorite)
        assertTrue(result.first { it.id == "2" }.favorite)
        assertTrue(result.first { it.id == "3" }.favorite)
    }

    @Test fun deleting_item_keeps_other_items() {
        val result = HistoryLogic.delete(items, "2")
        assertEquals(listOf("1", "3"), result.map { it.id })
        assertFalse(result.any { it.id == "2" })
    }
}
