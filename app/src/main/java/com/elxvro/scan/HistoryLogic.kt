package com.elxvro.scan

enum class HistoryFilter { ALL, FAVORITES, QR, BARCODE }

object HistoryLogic {
    fun filter(items: List<ScanItem>, filter: HistoryFilter): List<ScanItem> = when (filter) {
        HistoryFilter.ALL -> items
        HistoryFilter.FAVORITES -> items.filter { it.favorite }
        HistoryFilter.QR -> items.filter { it.kind == "QR" }
        HistoryFilter.BARCODE -> items.filter { it.kind == "Barkod" }
    }

    fun toggleFavorite(items: List<ScanItem>, id: String): List<ScanItem> = items.map { item ->
        if (item.id == id) item.copy(favorite = !item.favorite) else item
    }

    fun delete(items: List<ScanItem>, id: String): List<ScanItem> = items.filterNot { it.id == id }
}
