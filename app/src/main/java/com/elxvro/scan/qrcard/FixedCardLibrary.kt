package com.elxvro.scan.qrcard

enum class FixedCardTier {
    FREE,
    PRO
}

enum class FixedCardPattern(val label: String) {
    ARC("Arc"),
    GRID("Grid"),
    RINGS("Rings"),
    DIAGONAL("Diagonal"),
    FRAME("Frame"),
    HORIZON("Horizon"),
    FACETS("Facets"),
    ORBIT("Orbit"),
    WAVE("Wave"),
    CUT("Cut")
}

enum class FixedCardLayoutVariant {
    TEXT_LEFT_QR_RIGHT,
    QR_LEFT_TEXT_RIGHT,
    CENTER_STACK,
    TOP_COPY_BOTTOM_QR,
    SPLIT_BAND,
    CORNER_QR,
    FLOATING_QR,
    SIDE_RAIL,
    EDITORIAL,
    SIGNATURE
}

data class FixedCardPreset(
    val id: String,
    val label: String,
    val tier: FixedCardTier,
    val startArgb: Int,
    val endArgb: Int,
    val accentArgb: Int,
    val titleArgb: Int,
    val bodyArgb: Int,
    val ctaTextArgb: Int,
    val pattern: FixedCardPattern,
    val layout: FixedCardLayoutVariant
) {
    val visualFingerprint: String
        get() = listOf(
            startArgb,
            endArgb,
            accentArgb,
            titleArgb,
            bodyArgb,
            ctaTextArgb,
            pattern.name,
            layout.name
        ).joinToString(":")
}

object FixedCardLibrary {
    private data class Palette(
        val id: String,
        val label: String,
        val start: Int,
        val end: Int,
        val accent: Int,
        val title: Int,
        val body: Int,
        val ctaText: Int
    )

    private val freePalettes = listOf(
        Palette("electric_blue", "Electric Blue", 0xFF061222.toInt(), 0xFF0A2F58.toInt(), 0xFF2F9BFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFC4DDF6.toInt(), 0xFF06121F.toInt()),
        Palette("ocean_teal", "Ocean Teal", 0xFF061819.toInt(), 0xFF0B4043.toInt(), 0xFF45D5D0.toInt(), 0xFFF7FFFF.toInt(), 0xFFBDE2E2.toInt(), 0xFF061516.toInt()),
        Palette("violet_night", "Violet Night", 0xFF100B1F.toInt(), 0xFF38236A.toInt(), 0xFFB18BFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFD8C8F4.toInt(), 0xFF130C22.toInt()),
        Palette("ruby_glow", "Ruby Glow", 0xFF19090D.toInt(), 0xFF551A27.toInt(), 0xFFFF6A86.toInt(), 0xFFFFFAFB.toInt(), 0xFFF0C6CF.toInt(), 0xFF1A0910.toInt()),
        Palette("emerald_link", "Emerald Link", 0xFF06130E.toInt(), 0xFF16462F.toInt(), 0xFF4CDF95.toInt(), 0xFFF8FFFB.toInt(), 0xFFC4E7D4.toInt(), 0xFF07140D.toInt()),
        Palette("sunset_orange", "Sunset Orange", 0xFF1C0C07.toInt(), 0xFF5A2A15.toInt(), 0xFFFF9A52.toInt(), 0xFFFFFBF7.toInt(), 0xFFF2CEB5.toInt(), 0xFF1C0D07.toInt()),
        Palette("ice_blue", "Ice Blue", 0xFF06131B.toInt(), 0xFF123848.toInt(), 0xFF79D8F7.toInt(), 0xFFF8FDFF.toInt(), 0xFFC6E5F0.toInt(), 0xFF07151C.toInt()),
        Palette("magenta_flux", "Magenta Flux", 0xFF19091B.toInt(), 0xFF51205A.toInt(), 0xFFF06DFF.toInt(), 0xFFFFF8FF.toInt(), 0xFFEBC6EE.toInt(), 0xFF1B0A1D.toInt()),
        Palette("lime_signal", "Lime Signal", 0xFF0D1508.toInt(), 0xFF2F491C.toInt(), 0xFFA6E65F.toInt(), 0xFFFBFFF7.toInt(), 0xFFD8EAC7.toInt(), 0xFF0D1508.toInt()),
        Palette("steel_blue", "Steel Blue", 0xFF0A111A.toInt(), 0xFF253A54.toInt(), 0xFF80AEE8.toInt(), 0xFFF8FBFF.toInt(), 0xFFC9D6E7.toInt(), 0xFF0B121B.toInt())
    )

