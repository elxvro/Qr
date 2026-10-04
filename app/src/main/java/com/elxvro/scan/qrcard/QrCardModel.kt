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

data class QrCardModel(
    val template: QrCardTemplate = QrCardTemplate.MINIMAL,
    val designPreset: QrCardDesignPreset? = null,
    val backgroundPresetId: String = FixedCardLibrary.defaultPro.id,
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
    val cardAspectRatio: Float = 1.586f
)
