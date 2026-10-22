package com.elxvro.scan

import com.elxvro.scan.qrcard.ProfessionalCardCatalog

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
        CopySet("Randevunu Oluştur", "Kuaför ve bakım hizmetlerimizi keşfet", "TARA"),
        CopySet("Menüyü Keşfet", "Kahve, tatlı ve günün lezzetlerine göz at", "TARA"),
        CopySet("Lezzetlerimizi Keşfet", "Menü, rezervasyon ve iletişim bilgilerine ulaş", "TARA"),
        CopySet("Günün Tazeleri", "Fırından çıkan ürünleri ve özel siparişleri gör", "TARA"),
        CopySet("Bakım & Güzellik", "Hizmetleri incele ve randevu bilgilerine ulaş", "TARA"),
        CopySet("Yeni Koleksiyon", "Ürünleri, kampanyaları ve mağaza bilgilerini keşfet", "TARA"),
        CopySet("Antrenmana Başla", "Program, üyelik ve iletişim bilgilerine ulaş", "TARA"),
        CopySet("Randevu & Bilgi", "Hizmetler ve iletişim detayları için tara", "TARA"),
        CopySet("Portföyü İncele", "İlanları, proje bilgilerini ve iletişimi görüntüle", "TARA"),
        CopySet("Araçlarımızı Keşfet", "Araç, hizmet ve iletişim seçeneklerine ulaş", "TARA")
    )

    val all: List<FreeQrBrandPreset> = ProfessionalCardCatalog.free.mapIndexed { index, card ->
        val copy = copySets[(index / 5).coerceIn(0, copySets.lastIndex)]
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
