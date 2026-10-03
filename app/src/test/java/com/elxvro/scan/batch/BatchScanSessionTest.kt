package com.elxvro.scan.batch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BatchScanSessionTest {
    @Test
    fun addsUniqueCodesNewestFirst() {
        var session = BatchScanSession()
        session = session.add(BatchScanEntry("A", "QR Code", "QR", 100L))
        session = session.add(BatchScanEntry("B", "EAN-13", "Barkod", 200L))

        assertEquals(listOf("B", "A"), session.entries.map { it.value })
    }

    @Test
    fun rejectsDuplicateValueAndFormatWithinSession() {
        var session = BatchScanSession()
        session = session.add(BatchScanEntry("123", "EAN-13", "Barkod", 100L))
        session = session.add(BatchScanEntry("123", "EAN-13", "Barkod", 200L))

        assertEquals(1, session.entries.size)
    }

    @Test
    fun sameValueDifferentFormatIsKept() {
        var session = BatchScanSession()
        session = session.add(BatchScanEntry("123", "QR Code", "QR", 100L))
        session = session.add(BatchScanEntry("123", "EAN-13", "Barkod", 200L))

        assertEquals(2, session.entries.size)
    }

    @Test
    fun toggleSelectionAndSelectAllWork() {
        var session = BatchScanSession()
            .add(BatchScanEntry("A", "QR Code", "QR", 100L))
            .add(BatchScanEntry("B", "EAN-13", "Barkod", 200L))

        session = session.toggleSelection(session.entries.first().id)
        assertEquals(1, session.selectedEntries().size)

        session = session.selectAll()
        assertEquals(2, session.selectedEntries().size)

        session = session.clearSelection()
        assertTrue(session.selectedIds.isEmpty())
    }

    @Test
    fun removeSelectedOnlyRemovesChosenItems() {
        var session = BatchScanSession()
            .add(BatchScanEntry("A", "QR Code", "QR", 100L))
            .add(BatchScanEntry("B", "EAN-13", "Barkod", 200L))

        val id = session.entries.first { it.value == "A" }.id
        session = session.toggleSelection(id).removeSelected()

        assertEquals(listOf("B"), session.entries.map { it.value })
        assertFalse(session.selectedIds.contains(id))
    }
}
