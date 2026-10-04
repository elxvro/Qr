package com.elxvro.scan

data class FreeQrBrandPreset(
    val id: String,
    val label: String,
    val brand: String,
    val title: String,
    val description: String,
    val cta: String,
    val backgroundPresetId: String
)

object FreeQrBrandPresetCatalog {
    val all = listOf(
        FreeQrBrandPreset(
            id = "elxvro_web",
            label = "ELXVRO.com",
            brand = "ELXVRO",
            title = "elxvro.com",
            description = "Dijital dünyanı keşfet",
            cta = "TARA",
            backgroundPresetId = "obsidian_gold_sweep"
        ),
        FreeQrBrandPreset(
            id = "elxvro_scan",
            label = "ELXVRO Scan",
            brand = "ELXVRO",
            title = "ELXVRO Scan",
            description = "QR ve barkodları hızlıca tara",
            cta = "TARA",
            backgroundPresetId = "midnight_blue_frame"
        ),
        FreeQrBrandPreset(
            id = "tara_kesfet",
            label = "Tara • Keşfet",
            brand = "ELXVRO",
            title = "Tara • Keşfet",
            description = "Bağlantıyı aç, içeriğe ulaş",
            cta = "AÇ",
            backgroundPresetId = "royal_violet_rings"
        ),
        FreeQrBrandPreset(
            id = "tek_taramada",
            label = "Tek Taramada",
            brand = "ELXVRO",
            title = "Tek taramada bağlan",
            description = "Hızlı, sade ve doğrudan",
            cta = "TARA",
            backgroundPresetId = "emerald_noir_horizon"
        ),
        FreeQrBrandPreset(
            id = "dijital_kart",
            label = "Dijital Kart",
            brand = "ELXVRO",
            title = "Dijital dünyana açılan kod",
            description = "elxvro.com",
            cta = "KEŞFET",
            backgroundPresetId = "copper_smoke_facets"
        ),
        FreeQrBrandPreset(
            id = "people_places",
            label = "ELXVRO Signature",
            brand = "ELXVRO",
            title = "People • Places • Possibilities",
            description = "Scan. Connect. Discover.",
            cta = "TARA",
            backgroundPresetId = "arctic_navy_diagonal"
        )
    )

    val default: FreeQrBrandPreset = all.first()

    fun find(id: String): FreeQrBrandPreset =
        all.firstOrNull { it.id == id } ?: default
}
