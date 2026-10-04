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

enum class QrCardDesignPreset(
    val label: String,
    val aspectPreset: QrCardAspectPreset
) {
    SQUARE_MINIMAL("Square Minimal", QrCardAspectPreset.SQUARE),
    SQUARE_PHOTO("Square Photo", QrCardAspectPreset.SQUARE),

    WIDE_EDITORIAL("Wide Editorial", QrCardAspectPreset.WIDE),
    WIDE_CINEMATIC("Wide Cinematic", QrCardAspectPreset.WIDE),

    CLASSIC_EXECUTIVE("Executive", QrCardAspectPreset.CARD),
    CLASSIC_LUXURY("Black & Gold", QrCardAspectPreset.CARD),

    PORTRAIT_EDITORIAL("Portrait Editorial", QrCardAspectPreset.PORTRAIT),
    PORTRAIT_CAMPAIGN("Portrait Campaign", QrCardAspectPreset.PORTRAIT),

    STORY_EDITORIAL("Story Editorial", QrCardAspectPreset.STORY),
    STORY_LUXURY("Story Luxury", QrCardAspectPreset.STORY);

    fun theme(): QrCardDesignTheme = when (this) {
        SQUARE_MINIMAL -> QrCardDesignTheme(
            backgroundArgb = 0xFFF5F2EA.toInt(),
            accentArgb = 0xFFB7893E.toInt(),
            titleArgb = 0xFF11151B.toInt(),
            bodyArgb = 0xFF5D5345.toInt(),
            photoOverlayAlpha = 0.06f
        )
        SQUARE_PHOTO -> QrCardDesignTheme(
            backgroundArgb = 0xFF10151C.toInt(),
            accentArgb = 0xFFD6A85F.toInt(),
            titleArgb = 0xFFFFFFFF.toInt(),
            bodyArgb = 0xFFE9D7B8.toInt(),
            photoOverlayAlpha = 0.42f
        )
        WIDE_EDITORIAL -> QrCardDesignTheme(
            backgroundArgb = 0xFF0C1118.toInt(),
            accentArgb = 0xFFD7A85C.toInt(),
            titleArgb = 0xFFF8FAFC.toInt(),
            bodyArgb = 0xFFB7C0CB.toInt(),
            photoOverlayAlpha = 0.18f
        )
        WIDE_CINEMATIC -> QrCardDesignTheme(
            backgroundArgb = 0xFF090D12.toInt(),
            accentArgb = 0xFFF0B85E.toInt(),
            titleArgb = 0xFFFFFFFF.toInt(),
            bodyArgb = 0xFFE8D8BA.toInt(),
            photoOverlayAlpha = 0.48f
        )
        CLASSIC_EXECUTIVE -> QrCardDesignTheme(
            backgroundArgb = 0xFFF2F5F9.toInt(),
            accentArgb = 0xFF1C4D8C.toInt(),
            titleArgb = 0xFF122033.toInt(),
            bodyArgb = 0xFF566579.toInt(),
            photoOverlayAlpha = 0.08f
        )
        CLASSIC_LUXURY -> QrCardDesignTheme(
            backgroundArgb = 0xFF0A0D11.toInt(),
            accentArgb = 0xFFD5A653.toInt(),
            titleArgb = 0xFFF7F4EE.toInt(),
            bodyArgb = 0xFFCDBE9F.toInt(),
            photoOverlayAlpha = 0.34f
        )
        PORTRAIT_EDITORIAL -> QrCardDesignTheme(
            backgroundArgb = 0xFFF7F2E9.toInt(),
            accentArgb = 0xFFC58842.toInt(),
            titleArgb = 0xFF18140F.toInt(),
            bodyArgb = 0xFF6A5948.toInt(),
            photoOverlayAlpha = 0.08f
        )
        PORTRAIT_CAMPAIGN -> QrCardDesignTheme(
            backgroundArgb = 0xFF121317.toInt(),
            accentArgb = 0xFFE7B85D.toInt(),
            titleArgb = 0xFFFFFFFF.toInt(),
            bodyArgb = 0xFFE9D9BB.toInt(),
            photoOverlayAlpha = 0.52f
        )
        STORY_EDITORIAL -> QrCardDesignTheme(
            backgroundArgb = 0xFF101319.toInt(),
            accentArgb = 0xFFE0AD59.toInt(),
            titleArgb = 0xFFFFFFFF.toInt(),
            bodyArgb = 0xFFD5C8B2.toInt(),
            photoOverlayAlpha = 0.30f
        )
        STORY_LUXURY -> QrCardDesignTheme(
            backgroundArgb = 0xFF070A0E.toInt(),
            accentArgb = 0xFFF1BE63.toInt(),
            titleArgb = 0xFFFDFBF7.toInt(),
            bodyArgb = 0xFFCDBA98.toInt(),
            photoOverlayAlpha = 0.56f
        )
    }
}

object QrCardDesignCatalog {
    fun forAspect(aspect: QrCardAspectPreset): List<QrCardDesignPreset> =
        QrCardDesignPreset.entries.filter { it.aspectPreset == aspect }

    fun defaultFor(aspect: QrCardAspectPreset): QrCardDesignPreset =
        forAspect(aspect).firstOrNull() ?: QrCardDesignPreset.CLASSIC_EXECUTIVE
}
