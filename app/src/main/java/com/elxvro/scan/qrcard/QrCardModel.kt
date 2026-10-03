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
    val payload: String,
    val title: String = "",
    val subtitle: String = "",
    val contactLine: String = "",
    val wifiSsid: String = "",
    val socialHandle: String = "",
    val eventDate: String = "",
    val eventLocation: String = "",
    val cardBackgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val accentArgb: Int = 0xFF0068F8.toInt(),
    val textArgb: Int = 0xFF111820.toInt(),
    val qrForegroundArgb: Int = 0xFF111820.toInt(),
    val qrBackgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val logoMode: LogoMode = LogoMode.ELXVRO,
    val logoScaleFraction: Float = 0.18f,
    val quietZoneModules: Int = 4,
    val qrPosition: QrPosition = QrPosition.CENTER,
    val cardAspectRatio: Float = 1.586f
)
