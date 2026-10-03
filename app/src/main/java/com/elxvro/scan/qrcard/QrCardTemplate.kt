package com.elxvro.scan.qrcard

enum class QrCardTemplate {
    MINIMAL,
    CORPORATE,
    WIFI,
    SOCIAL,
    EVENT;

    fun defaults(): QrCardDefaults = when (this) {
        MINIMAL -> QrCardDefaults(
            cardBackgroundArgb = 0xFFFFFFFF.toInt(),
            accentArgb = 0xFF0068F8.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
        CORPORATE -> QrCardDefaults(
            cardBackgroundArgb = 0xFF090E15.toInt(),
            accentArgb = 0xFF00A3FF.toInt(),
            textArgb = 0xFFF8FAFC.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.586f
        )
        WIFI -> QrCardDefaults(
            cardBackgroundArgb = 0xFFF7F8F9.toInt(),
            accentArgb = 0xFF00A86B.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.25f
        )
        SOCIAL -> QrCardDefaults(
            cardBackgroundArgb = 0xFF111821.toInt(),
            accentArgb = 0xFF8B5CF6.toInt(),
            textArgb = 0xFFF8FAFC.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.CENTER,
            cardAspectRatio = 1.0f
        )
        EVENT -> QrCardDefaults(
            cardBackgroundArgb = 0xFFFFFFFF.toInt(),
            accentArgb = 0xFFFF7A00.toInt(),
            textArgb = 0xFF111820.toInt(),
            qrForegroundArgb = 0xFF111820.toInt(),
            qrBackgroundArgb = 0xFFFFFFFF.toInt(),
            qrPosition = QrPosition.BOTTOM,
            cardAspectRatio = 0.75f
        )
    }
}

data class QrCardDefaults(
    val cardBackgroundArgb: Int,
    val accentArgb: Int,
    val textArgb: Int,
    val qrForegroundArgb: Int,
    val qrBackgroundArgb: Int,
    val logoScaleFraction: Float = 0.18f,
    val quietZoneModules: Int = 4,
    val qrPosition: QrPosition,
    val cardAspectRatio: Float
)
