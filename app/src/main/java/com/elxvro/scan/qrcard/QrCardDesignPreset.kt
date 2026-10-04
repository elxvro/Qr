package com.elxvro.scan.qrcard

data class QrCardDesignTheme(
    val backgroundArgb: Int,
    val accentArgb: Int,
    val titleArgb: Int,
    val bodyArgb: Int,
    val qrForegroundArgb: Int = 0xFF0A0D12.toInt(),
    val qrBackgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val photoOverlayAlpha: Float
)

enum class QrCardBackgroundMode(val label: String) {
    FIXED_BACKGROUND("Sabit Arka Plan"),
    FULL_BACKGROUND("Değişen Arka Plan")
}

enum class QrCardDesignPreset(
    val label: String,
    val aspectPreset: QrCardAspectPreset,
    val backgroundMode: QrCardBackgroundMode
) {
    SQUARE_MINIMAL("1:1 Sabit", QrCardAspectPreset.SQUARE, QrCardBackgroundMode.FIXED_BACKGROUND),
    SQUARE_PHOTO("1:1 Değişen", QrCardAspectPreset.SQUARE, QrCardBackgroundMode.FULL_BACKGROUND),

    WIDE_EDITORIAL("16:9 Sabit", QrCardAspectPreset.WIDE, QrCardBackgroundMode.FIXED_BACKGROUND),
    WIDE_CINEMATIC("16:9 Değişen", QrCardAspectPreset.WIDE, QrCardBackgroundMode.FULL_BACKGROUND),

    CLASSIC_EXECUTIVE("Kart Sabit", QrCardAspectPreset.CARD, QrCardBackgroundMode.FIXED_BACKGROUND),
    CLASSIC_LUXURY("Kart Değişen", QrCardAspectPreset.CARD, QrCardBackgroundMode.FULL_BACKGROUND),

    PORTRAIT_EDITORIAL("4:5 Sabit", QrCardAspectPreset.PORTRAIT, QrCardBackgroundMode.FIXED_BACKGROUND),
    PORTRAIT_CAMPAIGN("4:5 Değişen", QrCardAspectPreset.PORTRAIT, QrCardBackgroundMode.FULL_BACKGROUND),

    STORY_EDITORIAL("9:16 Sabit", QrCardAspectPreset.STORY, QrCardBackgroundMode.FIXED_BACKGROUND),
    STORY_LUXURY("9:16 Değişen", QrCardAspectPreset.STORY, QrCardBackgroundMode.FULL_BACKGROUND);

    val fullBleedPhoto: Boolean
        get() = backgroundMode == QrCardBackgroundMode.FULL_BACKGROUND

    fun theme(): QrCardDesignTheme = when (backgroundMode) {
        QrCardBackgroundMode.FIXED_BACKGROUND -> fixedPremiumTheme()
        QrCardBackgroundMode.FULL_BACKGROUND -> fullBackgroundTheme()
    }
}

object QrCardDesignCatalog {
    fun forAspect(aspect: QrCardAspectPreset): List<QrCardDesignPreset> =
        QrCardDesignPreset.entries.filter { it.aspectPreset == aspect }

    fun forAspectAndMode(
        aspect: QrCardAspectPreset,
        mode: QrCardBackgroundMode
    ): List<QrCardDesignPreset> =
        QrCardDesignPreset.entries.filter {
            it.aspectPreset == aspect && it.backgroundMode == mode
        }

    fun defaultFor(aspect: QrCardAspectPreset): QrCardDesignPreset =
        defaultFor(aspect, QrCardBackgroundMode.FIXED_BACKGROUND)

    fun defaultFor(
        aspect: QrCardAspectPreset,
        mode: QrCardBackgroundMode
    ): QrCardDesignPreset =
        forAspectAndMode(aspect, mode).firstOrNull()
            ?: QrCardDesignPreset.CLASSIC_EXECUTIVE
}

private fun fixedPremiumTheme() = QrCardDesignTheme(
    backgroundArgb = 0xFF090C10.toInt(),
    accentArgb = 0xFFE2B15E.toInt(),
    titleArgb = 0xFFF9F7F2.toInt(),
    bodyArgb = 0xFFD2C3A8.toInt(),
    qrForegroundArgb = 0xFF0A0D12.toInt(),
    qrBackgroundArgb = 0xFFFFFFFF.toInt(),
    photoOverlayAlpha = 0.18f
)

private fun fullBackgroundTheme() = QrCardDesignTheme(
    backgroundArgb = 0xFF080B0F.toInt(),
    accentArgb = 0xFFE7B45F.toInt(),
    titleArgb = 0xFFFFFFFF.toInt(),
    bodyArgb = 0xFFE2D3B7.toInt(),
    qrForegroundArgb = 0xFF0A0D12.toInt(),
    qrBackgroundArgb = 0xFFFFFFFF.toInt(),
    photoOverlayAlpha = 0.48f
)
