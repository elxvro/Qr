package com.elxvro.scan.qrcard

enum class LogoMode {
    ELXVRO,
    CUSTOM,
    NONE
}

enum class QrPosition {
    TOP,
    CENTER,
    BOTTOM
}

enum class CardFontStyle {
    SANS,
    SERIF,
    MONO,
    SCRIPT
}

enum class CardTextAlignment {
    LEFT,
    CENTER,
    RIGHT
}

data class QrCardModel(
    val template: QrCardTemplate = QrCardTemplate.MINIMAL,
    val designPreset: QrCardDesignPreset? = null,
    val backgroundPresetId: String = ProfessionalCardCatalog.defaultPro.id,
    val payload: String,
    val brandText: String = "ELXVRO",
    val title: String = "",
    val descriptionText: String = "",
    val ctaText: String = "TARA",
    val subtitle: String = "",
    val contactLine: String = "",
    val wifiSsid: String = "",
    val socialHandle: String = "",
    val eventDate: String = "",
    val eventLocation: String = "",
    val cardBackgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val accentArgb: Int = 0xFF0068F8.toInt(),
    val textArgb: Int = 0xFF111820.toInt(),
    val brandTextArgb: Int? = null,
    val bodyTextArgb: Int? = null,
    val ctaTextArgb: Int? = null,
    val qrForegroundArgb: Int = 0xFF111820.toInt(),
    val qrBackgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val logoMode: LogoMode = LogoMode.ELXVRO,
    val logoScaleFraction: Float = 0.18f,
    val quietZoneModules: Int = 4,
    val qrPosition: QrPosition = QrPosition.CENTER,
    val cardAspectRatio: Float = 1.586f,
    val freeformEnabled: Boolean = false,
    val brandAnchorX: Float = 0.22f,
    val brandAnchorY: Float = 0.17f,
    val titleAnchorX: Float = 0.24f,
    val titleAnchorY: Float = 0.31f,
    val bodyAnchorX: Float = 0.24f,
    val bodyAnchorY: Float = 0.49f,
    val qrAnchorX: Float = 0.77f,
    val qrAnchorY: Float = 0.43f,
    val ctaAnchorX: Float = 0.77f,
    val ctaAnchorY: Float = 0.77f,
    val brandScale: Float = 1.0f,
    val titleScale: Float = 1.0f,
    val bodyScale: Float = 1.0f,
    val qrScale: Float = 1.0f,
    val ctaScale: Float = 1.0f,
    val textAlignment: CardTextAlignment = CardTextAlignment.LEFT,
    val fontStyle: CardFontStyle = CardFontStyle.SANS
)
