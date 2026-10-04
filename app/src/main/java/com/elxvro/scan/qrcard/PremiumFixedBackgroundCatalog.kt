package com.elxvro.scan.qrcard

enum class PremiumFixedPattern(val label: String) {
    SWEEP("Akış"),
    RINGS("Halka"),
    DIAGONAL("Diyagonal"),
    FRAME("Çerçeve"),
    HORIZON("Ufuk"),
    FACETS("Geometri")
}

data class PremiumFixedBackgroundPreset(
    val id: String,
    val label: String,
    val family: String,
    val startArgb: Int,
    val endArgb: Int,
    val accentArgb: Int,
    val titleArgb: Int,
    val bodyArgb: Int,
    val ctaTextArgb: Int,
    val pattern: PremiumFixedPattern
)

object PremiumFixedBackgroundCatalog {
    private data class Family(
        val id: String,
        val label: String,
        val startArgb: Int,
        val endArgb: Int,
        val accentArgb: Int,
        val titleArgb: Int,
        val bodyArgb: Int,
        val ctaTextArgb: Int
    )

    private val families = listOf(
        Family(
            "obsidian_gold", "Obsidian Gold",
            0xFF07090C.toInt(), 0xFF15191F.toInt(), 0xFFE0B35D.toInt(),
            0xFFFFFFFF.toInt(), 0xFFD8C9AA.toInt(), 0xFF111318.toInt()
        ),
        Family(
            "midnight_blue", "Midnight Blue",
            0xFF07111F.toInt(), 0xFF102A49.toInt(), 0xFF4AA8FF.toInt(),
            0xFFF4FAFF.toInt(), 0xFFB9D4ED.toInt(), 0xFF07111F.toInt()
        ),
        Family(
            "emerald_noir", "Emerald Noir",
            0xFF06110E.toInt(), 0xFF123128.toInt(), 0xFF52D6A1.toInt(),
            0xFFF3FFF9.toInt(), 0xFFB8DCCC.toInt(), 0xFF071510.toInt()
        ),
        Family(
            "burgundy_gold", "Burgundy Gold",
            0xFF14080D.toInt(), 0xFF3C1522.toInt(), 0xFFE4B15A.toInt(),
            0xFFFFF7F2.toInt(), 0xFFE7C7B9.toInt(), 0xFF1A0A0F.toInt()
        ),
        Family(
            "graphite_silver", "Graphite Silver",
            0xFF111317.toInt(), 0xFF2B3038.toInt(), 0xFFC6D0DC.toInt(),
            0xFFFFFFFF.toInt(), 0xFFD0D6DD.toInt(), 0xFF14171B.toInt()
        ),
        Family(
            "royal_violet", "Royal Violet",
            0xFF100A1D.toInt(), 0xFF32205E.toInt(), 0xFFB58CFF.toInt(),
            0xFFFBF7FF.toInt(), 0xFFD7C7EF.toInt(), 0xFF140A22.toInt()
        ),
        Family(
            "copper_smoke", "Copper Smoke",
            0xFF15100D.toInt(), 0xFF3A2A23.toInt(), 0xFFE09A64.toInt(),
            0xFFFFF8F3.toInt(), 0xFFE2C8B6.toInt(), 0xFF1A100B.toInt()
        ),
        Family(
            "arctic_navy", "Arctic Navy",
            0xFF08131A.toInt(), 0xFF173342.toInt(), 0xFF87D7E8.toInt(),
            0xFFF5FDFF.toInt(), 0xFFC8E1E7.toInt(), 0xFF081319.toInt()
        )
    )

    val all: List<PremiumFixedBackgroundPreset> = families.flatMap { family ->
        PremiumFixedPattern.entries.map { pattern ->
            PremiumFixedBackgroundPreset(
                id = "${family.id}_${pattern.name.lowercase()}",
                label = "${family.label} • ${pattern.label}",
                family = family.label,
                startArgb = family.startArgb,
                endArgb = family.endArgb,
                accentArgb = family.accentArgb,
                titleArgb = family.titleArgb,
                bodyArgb = family.bodyArgb,
                ctaTextArgb = family.ctaTextArgb,
                pattern = pattern
            )
        }
    }

    val default: PremiumFixedBackgroundPreset = all.first()

    fun find(id: String?): PremiumFixedBackgroundPreset =
        all.firstOrNull { it.id == id } ?: default
}