    private val proPalettes = listOf(
        Palette("obsidian_gold", "Obsidian Gold", 0xFF050608.toInt(), 0xFF16191F.toInt(), 0xFFE4B660.toInt(), 0xFFFFFEFA.toInt(), 0xFFD8C8A8.toInt(), 0xFF12100A.toInt()),
        Palette("carbon_platinum", "Carbon Platinum", 0xFF090A0C.toInt(), 0xFF252A30.toInt(), 0xFFD6DEE8.toInt(), 0xFFFFFFFF.toInt(), 0xFFD4D9E0.toInt(), 0xFF111418.toInt()),
        Palette("imperial_burgundy", "Imperial Burgundy", 0xFF13070B.toInt(), 0xFF3C111D.toInt(), 0xFFE6B967.toInt(), 0xFFFFFAF7.toInt(), 0xFFE8C7B9.toInt(), 0xFF180B0D.toInt()),
        Palette("royal_emerald", "Royal Emerald", 0xFF04100B.toInt(), 0xFF123328.toInt(), 0xFF72D6A8.toInt(), 0xFFF8FFFB.toInt(), 0xFFC4E2D3.toInt(), 0xFF07130E.toInt()),
        Palette("midnight_sapphire", "Midnight Sapphire", 0xFF050A14.toInt(), 0xFF112A52.toInt(), 0xFF73A9FF.toInt(), 0xFFF8FBFF.toInt(), 0xFFC5D5EE.toInt(), 0xFF07101E.toInt()),
        Palette("royal_amethyst", "Royal Amethyst", 0xFF0D0718.toInt(), 0xFF2D184F.toInt(), 0xFFC19BFF.toInt(), 0xFFFFFAFF.toInt(), 0xFFDCCBED.toInt(), 0xFF12091D.toInt()),
        Palette("copper_noir", "Copper Noir", 0xFF120B08.toInt(), 0xFF35241D.toInt(), 0xFFE3A06E.toInt(), 0xFFFFFBF7.toInt(), 0xFFE4C9B9.toInt(), 0xFF160D09.toInt()),
        Palette("arctic_graphite", "Arctic Graphite", 0xFF081014.toInt(), 0xFF20343C.toInt(), 0xFFA8DDE6.toInt(), 0xFFF8FEFF.toInt(), 0xFFD1E4E7.toInt(), 0xFF091216.toInt()),
        Palette("champagne_noir", "Champagne Noir", 0xFF100D08.toInt(), 0xFF332B1D.toInt(), 0xFFF0D18B.toInt(), 0xFFFFFCF6.toInt(), 0xFFE7D9BB.toInt(), 0xFF17130B.toInt()),
        Palette("black_crimson", "Black Crimson", 0xFF0D0608.toInt(), 0xFF301016.toInt(), 0xFFFF6D7F.toInt(), 0xFFFFFAFB.toInt(), 0xFFEBC8CE.toInt(), 0xFF16090C.toInt())
    )

    val free: List<FixedCardPreset> = build(FixedCardTier.FREE, freePalettes, offset = 0)
    val pro: List<FixedCardPreset> = build(FixedCardTier.PRO, proPalettes, offset = 5)
    val all: List<FixedCardPreset> = free + pro

    val defaultFree: FixedCardPreset = free.first()
    val defaultPro: FixedCardPreset = pro.first()

    fun find(id: String?): FixedCardPreset =
        all.firstOrNull { it.id == id } ?: defaultPro

    fun forTier(tier: FixedCardTier): List<FixedCardPreset> =
        if (tier == FixedCardTier.FREE) free else pro

    private fun build(
        tier: FixedCardTier,
        palettes: List<Palette>,
        offset: Int
    ): List<FixedCardPreset> = palettes.flatMapIndexed { paletteIndex, palette ->
        (0 until 5).map { variantIndex ->
            val serial = paletteIndex * 5 + variantIndex
            val pattern = FixedCardPattern.entries[(serial + offset) % FixedCardPattern.entries.size]
            val layout = FixedCardLayoutVariant.entries[
                (paletteIndex + variantIndex * 3 + offset) % FixedCardLayoutVariant.entries.size
            ]
            FixedCardPreset(
                id = "${tier.name.lowercase()}_${palette.id}_${variantIndex + 1}",
                label = "${palette.label} ${variantIndex + 1}",
                tier = tier,
                startArgb = palette.start,
                endArgb = palette.end,
                accentArgb = palette.accent,
                titleArgb = palette.title,
                bodyArgb = palette.body,
                ctaTextArgb = palette.ctaText,
                pattern = pattern,
                layout = layout
            )
        }
    }
}
