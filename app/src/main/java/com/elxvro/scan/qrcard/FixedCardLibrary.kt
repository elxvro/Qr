package com.elxvro.scan.qrcard

enum class FixedCardTier {
    FREE,
    PRO
}

enum class FixedCardPattern(val label: String) {
    SWEEP("Sweep"),
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

enum class FixedCardVisualScene(val categoryLabel: String) {
    FREE_TRAVEL_MOUNTAINS("Seyahat"),
    FREE_CAFE_TABLE("Kafe"),
    FREE_TECH_NEON("Teknoloji"),
    FREE_CITY_NIGHT("Şehir"),
    FREE_NATURE_FOREST("Doğa"),
    FREE_BUSINESS_DESK("İş"),
    FREE_EVENT_STAGE("Etkinlik"),
    FREE_MINIMAL_INTERIOR("Minimal"),
    FREE_CREATIVE_PET("Yaratıcı"),
    FREE_ABSTRACT_FLOW("Soyut"),

    PRO_LUXURY_COAST("Lüks Seyahat"),
    PRO_FINE_DINING("Fine Dining"),
    PRO_CORPORATE_TOWER("Kurumsal"),
    PRO_FASHION_EDITORIAL("Moda"),
    PRO_NIGHTLIFE_STAGE("Gece"),
    PRO_WELLNESS_RETREAT("Wellness"),
    PRO_HOTEL_RESORT("Otel"),
    PRO_PREMIUM_TECH("Premium Tech"),
    PRO_REAL_ESTATE("Emlak"),
    PRO_BEAUTY_PRODUCT("Beauty")
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
    SIGNATURE,
    HERO_TOP,
    HERO_BOTTOM,
    OFFSET_LEFT,
    OFFSET_RIGHT,
    MAGAZINE
}

data class FixedCardPreset(
    val id: String,
    val label: String,
    val tier: FixedCardTier,
    val category: String,
    val scene: FixedCardVisualScene,
    val sceneVariant: Int,
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
            scene.name,
            sceneVariant,
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
    private data class SceneStyle(
        val scene: FixedCardVisualScene,
        val name: String,
        val start: Int,
        val end: Int,
        val accent: Int,
        val title: Int,
        val body: Int,
        val ctaText: Int
    )

    private val freeStyles = listOf(
        SceneStyle(FixedCardVisualScene.FREE_TRAVEL_MOUNTAINS, "Alpine Escape", 0xFF0A2B49.toInt(), 0xFF4D9ED0.toInt(), 0xFF36A8FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFE2F4FF.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_CAFE_TABLE, "Roast & Co.", 0xFF2B160B.toInt(), 0xFF8D5A31.toInt(), 0xFFF3C57C.toInt(), 0xFFFFF8EF.toInt(), 0xFFF0D8BF.toInt(), 0xFF261409.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_TECH_NEON, "Nova Grid", 0xFF09072A.toInt(), 0xFF26125C.toInt(), 0xFF7E5CFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFD5CEFF.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_CITY_NIGHT, "City Lights", 0xFF071423.toInt(), 0xFF17385F.toInt(), 0xFF00A8FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFC7DDF4.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_NATURE_FOREST, "Green Path", 0xFF082616.toInt(), 0xFF2C7046.toInt(), 0xFF62D982.toInt(), 0xFFFFFFFF.toInt(), 0xFFD7F2DF.toInt(), 0xFF08200F.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_BUSINESS_DESK, "Studio Desk", 0xFFECE7DE.toInt(), 0xFFB7A58F.toInt(), 0xFF183C6B.toInt(), 0xFF101820.toInt(), 0xFF39424B.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_EVENT_STAGE, "Pulse Stage", 0xFF160526.toInt(), 0xFF4F1268.toInt(), 0xFFFF39A5.toInt(), 0xFFFFFFFF.toInt(), 0xFFEBC7E7.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_MINIMAL_INTERIOR, "Soft Space", 0xFFF3ECE1.toInt(), 0xFFDCCAB5.toInt(), 0xFFB98F5D.toInt(), 0xFF191713.toInt(), 0xFF5E554B.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_CREATIVE_PET, "Happy Studio", 0xFFFFE4CA.toInt(), 0xFFFFA974.toInt(), 0xFFFF6B4A.toInt(), 0xFF402017.toInt(), 0xFF6D4336.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.FREE_ABSTRACT_FLOW, "Color Flow", 0xFF07152A.toInt(), 0xFF173F7A.toInt(), 0xFFFF792E.toInt(), 0xFFFFFFFF.toInt(), 0xFFD9E6FF.toInt(), 0xFFFFFFFF.toInt())
    )

    private val proStyles = listOf(
        SceneStyle(FixedCardVisualScene.PRO_LUXURY_COAST, "Azure Reserve", 0xFF061421.toInt(), 0xFF1D5570.toInt(), 0xFFE4B660.toInt(), 0xFFFFFBF1.toInt(), 0xFFE9D9B4.toInt(), 0xFF101820.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_FINE_DINING, "Maison Noire", 0xFF120A07.toInt(), 0xFF4A2615.toInt(), 0xFFE7BA66.toInt(), 0xFFFFFBF6.toInt(), 0xFFEBD3B8.toInt(), 0xFF171009.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_CORPORATE_TOWER, "Apex Black", 0xFF05080D.toInt(), 0xFF162237.toInt(), 0xFFD6B76C.toInt(), 0xFFFFFFFF.toInt(), 0xFFD5D8DE.toInt(), 0xFF0B0E12.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_FASHION_EDITORIAL, "Velora", 0xFF130B0B.toInt(), 0xFF4B2B25.toInt(), 0xFFE5B890.toInt(), 0xFFFFF8F3.toInt(), 0xFFE7CEC0.toInt(), 0xFF1A0E0C.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_NIGHTLIFE_STAGE, "Afterglow", 0xFF090015.toInt(), 0xFF30004A.toInt(), 0xFFFF25D0.toInt(), 0xFFFFFFFF.toInt(), 0xFFF0C7EB.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_WELLNESS_RETREAT, "Seren", 0xFFE7D7B6.toInt(), 0xFF7B9A69.toInt(), 0xFF2C6043.toInt(), 0xFF18241C.toInt(), 0xFF405447.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_HOTEL_RESORT, "Altura", 0xFF0B1E2E.toInt(), 0xFF587F97.toInt(), 0xFFE1B56C.toInt(), 0xFFFFFCF5.toInt(), 0xFFE0D5C5.toInt(), 0xFF101820.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_PREMIUM_TECH, "VYLO", 0xFF040815.toInt(), 0xFF071F50.toInt(), 0xFF356DFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFC8D8FF.toInt(), 0xFFFFFFFF.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_REAL_ESTATE, "Horizon", 0xFF101820.toInt(), 0xFF55758D.toInt(), 0xFFE1B56C.toInt(), 0xFFFFFFFF.toInt(), 0xFFE5E7EA.toInt(), 0xFF101820.toInt()),
        SceneStyle(FixedCardVisualScene.PRO_BEAUTY_PRODUCT, "Lumière", 0xFFF2E8D6.toInt(), 0xFFCAB898.toInt(), 0xFFB88A4A.toInt(), 0xFF2A241B.toInt(), 0xFF625747.toInt(), 0xFFFFFFFF.toInt())
    )

    val free: List<FixedCardPreset> = build(FixedCardTier.FREE, freeStyles, offset = 0)
    val pro: List<FixedCardPreset> = build(FixedCardTier.PRO, proStyles, offset = 7)
    val all: List<FixedCardPreset> = free + pro

    val defaultFree: FixedCardPreset = free.first()
    val defaultPro: FixedCardPreset = pro.first()

    fun find(id: String?): FixedCardPreset =
        all.firstOrNull { it.id == id } ?: defaultPro

    fun forTier(tier: FixedCardTier): List<FixedCardPreset> =
        if (tier == FixedCardTier.FREE) free else pro

    fun categories(tier: FixedCardTier): List<String> =
        forTier(tier).map { it.category }.distinct()

    fun byCategory(tier: FixedCardTier, category: String): List<FixedCardPreset> =
        forTier(tier).filter { it.category == category }

    private fun build(
        tier: FixedCardTier,
        styles: List<SceneStyle>,
        offset: Int
    ): List<FixedCardPreset> = styles.flatMapIndexed { sceneIndex, style ->
        (0 until 5).map { variant ->
            val serial = sceneIndex * 5 + variant
            val pattern = FixedCardPattern.entries[(serial + offset) % FixedCardPattern.entries.size]
            val curatedLayouts = listOf(
                FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT,
                FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT,
                FixedCardLayoutVariant.CENTER_STACK,
                FixedCardLayoutVariant.EDITORIAL,
                FixedCardLayoutVariant.SIGNATURE
            )
            // One disciplined composition per visual family. The five variants
            // change the artwork, not the information architecture.
            val layout = curatedLayouts[(sceneIndex + offset) % curatedLayouts.size]
            FixedCardPreset(
                id = "${tier.name.lowercase()}_${style.scene.name.lowercase()}_${variant + 1}",
                label = "${style.name} ${variant + 1}",
                tier = tier,
                category = style.scene.categoryLabel,
                scene = style.scene,
                sceneVariant = variant,
                startArgb = tune(style.start, variant, lighten = variant % 2 == 0),
                endArgb = tune(style.end, variant, lighten = variant % 2 != 0),
                accentArgb = tune(style.accent, variant, lighten = true),
                titleArgb = style.title,
                bodyArgb = style.body,
                ctaTextArgb = style.ctaText,
                pattern = pattern,
                layout = layout
            )
        }
    }

    private fun tune(color: Int, variant: Int, lighten: Boolean): Int {
        if (variant == 0) return color
        val factor = 0.035f * variant
        fun channel(shift: Int): Int {
            val c = (color shr shift) and 0xFF
            val target = if (lighten) 255 else 0
            return (c + (target - c) * factor).toInt().coerceIn(0, 255)
        }
        return (0xFF shl 24) or
            (channel(16) shl 16) or
            (channel(8) shl 8) or
            channel(0)
    }
}
