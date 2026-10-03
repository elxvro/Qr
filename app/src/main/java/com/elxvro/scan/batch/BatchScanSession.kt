package com.elxvro.scan.batch

import java.util.UUID

data class BatchScanEntry(
    val value: String,
    val format: String,
    val kind: String,
    val time: Long,
    val id: String = UUID.randomUUID().toString()
) {
    val signature: String get() = format + "\u0000" + value
}

data class BatchScanSession(
    val entries: List<BatchScanEntry> = emptyList(),
    val selectedIds: Set<String> = emptySet()
) {
    fun add(entry: BatchScanEntry): BatchScanSession {
        if (entries.any { it.signature == entry.signature }) return this
        return copy(entries = listOf(entry) + entries)
    }

    fun contains(value: String, format: String): Boolean =
        entries.any { it.value == value && it.format == format }

    fun toggleSelection(id: String): BatchScanSession {
        if (entries.none { it.id == id }) return this
        val next = if (id in selectedIds) selectedIds - id else selectedIds + id
        return copy(selectedIds = next)
    }

    fun selectAll(): BatchScanSession = copy(selectedIds = entries.mapTo(linkedSetOf()) { it.id })

    fun clearSelection(): BatchScanSession = copy(selectedIds = emptySet())

    fun selectedEntries(): List<BatchScanEntry> =
        entries.filter { it.id in selectedIds }

    fun removeSelected(): BatchScanSession {
        if (selectedIds.isEmpty()) return this
        return copy(
            entries = entries.filterNot { it.id in selectedIds },
            selectedIds = emptySet()
        )
    }

    fun clear(): BatchScanSession = BatchScanSession()
}
