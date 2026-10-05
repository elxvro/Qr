package com.elxvro.scan.qrcard

import kotlin.math.min

enum class ProfessionalCardTier { FREE, PRO }

enum class ProfessionalCardScene(val categoryLabel: String) {
    FREE_ALPINE("Seyahat"),
    FREE_COFFEE("Kafe"),
    FREE_NEON_TECH("Teknoloji"),
    FREE_METRO("Şehir"),
    FREE_BOTANICAL("Doğa"),
    FREE_STUDIO("İş"),
    FREE_STAGE("Etkinlik"),
    FREE_INTERIOR("Minimal"),
    FREE_CREATIVE("Yaratıcı"),
    FREE_FLOW("Soyut"),

    PRO_COAST("Lüks Seyahat"),
    PRO_DINING("Fine Dining"),
    PRO_ARCHITECTURE("Kurumsal"),
    PRO_FASHION("Moda"),
    PRO_NIGHT("Gece"),
    PRO_WELLNESS("Wellness"),
    PRO_RESORT("Otel"),
    PRO_TECH_LAUNCH("Premium Tech"),
    PRO_PROPERTY("Emlak"),
    PRO_BEAUTY("Beauty")
}

enum class ProfessionalCardComposition(val label: String) {
    SPLIT_RIGHT("Split"),
    SPLIT_LEFT("Reverse"),
    CENTER_POSTER("Poster"),
    BOTTOM_DOCK("Dock"),
    TOP_FEATURE("Feature"),
    DIAGONAL_EDITORIAL("Editorial"),
    SIDE_RAIL("Rail"),
    FLOATING_CORNER("Corner"),
    HERO_BAND("Hero"),
    SIGNATURE("Signature")
}

enum class ProfessionalPanelStyle { GLASS, SOLID, SOFT, OUTLINE, NONE }
enum class ProfessionalQrPlateStyle { CLEAN, BORDERED, INK_EDGE, GLASS }

data class ProfessionalCardPreset(
    val id: String,
    val label: String,
    val tier: ProfessionalCardTier,
    val category: String,
    val scene: ProfessionalCardScene,
    val artVariant: Int,
    val composition: ProfessionalCardComposition,
    val backgroundStartArgb: Int,
    val backgroundEndArgb: Int,
    val accentArgb: Int,
    val titleArgb: Int,
    val bodyArgb: Int,
    val ctaTextArgb: Int,
    val panelStyle: ProfessionalPanelStyle,
    val qrPlateStyle: ProfessionalQrPlateStyle
) {
    val visualFingerprint: String
        get() = listOf(
            scene.name,
            artVariant,
            composition.name,
            panelStyle.name,
            qrPlateStyle.name,
            backgroundStartArgb,
            backgroundEndArgb,
            accentArgb
        ).joinToString(":")
}

object ProfessionalCardCatalog {
    private data class SceneStyle(
        val scene: ProfessionalCardScene,
        val name: String,
        val start: Int,
        val end: Int,
        val accent: Int,
        val title: Int,
        val body: Int,
        val ctaText: Int,
        val panel: ProfessionalPanelStyle,
        val qrPlate: ProfessionalQrPlateStyle
    )

