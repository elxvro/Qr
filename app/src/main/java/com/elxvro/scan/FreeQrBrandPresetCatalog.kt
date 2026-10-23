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

/**
 * FREE kullanıcıya tek bir ELXVRO tanıtım kartı sunulur.
 * Kart herhangi bir sabit sektör fotoğrafı kullanmaz; ELXVRO marka tasarımıyla oluşturulur.
 */
object FreeQrBrandPresetCatalog {
    val default = FreeQrBrandPreset(
        id = "elxvro_intro",
        label = "ELXVRO Özel Kart",
        brand = "ELXVRO",
        category = "ELXVRO",
        title = "ELXVRO Scan ile Keşfet",
        description = "QR ve barkod araçları, akıllı tarama ve daha fazlası • elxvro.com",
        cta = "ELXVRO.COM",
        backgroundPresetId = ProfessionalCardCatalog.defaultFree.id
    )

    val all: List<FreeQrBrandPreset> = listOf(default)

    fun find(id: String): FreeQrBrandPreset = default
    fun categories(): List<String> = listOf("ELXVRO")
    fun byCategory(category: String): List<FreeQrBrandPreset> = all
}
