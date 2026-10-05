package com.elxvro.scan

import com.elxvro.scan.qrcard.FixedCardLibrary

data class FreeQrBrandPreset(
    val id: String,
    val label: String,
    val brand: String,
    val category: String,
    val title: String,
    val description: String,
    val cta: String,
    val backgroundPresetId: String
)

object FreeQrBrandPresetCatalog {
    private data class CopySet(
        val title: String,
        val description: String,
        val cta: String
    )

    private val copySets = listOf(
        CopySet("elxvro.com", "Dijital dünyanı keşfet", "TARA"),
        CopySet("ELXVRO Scan", "QR ve barkodları hızlıca tara", "TARA"),
        CopySet("Tara • Keşfet", "Bağlantıyı aç, içeriğe ulaş", "AÇ"),
        CopySet("Tek taramada bağlan", "Hızlı, sade ve doğrudan", "TARA"),
        CopySet("Dijital dünyana açılan kod", "elxvro.com", "KEŞFET"),
        CopySet("People • Places • Possibilities", "Scan. Connect. Discover.", "TARA"),
        CopySet("Bağlantın burada", "Kodu tara ve devam et", "AÇ"),
        CopySet("ELXVRO ile keşfet", "Tek kod, hızlı erişim", "TARA"),
        CopySet("Daha hızlı bağlan", "QR ile anında eriş", "TARA"),
        CopySet("Scan smarter", "Connect faster with ELXVRO", "AÇ")
    )

    val all: List<FreeQrBrandPreset> = FixedCardLibrary.free.mapIndexed { index, card ->
        val copy = copySets[index % copySets.size]
        FreeQrBrandPreset(
            id = "free_brand_${index + 1}",
            label = card.label,
            brand = "ELXVRO",
            category = card.category,
            title = copy.title,
            description = copy.description,
            cta = copy.cta,
            backgroundPresetId = card.id
        )
    }

    val default: FreeQrBrandPreset = all.first()

    fun find(id: String): FreeQrBrandPreset =
        all.firstOrNull { it.id == id } ?: default

    fun categories(): List<String> =
        all.map { it.category }.distinct()

    fun byCategory(category: String): List<FreeQrBrandPreset> =
        all.filter { it.category == category }
}