    private val freeStyles = listOf(
        SceneStyle(ProfessionalCardScene.FREE_ALPINE, "Alpine Route", 0xFF0D2D46.toInt(), 0xFF6DA8C7.toInt(), 0xFFFFD166.toInt(), 0xFFFFFFFF.toInt(), 0xFFE4F2F7.toInt(), 0xFF111820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_COFFEE, "Roastery", 0xFF2A170F.toInt(), 0xFF9A6A45.toInt(), 0xFFF3C982.toInt(), 0xFFFFF8EF.toInt(), 0xFFF2DDC6.toInt(), 0xFF2A170F.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_NEON_TECH, "Nova Circuit", 0xFF080A24.toInt(), 0xFF342B75.toInt(), 0xFF62D7FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFD8E7FF.toInt(), 0xFF07121C.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_METRO, "Metro Afterdark", 0xFF071827.toInt(), 0xFF315470.toInt(), 0xFFFFB454.toInt(), 0xFFFFFFFF.toInt(), 0xFFD7E5F0.toInt(), 0xFF111820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.FREE_BOTANICAL, "Field Notes", 0xFF0D3020.toInt(), 0xFF6D9A73.toInt(), 0xFFE9D17B.toInt(), 0xFFFFFFFF.toInt(), 0xFFE1F0E3.toInt(), 0xFF173020.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_STUDIO, "Workday", 0xFFE9E4DB.toInt(), 0xFFBBAF9F.toInt(), 0xFF0E4D92.toInt(), 0xFF141A21.toInt(), 0xFF48515B.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_STAGE, "Live Signal", 0xFF170521.toInt(), 0xFF601D70.toInt(), 0xFFFF4E9A.toInt(), 0xFFFFFFFF.toInt(), 0xFFF1CDE4.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_INTERIOR, "Soft Form", 0xFFF3EEE7.toInt(), 0xFFD8C7B5.toInt(), 0xFF9A6D43.toInt(), 0xFF1D1B18.toInt(), 0xFF5A524A.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_CREATIVE, "Play Studio", 0xFFFFE2CA.toInt(), 0xFFFFA36D.toInt(), 0xFFE94D3D.toInt(), 0xFF402019.toInt(), 0xFF6A4034.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_FLOW, "Motion Field", 0xFF06172B.toInt(), 0xFF174982.toInt(), 0xFFFF7A35.toInt(), 0xFFFFFFFF.toInt(), 0xFFDCEAFF.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE)
    )

    private val proStyles = listOf(
        SceneStyle(ProfessionalCardScene.PRO_COAST, "Azure Reserve", 0xFF041824.toInt(), 0xFF316B82.toInt(), 0xFFE7BE6B.toInt(), 0xFFFFFCF5.toInt(), 0xFFEADDBB.toInt(), 0xFF101820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_DINING, "Maison Noire", 0xFF130B08.toInt(), 0xFF4A2819.toInt(), 0xFFE9BC69.toInt(), 0xFFFFFBF5.toInt(), 0xFFEBD3B8.toInt(), 0xFF171009.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_ARCHITECTURE, "Apex House", 0xFF05080D.toInt(), 0xFF1A293D.toInt(), 0xFFD9B96E.toInt(), 0xFFFFFFFF.toInt(), 0xFFD6DBE2.toInt(), 0xFF0B0E12.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_FASHION, "Velora Edit", 0xFF170E0E.toInt(), 0xFF56342D.toInt(), 0xFFE8B992.toInt(), 0xFFFFF9F5.toInt(), 0xFFE9D0C3.toInt(), 0xFF1A0E0C.toInt(), ProfessionalPanelStyle.OUTLINE, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.PRO_NIGHT, "Afterglow", 0xFF090014.toInt(), 0xFF32004D.toInt(), 0xFFFF3BCB.toInt(), 0xFFFFFFFF.toInt(), 0xFFF1C8EA.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.PRO_WELLNESS, "Seren", 0xFFE8D9BA.toInt(), 0xFF78936B.toInt(), 0xFF2D5E43.toInt(), 0xFF17231C.toInt(), 0xFF3F5346.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.PRO_RESORT, "Altura", 0xFF0A1D2C.toInt(), 0xFF5F8398.toInt(), 0xFFE3B871.toInt(), 0xFFFFFCF6.toInt(), 0xFFE1D5C5.toInt(), 0xFF101820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_TECH_LAUNCH, "VYLO", 0xFF030815.toInt(), 0xFF08265C.toInt(), 0xFF4C7BFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFCAD9FF.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.PRO_PROPERTY, "Horizon Estate", 0xFF101820.toInt(), 0xFF5A788C.toInt(), 0xFFE1B66B.toInt(), 0xFFFFFFFF.toInt(), 0xFFE6E8EA.toInt(), 0xFF101820.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_BEAUTY, "Lumière", 0xFFF1E7D6.toInt(), 0xFFCDBB9C.toInt(), 0xFFB9894D.toInt(), 0xFF2B241C.toInt(), 0xFF625647.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN)
    )

