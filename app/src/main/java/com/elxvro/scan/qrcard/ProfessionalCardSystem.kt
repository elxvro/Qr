package com.elxvro.scan.qrcard

import kotlin.math.min

enum class ProfessionalCardTier { FREE, PRO }

enum class ProfessionalCardScene(val categoryLabel: String) {
    FREE_ALPINE("Sisli Mavi"),
    FREE_COFFEE("Toprak & Kahve"),
    FREE_NEON_TECH("Gece Moru"),
    FREE_METRO("Petrol Tonları"),
    FREE_BOTANICAL("Adaçayı"),
    FREE_STUDIO("Taş & Gri"),
    FREE_STAGE("Mürdüm"),
    FREE_INTERIOR("Krem & Vizon"),
    FREE_CREATIVE("Şeftali"),
    FREE_FLOW("Derin Okyanus"),

    PRO_COAST("Lacivert Altın"),
    PRO_DINING("Espresso"),
    PRO_ARCHITECTURE("Grafit"),
    PRO_FASHION("Rose Taupe"),
    PRO_NIGHT("Gece Işığı"),
    PRO_WELLNESS("Zeytin & Bej"),
    PRO_RESORT("Arduvaz Mavi"),
    PRO_TECH_LAUNCH("Safir"),
    PRO_PROPERTY("Çelik Mavi"),
    PRO_BEAUTY("İpeksi Nude")
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
        SceneStyle(ProfessionalCardScene.FREE_ALPINE, "Kuzey Mavi", 0xFF20384C.toInt(), 0xFF7A96AB.toInt(), 0xFFDCEAF4.toInt(), 0xFFFFFFFF.toInt(), 0xFFE6EEF4.toInt(), 0xFF12202D.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_COFFEE, "Kakao Kum", 0xFF483329.toInt(), 0xFFA98772.toInt(), 0xFFF3E0C8.toInt(), 0xFFFFFAF6.toInt(), 0xFFF2E5D9.toInt(), 0xFF2E2018.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_NEON_TECH, "Gece Lavanta", 0xFF221D39.toInt(), 0xFF6B6499.toInt(), 0xFFD9D5F2.toInt(), 0xFFFFFFFF.toInt(), 0xFFE7E4F7.toInt(), 0xFF18142A.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_METRO, "Petrol Sis", 0xFF18313C.toInt(), 0xFF5A7D85.toInt(), 0xFFD7E6E8.toInt(), 0xFFFFFFFF.toInt(), 0xFFDDE9EB.toInt(), 0xFF15242A.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.FREE_BOTANICAL, "Adaçayı Bahçe", 0xFF304239.toInt(), 0xFF91A88C.toInt(), 0xFFE8F0E4.toInt(), 0xFFFFFFFF.toInt(), 0xFFE5EEE2.toInt(), 0xFF1E2A22.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_STUDIO, "Taş Gri", 0xFFCFCCC6.toInt(), 0xFF9B9B97.toInt(), 0xFFF5F2ED.toInt(), 0xFF202329.toInt(), 0xFF565A63.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_STAGE, "Mürdüm Pus", 0xFF41263F.toInt(), 0xFF8C6691.toInt(), 0xFFF1DFEF.toInt(), 0xFFFFFFFF.toInt(), 0xFFEEDFF0.toInt(), 0xFF241626.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.FREE_INTERIOR, "Krem Vizon", 0xFFF2ECE2.toInt(), 0xFFD0C0AD.toInt(), 0xFFFBF7F1.toInt(), 0xFF2A261F.toInt(), 0xFF6A6156.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_CREATIVE, "Şeftali Toz", 0xFFF0C7B2.toInt(), 0xFFD98A74.toInt(), 0xFFFFF0E8.toInt(), 0xFF3B2520.toInt(), 0xFF70463C.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.FREE_FLOW, "Derin Okyanus", 0xFF1B3047.toInt(), 0xFF5E7D99.toInt(), 0xFFDDE7F0.toInt(), 0xFFFFFFFF.toInt(), 0xFFE4EDF4.toInt(), 0xFF132130.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE)
    )

    private val proStyles = listOf(
        SceneStyle(ProfessionalCardScene.PRO_COAST, "Lacivert Altın", 0xFF132737.toInt(), 0xFF4F6B7E.toInt(), 0xFFE2C28B.toInt(), 0xFFFFFCF8.toInt(), 0xFFE7DAC0.toInt(), 0xFF101820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_DINING, "Espresso Gold", 0xFF241713.toInt(), 0xFF6A4A3D.toInt(), 0xFFE3C18F.toInt(), 0xFFFFFBF7.toInt(), 0xFFEEDFCC.toInt(), 0xFF17110D.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_ARCHITECTURE, "Grafit İnci", 0xFF20262D.toInt(), 0xFF697887.toInt(), 0xFFE4E8EC.toInt(), 0xFFFFFFFF.toInt(), 0xFFE3E8ED.toInt(), 0xFF11161A.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_FASHION, "Rose Taupe", 0xFF5A4343.toInt(), 0xFFB58D86.toInt(), 0xFFF1DDD5.toInt(), 0xFFFFFBF8.toInt(), 0xFFF1E2DD.toInt(), 0xFF251A18.toInt(), ProfessionalPanelStyle.OUTLINE, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.PRO_NIGHT, "Gece Işığı", 0xFF1C1530.toInt(), 0xFF5D447A.toInt(), 0xFFE6D4FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFEADEF7.toInt(), 0xFF181226.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.PRO_WELLNESS, "Zeytin Bej", 0xFF6E7864.toInt(), 0xFFD1C5AE.toInt(), 0xFFF5F1E7.toInt(), 0xFF1B231B.toInt(), 0xFF4E594D.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN),
        SceneStyle(ProfessionalCardScene.PRO_RESORT, "Arduvaz Sahil", 0xFF253748.toInt(), 0xFF7D95A6.toInt(), 0xFFE8D5B3.toInt(), 0xFFFFFCF8.toInt(), 0xFFE9E0CF.toInt(), 0xFF101820.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_TECH_LAUNCH, "Safir Sis", 0xFF1A2441.toInt(), 0xFF556C9C.toInt(), 0xFFDDE6FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFE5EBFA.toInt(), 0xFF121A2D.toInt(), ProfessionalPanelStyle.GLASS, ProfessionalQrPlateStyle.BORDERED),
        SceneStyle(ProfessionalCardScene.PRO_PROPERTY, "Çelik Mavi", 0xFF2C3946.toInt(), 0xFF71879C.toInt(), 0xFFE7E8EA.toInt(), 0xFFFFFFFF.toInt(), 0xFFE7ECF0.toInt(), 0xFF131A20.toInt(), ProfessionalPanelStyle.SOLID, ProfessionalQrPlateStyle.INK_EDGE),
        SceneStyle(ProfessionalCardScene.PRO_BEAUTY, "İpeksi Nude", 0xFFE8DDD4.toInt(), 0xFFCDB6A9.toInt(), 0xFFFFF6F0.toInt(), 0xFF322720.toInt(), 0xFF6E6056.toInt(), 0xFFFFFFFF.toInt(), ProfessionalPanelStyle.SOFT, ProfessionalQrPlateStyle.CLEAN)
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
            ProfessionalCardComposition.TOP_FEATURE, ProfessionalCardComposition.SPLIT_LEFT -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.12f,.52f,.88f,.57f), rect(.12f,.59f,.88f,.68f), rect(.12f,.70f,.88f,.76f), qr(.50f,.22f,.34f), rect(.24f,.80f,.76f,.87f), FixedCardTextAlignment.CENTER)
            ProfessionalCardComposition.HERO_BAND, ProfessionalCardComposition.BOTTOM_DOCK -> ProfessionalCardLayout(rect(.07f,.46f,.93f,.95f), rect(.12f,.51f,.88f,.57f), rect(.12f,.59f,.88f,.69f), rect(.12f,.70f,.88f,.77f), qr(.50f,.20f,.32f), rect(.24f,.84f,.76f,.90f), FixedCardTextAlignment.CENTER)
            ProfessionalCardComposition.FLOATING_CORNER, ProfessionalCardComposition.DIAGONAL_EDITORIAL -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.10f,.09f,.90f,.15f), rect(.10f,.17f,.90f,.28f), rect(.10f,.29f,.90f,.36f), qr(.63f,.58f,.36f), rect(.10f,.82f,.52f,.89f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SIDE_RAIL, ProfessionalCardComposition.SIGNATURE -> ProfessionalCardLayout(rect(.10f,.05f,.93f,.95f), rect(.16f,.09f,.88f,.15f), rect(.16f,.17f,.88f,.28f), rect(.16f,.29f,.88f,.36f), qr(.51f,.59f,.40f), rect(.22f,.81f,.80f,.88f), FixedCardTextAlignment.LEFT)
            ProfessionalCardComposition.SPLIT_RIGHT, ProfessionalCardComposition.CENTER_POSTER -> ProfessionalCardLayout(rect(.07f,.05f,.93f,.95f), rect(.12f,.08f,.88f,.14f), rect(.12f,.16f,.88f,.27f), rect(.12f,.28f,.88f,.36f), qr(.50f,.59f,.41f), rect(.24f,.82f,.76f,.89f), FixedCardTextAlignment.CENTER)
        }
    }
}