    private val variantNames = listOf("Editorial", "Campaign", "Studio", "Signature", "Poster")
    private val compositions = ProfessionalCardComposition.entries

    val free: List<ProfessionalCardPreset> = build(ProfessionalCardTier.FREE, freeStyles, 0)
    val pro: List<ProfessionalCardPreset> = build(ProfessionalCardTier.PRO, proStyles, 3)
    val all: List<ProfessionalCardPreset> = free + pro

    val defaultFree: ProfessionalCardPreset = free.first()
    val defaultPro: ProfessionalCardPreset = pro.first()

    fun find(id: String?): ProfessionalCardPreset =
        all.firstOrNull { it.id == id } ?: defaultPro

    fun forTier(tier: ProfessionalCardTier): List<ProfessionalCardPreset> =
        if (tier == ProfessionalCardTier.FREE) free else pro

    fun categories(tier: ProfessionalCardTier): List<String> =
        forTier(tier).map { it.category }.distinct()

    fun byCategory(tier: ProfessionalCardTier, category: String): List<ProfessionalCardPreset> =
        forTier(tier).filter { it.category == category }

    private fun build(
        tier: ProfessionalCardTier,
        styles: List<SceneStyle>,
        compositionOffset: Int
    ): List<ProfessionalCardPreset> = styles.flatMapIndexed { sceneIndex, style ->
        (0 until 5).map { variant ->
            val composition = compositions[(sceneIndex * 2 + variant + compositionOffset) % compositions.size]
            val panelStyle = when ((variant + sceneIndex + compositionOffset) % 5) {
                0 -> style.panel
                1 -> ProfessionalPanelStyle.GLASS
                2 -> ProfessionalPanelStyle.SOFT
                3 -> ProfessionalPanelStyle.OUTLINE
                else -> if (tier == ProfessionalCardTier.PRO) ProfessionalPanelStyle.SOLID else style.panel
            }
            val qrPlateStyle = when ((variant + sceneIndex) % 4) {
                0 -> style.qrPlate
                1 -> ProfessionalQrPlateStyle.BORDERED
                2 -> ProfessionalQrPlateStyle.CLEAN
                else -> if (tier == ProfessionalCardTier.PRO) ProfessionalQrPlateStyle.INK_EDGE else ProfessionalQrPlateStyle.GLASS
            }
            ProfessionalCardPreset(
                id = "professional_${tier.name.lowercase()}_${style.scene.name.lowercase()}_${variant + 1}",
                label = "${style.name} • ${variantNames[variant]}",
                tier = tier,
                category = style.scene.categoryLabel,
                scene = style.scene,
                artVariant = variant,
                composition = composition,
                backgroundStartArgb = style.start,
                backgroundEndArgb = style.end,
                accentArgb = style.accent,
                titleArgb = style.title,
                bodyArgb = style.body,
                ctaTextArgb = style.ctaText,
                panelStyle = panelStyle,
                qrPlateStyle = qrPlateStyle
            )
        }
    }
}

data class ProfessionalCardLayout(
    val panelRect: LayoutRect,
    val brandRect: LayoutRect,
    val titleRect: LayoutRect,
    val bodyRect: LayoutRect,
    val qrRect: LayoutRect,
    val ctaRect: LayoutRect,
    val textAlignment: FixedCardTextAlignment
)

object ProfessionalCardLayoutEngine {
    fun resolve(width: Int, height: Int, composition: ProfessionalCardComposition): ProfessionalCardLayout {
        require(width > 0 && height > 0)
        return (if (width.toFloat() / height >= 1.12f) {
            landscape(width, height, composition)
        } else {
            portrait(width, height, composition)
        }).normalizeCta(width.toFloat(), height.toFloat())
    }

    private fun landscape(width: Int, height: Int, c: ProfessionalCardComposition): ProfessionalCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val s = min(w, h)
        fun rect(l: Float, t: Float, r: Float, b: Float) = LayoutRect(w*l, h*t, w*r, h*b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = s * side
            return LayoutRect(w*cx-d/2f, h*cy-d/2f, w*cx+d/2f, h*cy+d/2f)
        }
        return when (c) {
            ProfessionalCardComposition.SPLIT_RIGHT -> ProfessionalCardLayout(rect(.045f,.07f,.955f,.93f), rect(.08f,.13f,.50f,.21f), rect(.08f,.25f,.50f,.43f), rect(.08f,.46f,.50f,.61f), qr(.77f,.43f,.40f), rect(.63f,.70f,.91f,.82f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SPLIT_LEFT -> ProfessionalCardLayout(rect(.045f,.07f,.955f,.93f), rect(.50f,.13f,.92f,.21f), rect(.50f,.25f,.92f,.43f), rect(.50f,.46f,.92f,.61f), qr(.23f,.43f,.40f), rect(.09f,.70f,.37f,.82f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.CENTER_POSTER -> ProfessionalCardLayout(rect(.10f,.07f,.90f,.93f), rect(.18f,.10f,.82f,.17f), rect(.18f,.20f,.82f,.34f), rect(.20f,.35f,.80f,.45f), qr(.50f,.64f,.34f), rect(.35f,.84f,.65f,.92f), FixedCardTextAlignment.CENTER)
            ProfessionalCardComposition.BOTTOM_DOCK -> ProfessionalCardLayout(rect(.05f,.08f,.95f,.92f), rect(.08f,.12f,.60f,.20f), rect(.08f,.23f,.62f,.40f), rect(.08f,.42f,.60f,.55f), qr(.78f,.63f,.35f), rect(.08f,.70f,.42f,.82f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.TOP_FEATURE -> ProfessionalCardLayout(rect(.05f,.08f,.95f,.92f), rect(.08f,.13f,.52f,.21f), rect(.08f,.25f,.52f,.42f), rect(.08f,.45f,.52f,.60f), qr(.78f,.34f,.34f), rect(.62f,.60f,.92f,.72f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.DIAGONAL_EDITORIAL -> ProfessionalCardLayout(rect(.045f,.06f,.955f,.94f), rect(.08f,.12f,.50f,.20f), rect(.08f,.23f,.53f,.41f), rect(.08f,.44f,.50f,.58f), qr(.76f,.56f,.36f), rect(.08f,.68f,.38f,.80f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SIDE_RAIL -> ProfessionalCardLayout(rect(.11f,.07f,.94f,.93f), rect(.16f,.13f,.52f,.21f), rect(.16f,.25f,.54f,.43f), rect(.16f,.46f,.54f,.60f), qr(.77f,.45f,.38f), rect(.16f,.70f,.46f,.82f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.FLOATING_CORNER -> ProfessionalCardLayout(rect(.05f,.07f,.95f,.93f), rect(.08f,.12f,.55f,.20f), rect(.08f,.23f,.58f,.41f), rect(.08f,.44f,.56f,.58f), qr(.78f,.58f,.34f), rect(.61f,.78f,.92f,.89f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.HERO_BAND -> ProfessionalCardLayout(rect(.04f,.50f,.96f,.94f), rect(.08f,.56f,.48f,.63f), rect(.08f,.65f,.50f,.77f), rect(.08f,.78f,.50f,.88f), qr(.77f,.69f,.29f), rect(.60f,.86f,.92f,.93f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SIGNATURE -> ProfessionalCardLayout(rect(.06f,.08f,.94f,.92f), rect(.08f,.13f,.47f,.21f), rect(.08f,.25f,.49f,.43f), rect(.08f,.46f,.49f,.60f), qr(.75f,.44f,.39f), rect(.59f,.70f,.91f,.82f), FixedCardTextAlignment.LEFT)
        }
    }

    private fun ProfessionalCardLayout.normalizeCta(width: Float, height: Float): ProfessionalCardLayout {
        val ctaWidth = (qrRect.width * 0.92f)
            .coerceAtMost(width * 0.34f)
            .coerceAtLeast(width * 0.18f)
        val ctaHeight = (qrRect.height * 0.20f)
            .coerceIn(height * 0.052f, height * 0.085f)
        val gap = (height * 0.022f).coerceAtLeast(10f)
        val desiredLeft = (qrRect.left + qrRect.right - ctaWidth) / 2f
        val horizontalPadding = width * 0.03f
        val left = desiredLeft.coerceIn(
            horizontalPadding,
            width - horizontalPadding - ctaWidth
        )
        var top = qrRect.bottom + gap
        var bottom = top + ctaHeight
        val maxBottom = minOf(height * 0.94f, panelRect.bottom - height * 0.015f)
        if (bottom > maxBottom) {
            bottom = maxBottom
            top = bottom - ctaHeight
        }
        return copy(
            ctaRect = LayoutRect(
                left = left,
                top = top,
                right = left + ctaWidth,
                bottom = bottom
            )
        )
    }

    private fun portrait(width: Int, height: Int, c: ProfessionalCardComposition): ProfessionalCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val s = min(w, h)
        fun rect(l: Float, t: Float, r: Float, b: Float) = LayoutRect(w*l, h*t, w*r, h*b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = s * side
            return LayoutRect(w*cx-d/2f, h*cy-d/2f, w*cx+d/2f, h*cy+d/2f)
        }
        return when (c) {
            ProfessionalCardComposition.TOP_FEATURE, ProfessionalCardComposition.SPLIT_LEFT -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.12f,.44f,.88f,.50f), rect(.12f,.52f,.88f,.63f), rect(.12f,.64f,.88f,.72f), qr(.50f,.24f,.42f), rect(.24f,.80f,.76f,.87f), FixedCardTextAlignment.CENTER)
            ProfessionalCardComposition.HERO_BAND, ProfessionalCardComposition.BOTTOM_DOCK -> ProfessionalCardLayout(rect(.07f,.46f,.93f,.95f), rect(.12f,.51f,.88f,.57f), rect(.12f,.59f,.88f,.69f), rect(.12f,.70f,.88f,.77f), qr(.50f,.24f,.38f), rect(.24f,.84f,.76f,.90f), FixedCardTextAlignment.CENTER)
            ProfessionalCardComposition.FLOATING_CORNER, ProfessionalCardComposition.DIAGONAL_EDITORIAL -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.10f,.09f,.90f,.15f), rect(.10f,.17f,.90f,.28f), rect(.10f,.29f,.90f,.36f), qr(.63f,.58f,.36f), rect(.10f,.82f,.52f,.89f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SIDE_RAIL, ProfessionalCardComposition.SIGNATURE -> ProfessionalCardLayout(rect(.10f,.05f,.93f,.95f), rect(.16f,.09f,.88f,.15f), rect(.16f,.17f,.88f,.28f), rect(.16f,.29f,.88f,.36f), qr(.51f,.59f,.40f), rect(.22f,.81f,.80f,.88f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SPLIT_RIGHT, ProfessionalCardComposition.CENTER_POSTER -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.12f,.08f,.88f,.14f), rect(.12f,.16f,.88f,.27f), rect(.12f,.28f,.88f,.36f), qr(.50f,.59f,.41f), rect(.24f,.82f,.76f,.89f), FixedCardTextAlignment.CENTER)
        }
    }
}
